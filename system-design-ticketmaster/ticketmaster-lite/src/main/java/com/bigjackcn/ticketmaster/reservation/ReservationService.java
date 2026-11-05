package com.bigjackcn.ticketmaster.reservation;

import com.bigjackcn.ticketmaster.event.Event;
import com.bigjackcn.ticketmaster.event.EventService;
import com.bigjackcn.ticketmaster.order.Order;
import com.bigjackcn.ticketmaster.order.OrderRepository;
import com.bigjackcn.ticketmaster.order.OrderStatus;
import com.bigjackcn.ticketmaster.reservation.dto.ConfirmReservationResponse;
import com.bigjackcn.ticketmaster.reservation.dto.CreateReservationResponse;
import com.bigjackcn.ticketmaster.ticket.TicketRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class ReservationService {
    private static final int HOLD_MINUTES = 10;

    private final ReservationRepository reservationRepository;
    private final TicketRepository ticketRepository;
    private final EventService eventService;
    private final OrderRepository orderRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              TicketRepository ticketRepository,
                              EventService eventService,
                              OrderRepository orderRepository) {
        this.reservationRepository = reservationRepository;
        this.ticketRepository = ticketRepository;
        this.eventService = eventService;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public CreateReservationResponse create(Long eventId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be > 0");
        }

        Event event = eventService.getById(eventId);

        Reservation reservation = new Reservation();
        reservation.setUserId(1L);
        reservation.setEventId(eventId);
        reservation.setStatus(ReservationStatus.ACTIVE);
        reservation.setQuantity(quantity);
        reservation.setCreatedAt(Instant.now());
        reservation.setExpiresAt(Instant.now().plus(HOLD_MINUTES, ChronoUnit.MINUTES));
        reservation.setTotalCents(event.getPriceCents() * quantity);
        reservation = reservationRepository.save(reservation);

        int affected = ticketRepository.reserveTickets(eventId, reservation.getId(), quantity);
        if (affected != quantity) {
            throw new NotEnoughTicketsException(eventId, quantity);
        }
        return new CreateReservationResponse(
                reservation.getId(),
                reservation.getStatus().name(),
                reservation.getQuantity(),
                reservation.getExpiresAt()
        );
    }

    @Transactional
    public ConfirmReservationResponse confirm(Long reservationId, String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("Idempotency-Key header is required");
        }

        // 1. 幂等：先查
        var existing = orderRepository.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            return toConfirmResponse(existing.get());
        }

        // 2. 加载 + 校验
        Reservation r = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(reservationId));

        if (r.getStatus() != ReservationStatus.ACTIVE) {
            throw new ReservationNotActiveException(reservationId, r.getStatus());
        }
        if (r.getExpiresAt().isBefore(Instant.now())) {
            throw new ReservationExpiredException(reservationId);
        }

        // 3. tickets RESERVED -> SOLD
        int affected = ticketRepository.markSold(reservationId);
        if (affected != r.getQuantity()) {
            throw new IllegalStateException(
                    "Ticket state mismatch for reservation " + reservationId);
        }

        // 4. reservation -> CONFIRMED
        r.setStatus(ReservationStatus.CONFIRMED);
        reservationRepository.save(r);

        // 5. create order
        Order order = new Order();
        order.setReservationId(reservationId);
        order.setUserId(r.getUserId());
        order.setStatus(OrderStatus.PAID);
        order.setTotalCents(r.getTotalCents());
        order.setIdempotencyKey(idempotencyKey);
        order.setCreatedAt(Instant.now());
        order.setPaidAt(Instant.now());
        order = orderRepository.save(order);

        return toConfirmResponse(order);
    }

    private ConfirmReservationResponse toConfirmResponse(Order order) {
        return new ConfirmReservationResponse(
                order.getId(),
                order.getReservationId(),
                "CONFIRMED",
                order.getTotalCents(),
                "CAD",
                order.getPaidAt()
        );
    }
}

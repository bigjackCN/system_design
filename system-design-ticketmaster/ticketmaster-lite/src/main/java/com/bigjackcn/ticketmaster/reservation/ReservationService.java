package com.bigjackcn.ticketmaster.reservation;

import com.bigjackcn.ticketmaster.event.Event;
import com.bigjackcn.ticketmaster.event.EventService;
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

    public ReservationService(ReservationRepository reservationRepository,
                              TicketRepository ticketRepository,
                              EventService eventService) {
        this.reservationRepository = reservationRepository;
        this.ticketRepository = ticketRepository;
        this.eventService = eventService;
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
}

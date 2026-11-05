package com.bigjackcn.ticketmaster.reservation;

import com.bigjackcn.ticketmaster.reservation.dto.ConfirmReservationResponse;
import com.bigjackcn.ticketmaster.reservation.dto.CreateReservationRequest;
import com.bigjackcn.ticketmaster.reservation.dto.CreateReservationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateReservationResponse create(@RequestBody CreateReservationRequest request) {
        return reservationService.create(request.eventId(), request.quantity());
    }

    @PostMapping("/{id}/confirm")
    public ConfirmReservationResponse confirm(
            @PathVariable Long id,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {
        return reservationService.confirm(id, idempotencyKey);
    }
}

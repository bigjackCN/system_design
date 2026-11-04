package com.bigjackcn.ticketmaster.reservation.dto;

import java.time.Instant;

public record CreateReservationResponse(
        Long reservationId,
        String status,
        int quantity,
        Instant expiresAt
) {
}

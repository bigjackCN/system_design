package com.bigjackcn.ticketmaster.reservation.dto;

import java.time.Instant;

public record ConfirmReservationResponse(
        Long orderId,
        Long reservationId,
        String status,
        long totalCents,
        String currency,
        Instant paidAt
) {
}
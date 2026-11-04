package com.bigjackcn.ticketmaster.reservation.dto;

public record CreateReservationRequest(Long eventId, int quantity) {
}

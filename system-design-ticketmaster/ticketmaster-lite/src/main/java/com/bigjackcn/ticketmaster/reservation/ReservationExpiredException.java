package com.bigjackcn.ticketmaster.reservation;

public class ReservationExpiredException extends RuntimeException {
    public ReservationExpiredException(Long id) {
        super("Reservation " + id + " has expired");
    }
}
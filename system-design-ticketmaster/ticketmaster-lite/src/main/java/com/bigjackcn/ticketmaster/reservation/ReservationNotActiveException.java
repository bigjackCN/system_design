package com.bigjackcn.ticketmaster.reservation;

public class ReservationNotActiveException extends RuntimeException {
    public ReservationNotActiveException(Long id, ReservationStatus status) {
        super("Reservation " + id + " is not ACTIVE (status=" + status + ")");
    }
}
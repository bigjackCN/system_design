package com.bigjackcn.ticketmaster.reservation;

public class NotEnoughTicketsException extends RuntimeException {
    public NotEnoughTicketsException(Long eventId, int requested) {
        super("Not enough tickets available for event " + eventId
                + " (requested " + requested + ")");
    }
}

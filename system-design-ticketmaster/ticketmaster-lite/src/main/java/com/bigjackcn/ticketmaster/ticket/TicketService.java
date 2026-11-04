package com.bigjackcn.ticketmaster.ticket;

import org.springframework.stereotype.Service;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public long countAvailable(Long eventId) {
        return ticketRepository.countByEventIdAndStatus(eventId, TicketStatus.AVAILABLE);
    }
}

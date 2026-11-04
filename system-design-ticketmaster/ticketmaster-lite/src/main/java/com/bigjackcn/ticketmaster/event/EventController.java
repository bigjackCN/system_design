package com.bigjackcn.ticketmaster.event;

import com.bigjackcn.ticketmaster.event.dto.EventDetailResponse;
import com.bigjackcn.ticketmaster.ticket.TicketService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {
    private final EventService eventService;
    private final TicketService ticketService;

    public EventController(EventService eventService, TicketService ticketService) {
        this.eventService = eventService;
        this.ticketService = ticketService;
    }

    @GetMapping("/{id}")
    public EventDetailResponse getEvent(@PathVariable Long id) {
        Event event = eventService.getById(id);
        long available = ticketService.countAvailable(id);
        return new EventDetailResponse(
                event.getId(),
                event.getName(),
                event.getVenue(),
                event.getStartAt(),
                event.getPriceCents(),
                event.getCurrency(),
                event.getCapacity(),
                available
        );
    }
}

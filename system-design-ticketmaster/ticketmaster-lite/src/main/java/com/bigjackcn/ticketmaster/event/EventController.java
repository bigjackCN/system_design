package com.bigjackcn.ticketmaster.event;

import com.bigjackcn.ticketmaster.event.dto.EventDetailResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/{id}")
    public EventDetailResponse getEvent(@PathVariable Long id) {
        Event event = eventService.getById(id);
        return new EventDetailResponse(event.getId(), event.getName(), event.getVenue(), event.getStartAt());
    }
}

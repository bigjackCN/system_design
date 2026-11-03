package com.bigjackcn.ticketmaster.event;

import com.bigjackcn.ticketmaster.common.EventNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class EventService {
    public final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Event getById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));
    }
}

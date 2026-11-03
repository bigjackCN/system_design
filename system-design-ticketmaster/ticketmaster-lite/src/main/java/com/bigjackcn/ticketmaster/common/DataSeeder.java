package com.bigjackcn.ticketmaster.common;

import com.bigjackcn.ticketmaster.event.Event;
import com.bigjackcn.ticketmaster.event.EventRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class DataSeeder implements CommandLineRunner {
    private final EventRepository eventRepository;

    public DataSeeder(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }


    @Override
    public void run(String... args) throws Exception {
        if (eventRepository.count() > 0) {
            return;
        }

        Event event = new Event();
        event.setName("Coldplay World Tour");
        event.setVenue("Rogers Centre");
        event.setStartAt(Instant.now().plus(30, ChronoUnit.DAYS));
        eventRepository.save(event);

        System.out.println("Seeded 1 event with id: " + event.getId());
    }
}

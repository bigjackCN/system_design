package com.bigjackcn.ticketmaster.common;

import com.bigjackcn.ticketmaster.event.Event;
import com.bigjackcn.ticketmaster.event.EventRepository;
import com.bigjackcn.ticketmaster.ticket.Ticket;
import com.bigjackcn.ticketmaster.ticket.TicketRepository;
import com.bigjackcn.ticketmaster.ticket.TicketStatus;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class DataSeeder implements CommandLineRunner {
    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;

    public DataSeeder(EventRepository eventRepository, TicketRepository ticketRepository) {
        this.eventRepository = eventRepository;
        this.ticketRepository = ticketRepository;
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
        event.setPriceCents(15000L);
        event.setCurrency("CAD");
        event.setCapacity(10);
        eventRepository.save(event);

        for (int i = 0; i < event.getCapacity(); i++) {
            Ticket ticket = new Ticket();
            ticket.setEventId(event.getId());
            ticket.setStatus(TicketStatus.AVAILABLE);
            ticket.setUpdatedAt(Instant.now());
            ticketRepository.save(ticket);
        }

        System.out.println("Seeded event with id= " + event.getId()
                + " with " + event.getCapacity() + " tickets");
    }
}

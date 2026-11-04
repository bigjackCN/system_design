package com.bigjackcn.ticketmaster.ticket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    long countByEventIdAndStatus(long eventId, TicketStatus status);

    @Modifying
    @Query(value = """
            UPDATE tickets
            SET status = 'RESERVED',
                        reservation_id = :reservationId,
                        update_at = now(),
                        version = version + 1
            WHERE id IN (
                        SELECT id FROM tickets
                        WHERE event_id = :eventId 
                            AND status = 'AVAILABLE'
                        ORDER BY id 
                        LIMIT :quantity
            )
            AND status = 'AVAILABLE'
            """, nativeQuery = true)
    int reserveTickets(@Param("eventId") Long eventId,
                       @Param("reservationId") Long reservationId,
                       @Param("quantity") int quantity);
}

package com.bigjackcn.ticketmaster.ticket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    long countByEventIdAndStatus(long eventId, TicketStatus status);

    @Modifying
    @Query(value = """
            UPDATE tickets
            SET status = 'RESERVED',
                        reservation_id = :reservationId,
                        updated_at = now(),
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

    @Modifying
    @Query(value = """
        UPDATE tickets
        SET status = 'SOLD',
            updated_at = now(),
            version = version + 1
        WHERE reservation_id = :reservationId
          AND status = 'RESERVED'
        """, nativeQuery = true)
    int markSold(@Param("reservationId") Long reservationId);
}

package com.bigjackcn.ticketmaster.event.dto;

import java.time.Instant;

public record EventDetailResponse(Long eventId, String name, String venue, Instant startAt) {
}

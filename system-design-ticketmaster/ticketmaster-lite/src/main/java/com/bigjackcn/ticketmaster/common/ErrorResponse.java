package com.bigjackcn.ticketmaster.common;

public record ErrorResponse(String code, String message, String requestId) {
}

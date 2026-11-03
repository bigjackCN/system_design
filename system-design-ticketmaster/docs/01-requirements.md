# Requirements

## Scope Decision
- Phase 1 supports **General Admission (GA) ticket tiers**, not seat-level selection.
- Seat-map based selection is a future extension (see Out of Scope).

## Functional Requirements
- FR1. Users can search events by keyword, city, and date range.
- FR2. Users can view an event detail: event info, venue, performer(s), and available ticket tiers.
- FR3. Users can create a reservation for one or more tickets in a tier.
- FR4. A reservation holds tickets for **10 minutes**. Unpaid reservations are released automatically after expiry.
- FR5. Users can confirm a reservation with payment within the hold window; this creates an order.
- FR6. Users can view their own orders.
- FR7. Expired or cancelled reservations release their tickets back to the pool.

## Non-Functional Requirements
- NFR1. **No overselling**: a ticket must never be sold twice.
- NFR2. Reservation creation and confirmation must be strongly consistent at the database level.
- NFR3. Search and event browsing are read-heavy and can be eventually consistent; availability is preferred over freshness.
- NFR4. Read traffic >> write traffic.
- NFR5. Must tolerate traffic spikes from popular on-sales (10x–100x normal load) without overselling.
- NFR6. Payment confirmation must be idempotent (duplicate requests return the same order).
- NFR7. Phase 1 runs as a modular monolith; the design should allow later extraction of Search and Booking.

## Out of Scope
- Seat-level selection and seat maps
- Real payment integration (Stripe is mocked)
- Multi-region disaster recovery
- Production-grade observability, alerting, and SLOs
- GDPR / privacy compliance
- Admin/back-office tooling
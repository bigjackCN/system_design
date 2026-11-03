## 2026-11-02

### Done
- Decided Phase 1 scope: GA ticket tiers, modular monolith, no seat selection.
- Set up Spring Boot 4.0.9 + Java 21 + Maven + PostgreSQL 16 (Docker).
- First vertical slice: GET /api/v1/events/{id} works end to end.
- Added GlobalExceptionHandler mapping EventNotFoundException -> 404.

### Decisions
- Package by feature (event / reservation / order / common), not by layer.
- Use `@Table(name = "events")` to keep DB naming consistent with docs.
- Seed data with CommandLineRunner instead of data.sql to avoid ordering issues.

### Next
- Add Ticket entity with status, version, and reservation_id.
- Implement POST /api/v1/reservations with conditional UPDATE to prevent overselling.
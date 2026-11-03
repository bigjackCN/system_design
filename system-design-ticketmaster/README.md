# System Design: Ticketmaster Lite

A hands-on system design and backend project: build a simplified Ticketmaster
that **never oversells a ticket under concurrency**, with design docs and
working Spring Boot code side by side.

Target level: junior software engineer interview preparation.

---

## Scope (Phase 1)

**In scope**
- General Admission (GA) ticket tiers (no seat selection)
- View event details and tier availability
- Reserve tickets with a 10-minute hold
- Confirm reservation and create an order (payment mocked)
- Auto-release expired reservations

**Out of scope (deliberately)**
- Seat maps, real payments, microservices, multi-region, admin back office

---

## Status

| Area                       | Status        |
|----------------------------|---------------|
| Design docs                | ✅ Done        |
| Project scaffold           | ✅ Done        |
| `GET /api/v1/events/{id}`  | ✅ Done        |
| Global error handling      | ✅ Done        |
| Ticket entity & inventory  | 🚧 In progress |
| `POST /reservations`       | ⏳ Planned     |
| Confirm + expiry scheduler | ⏳ Planned     |

---

## Architecture

Phase 1 is a **modular monolith**, split by feature so modules can be
extracted into services later without a rewrite.

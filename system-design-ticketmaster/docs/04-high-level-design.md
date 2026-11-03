# High-Level Design (Phase 1: Modular Monolith)

## Goals
- Ship a working booking flow with no overselling.
- Keep deployment simple: one Spring Boot app, one PostgreSQL.
- Make extension points explicit so Search and Booking can be split later.

## Architecture

```mermaid
flowchart LR
    C[Client] --> API[Spring Boot Monolith]

    subgraph API[Spring Boot Monolith]
        AUTH[Auth Module]
        EVT[Event & Search Module]
        RES[Reservation Module]
        ORD[Order & Payment Module]
        SCH[Expiry Scheduler]
    end

    EVT --> DB[(PostgreSQL)]
    RES --> DB
    ORD --> DB
    RES -. optional .-> RD[(Redis Lock / Cache)]
    ORD --> ST[Stripe Mock / Webhook]
    SCH --> DB
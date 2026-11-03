# Data Model

Phase 1 uses PostgreSQL. Seat-level selection is out of scope; tickets are grouped by tier.

## Tables

### events
| Column       | Type         | Notes                          |
|--------------|--------------|--------------------------------|
| id           | UUID PK      |                                |
| venue_id     | UUID FK      | -> venues.id                   |
| name         | VARCHAR(255) |                                |
| description  | TEXT         |                                |
| start_at     | TIMESTAMPTZ  |                                |
| status       | VARCHAR(32)  | DRAFT, PUBLISHED, CANCELLED    |
| created_at   | TIMESTAMPTZ  |                                |

### event_performers
| Column        | Type      | Notes              |
|---------------|-----------|--------------------|
| event_id      | UUID FK   | -> events.id       |
| performer_id  | UUID FK   | -> performers.id   |
| PK            | (event_id, performer_id) |       |

### venues
| Column    | Type         | Notes                          |
|-----------|--------------|--------------------------------|
| id        | UUID PK      |                                |
| name      | VARCHAR(255) |                                |
| city      | VARCHAR(128) |                                |
| country   | VARCHAR(64)  |                                |
| seat_map  | JSONB        | Static layout, future use      |

### performers
| Column  | Type         | Notes |
|---------|--------------|-------|
| id      | UUID PK      |       |
| name    | VARCHAR(255) |       |

### ticket_tiers
| Column      | Type         | Notes                                  |
|-------------|--------------|----------------------------------------|
| id          | UUID PK      |                                        |
| event_id    | UUID FK      | -> events.id                           |
| name        | VARCHAR(128) | e.g. "GA Floor", "VIP"                 |
| price_cents | BIGINT       | Store money in cents                   |
| currency    | CHAR(3)      | e.g. "CAD"                             |
| capacity    | INT          | Total tickets in this tier             |

### tickets
| Column          | Type         | Notes                                         |
|-----------------|--------------|-----------------------------------------------|
| id              | UUID PK      |                                               |
| event_id        | UUID FK      | -> events.id                                  |
| tier_id         | UUID FK      | -> ticket_tiers.id                            |
| seat_label      | VARCHAR(64)  | Nullable in GA phase                          |
| status          | VARCHAR(16)  | AVAILABLE, RESERVED, SOLD, RELEASED           |
| reservation_id  | UUID FK NULL | -> reservations.id                            |
| version         | BIGINT       | Optimistic locking                            |
| updated_at      | TIMESTAMPTZ  |                                               |

Indexes:
- `(event_id, tier_id, status)` — fast availability lookup
- `(reservation_id)` — release flow

### reservations
| Column      | Type         | Notes                                          |
|-------------|--------------|------------------------------------------------|
| id          | UUID PK      |                                                |
| user_id     | UUID         |                                                |
| event_id    | UUID FK      | -> events.id                                   |
| status      | VARCHAR(16)  | ACTIVE, CONFIRMED, EXPIRED, CANCELLED          |
| expires_at  | TIMESTAMPTZ  | created_at + 10 minutes                        |
| created_at  | TIMESTAMPTZ  |                                                |

Index:
- `(status, expires_at)` — expiry scheduler scan

### reservation_items
| Column          | Type     | Notes                       |
|-----------------|----------|-----------------------------|
| reservation_id  | UUID FK  | -> reservations.id          |
| ticket_id       | UUID FK  | -> tickets.id               |
| price_cents     | BIGINT   | Snapshot of price at hold   |
| PK              | (reservation_id, ticket_id) |        |

### orders
| Column          | Type         | Notes                                |
|-----------------|--------------|--------------------------------------|
| id              | UUID PK      |                                      |
| reservation_id  | UUID FK      | -> reservations.id                   |
| user_id         | UUID         |                                      |
| status          | VARCHAR(16)  | PENDING, PAID, FAILED, REFUNDED      |
| total_cents     | BIGINT       |                                      |
| currency        | CHAR(3)      |                                      |
| created_at      | TIMESTAMPTZ  |                                      |
| paid_at         | TIMESTAMPTZ  |                                      |

### payments
| Column               | Type         | Notes                              |
|----------------------|--------------|------------------------------------|
| id                   | UUID PK      |                                    |
| order_id             | UUID FK      | -> orders.id                       |
| provider             | VARCHAR(32)  | "stripe"                           |
| provider_payment_id  | VARCHAR(128) |                                    |
| status               | VARCHAR(16)  | INITIATED, SUCCEEDED, FAILED       |
| idempotency_key      | VARCHAR(128) | Unique per order                   |
| created_at           | TIMESTAMPTZ  |                                    |

Unique: `(provider, provider_payment_id)`, `(order_id, idempotency_key)`

## Concurrency: Preventing Overselling

Reservation is a **conditional update**, not a read-then-write:

```sql
UPDATE tickets
SET status = 'RESERVED', reservation_id = :rid, version = version + 1, updated_at = now()
WHERE id IN (:ticketIds)
  AND status = 'AVAILABLE';
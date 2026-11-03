# API Design

## Core Entities
- Event
- Venue
- Performer
- Ticket
- Reservation
- Order
- Payment

## Conventions
- Base path: `/api/v1`
- Auth: `Authorization: Bearer <JWT>`
- All timestamps are ISO-8601 UTC.
- All write endpoints require an `Idempotency-Key` header where noted.

## Endpoints

### Search events
GET /api/v1/search?q={keyword}&city={city}&dateFrom={date}&dateTo={date}

Response 200:
{
  "items": [
    {
      "eventId": "evt_123",
      "name": "Coldplay World Tour",
      "city": "Toronto",
      "startAt": "2026-06-01T19:00:00Z",
      "minPrice": 8000,
      "currency": "CAD"
    }
  ],
  "page": 0,
  "size": 20,
  "total": 134
}

### Get event detail
GET /api/v1/events/{eventId}

Response 200:
{
  "eventId": "evt_123",
  "name": "Coldplay World Tour",
  "description": "...",
  "startAt": "2026-06-01T19:00:00Z",
  "venue": { "venueId": "ven_1", "name": "Rogers Centre", "city": "Toronto" },
  "performers": [ { "performerId": "prf_1", "name": "Coldplay" } ],
  "ticketTiers": [
    { "tierId": "tier_ga", "name": "GA Floor", "price": 15000, "currency": "CAD", "available": 120 }
  ]
}

### Create reservation (hold tickets)
POST /api/v1/reservations
Headers: Idempotency-Key: <uuid>
Body:
{
  "eventId": "evt_123",
  "tierId": "tier_ga",
  "quantity": 2
}

Response 201:
{
  "reservationId": "res_456",
  "status": "ACTIVE",
  "expiresAt": "2026-11-02T10:15:00Z",
  "items": [
    { "ticketId": "tkt_1", "tierId": "tier_ga", "price": 15000 },
    { "ticketId": "tkt_2", "tierId": "tier_ga", "price": 15000 }
  ],
  "totalAmount": 30000,
  "currency": "CAD"
}

### Confirm reservation (pay)
POST /api/v1/reservations/{reservationId}/confirm
Headers: Idempotency-Key: <uuid>
Body:
{
  "paymentMethodId": "pm_card_visa"
}

Response 200:
{
  "orderId": "ord_789",
  "reservationId": "res_456",
  "status": "CONFIRMED",
  "totalAmount": 30000,
  "currency": "CAD",
  "paidAt": "2026-11-02T10:08:12Z"
}

### Get reservation
GET /api/v1/reservations/{reservationId}

### List my orders
GET /api/v1/orders?page=0&size=20

### Stripe webhook (mock in Phase 1)
POST /api/v1/webhooks/stripe
Headers: Stripe-Signature: <sig>

## Error Responses

| HTTP | Code                     | Meaning                                          |
|------|--------------------------|--------------------------------------------------|
| 400  | INVALID_REQUEST          | Malformed body or missing fields                 |
| 401  | UNAUTHORIZED             | Missing or invalid token                         |
| 404  | EVENT_NOT_FOUND          | Event does not exist                             |
| 409  | TICKET_ALREADY_RESERVED  | Not enough tickets available in the tier         |
| 409  | RESERVATION_NOT_ACTIVE   | Reservation is not in ACTIVE state               |
| 410  | RESERVATION_EXPIRED      | Hold window has passed                           |
| 402  | PAYMENT_FAILED           | Payment provider declined                        |
| 429  | RATE_LIMITED             | Too many requests                                |

Error body:
{
  "code": "TICKET_ALREADY_RESERVED",
  "message": "Not enough tickets available for tier tier_ga",
  "requestId": "req_abc"
}

## Idempotency
- `POST /reservations` and `POST /reservations/{id}/confirm` require `Idempotency-Key`.
- Repeating the same key returns the original response, not a new reservation/order.
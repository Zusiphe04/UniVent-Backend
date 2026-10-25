# UniVent API contract

Base URL: `http://localhost:8080`. All application routes include `/api`; the
frontend's `VITE_API_URL` is only the origin (`http://localhost:8080`).
Authenticated requests use `Authorization: Bearer <access-token>`. Enum values
are strings and date/time fields are ISO 8601. No response includes a password
or password hash.

## Authentication

`POST /api/auth/register` is public and accepts shared fields `name`, `email`,
`password`, `phoneNumber`, and `role`. The primary email domain must be
`mycput.ac.za` or `cput.ac.za`; passwords must contain at least eight
characters. Organizer contact email
must be a valid `.ac.za` address. Organizer and administrator staff numbers are
unique across accounts. Organizer `organizationType` means the organization's
category/type, not the organizer's faculty or department. Role-specific JSON
fields are:

```json
{
  "name": "Example Student",
  "email": "student@mycput.ac.za",
  "password": "<user-supplied-password>",
  "phoneNumber": "0840000000",
  "role": "STUDENT",
  "studentNumber": "221234567",
  "faculty": "Applied Sciences",
  "department": "Chemistry",
  "yearOfStudy": 2
}
```

For `ORGANIZER`, send `staffNumber`, `organizationName`, `organizationType`,
and `contactEmail`. For `ADMIN`, send `staffNumber`. Public invite-free Admin
registration is permitted only when the local `demo` profile is explicitly
enabled. In the default/production configuration, also send `inviteCode`; it
must be valid and unused. Admin registration with the demo profile disabled
and no invite code returns `403`.

Admin registration request in local demo mode:

```json
{
  "name": "Example Administrator",
  "email": "admin@mycput.ac.za",
  "password": "<user-supplied-password>",
  "phoneNumber": "0840000000",
  "role": "ADMIN",
  "staffNumber": "STAFF-1234"
}
```

The terms checkbox is validated by the frontend and is not part of the API
request. No student, organizer, or invite-code fields are required for an Admin
in demo mode.

`POST /api/auth/login` accepts `{ "email": "...", "password": "..." }`.
Registration returns `201`; login returns `200`. Both return:

```json
{
  "token": "<issued-access-token>",
  "role": "STUDENT",
  "userId": "<user-id>",
  "name": "Example Student",
  "email": "student@mycput.ac.za",
  "accountStatus": "ACTIVE"
}
```

Organizer registration returns `accountStatus: "PENDING_APPROVAL"`; admin
registration returns `"ACTIVE"`. Disabled account login is `403`. Invalid role
or required-field validation is `400`; duplicate email/staff number is `409`;
missing, invalid, or used production admin invite is `403`.

Successful demo Admin registration returns the same shape with `"role":
"ADMIN"`, the issued JWT in `token`, and `"accountStatus": "ACTIVE"`.

## Events

| Method and path | Access | Request | Success response |
|---|---|---|---|
| `GET /api/events` | Any authenticated role | Optional `search`, `category`, `date=YYYY-MM-DD`, `status=APPROVED`; results always exclude unapproved events. | `200` array of event summaries. |
| `GET /api/events/{id}` | Any authenticated role | None | `200` event summary; `404` if missing or not approved. |
| `POST /api/events` | Active organizer | JSON shown below; organizer comes from JWT. | `201` event summary, with status `PENDING_APPROVAL`. |
| `PUT /api/events/{id}` | Active owning organizer | Same editable event JSON; organizer and status are ignored. | `200` event summary. |
| `PATCH /api/events/{id}/cancel` | Active owning organizer | No body | `200` event summary with status `CANCELLED`; record is retained. |
| `GET /api/events/{eventId}/bookings` | Owning organizer or admin | None | `200` array of registration summaries. |
| `PATCH /api/events/{id}/approve` | Admin | No body | `200` admin event summary; only pending events are approvable. |
| `PATCH /api/events/{id}/disable` | Admin | No body | `200` admin event summary; already-disabled events conflict. |

Event create/update request:

```json
{
  "title": "Campus Workshop",
  "description": "Example event description",
  "dateTime": "2026-11-25T10:00:00",
  "endDateTime": "2026-11-25T12:00:00",
  "venueId": "<venue-id>",
  "category": "Technology",
  "imageUrl": "https://example.invalid/event-image",
  "maxAttendees": 25
}
```

Event summary response:

```json
{
  "id": 42,
  "title": "Campus Workshop",
  "description": "Example event description",
  "dateTime": "2026-11-25T10:00:00",
  "endDateTime": "2026-11-25T12:00:00",
  "venueName": "Main Hall",
  "category": "Technology",
  "imageUrl": "https://example.invalid/event-image",
  "maxAttendees": 25,
  "status": "APPROVED"
}
```

The legacy admin moderation endpoints remain supported:
`GET /api/admin/events?status=ALL|PENDING_APPROVAL|APPROVED|DISABLED`,
`PATCH /api/admin/events/{id}/approve`, and
`PATCH /api/admin/events/{id}/disable`. The frontend helpers use the event
moderation paths listed above.

## Bookings and tickets

| Method and path | Access | Request | Success response |
|---|---|---|---|
| `GET /api/bookings/exists?eventId={id}` | Student | Query parameter only | `200` JSON boolean. |
| `POST /api/bookings` | Student | `{ "eventId": 42 }`; student comes from JWT. | `201` booking summary. |
| `GET /api/bookings/my` | Student | None | `200` array of the current student's bookings. `/api/bookings/me` is an alias. |
| `PATCH /api/bookings/{bookingId-or-reference}/cancel` | Booking owner | No body | `200` updated booking summary; duplicate booking/capacity/repeated-cancel conflicts are `409`. |
| `GET /api/tickets/booking/{bookingId}` | Booking owner (student) | None | `200` ticket DTO; `404` if not found. |

Booking summary:

```json
{
  "id": 52,
  "bookingReference": "BKG-<generated-reference>",
  "bookingTime": "2026-11-20T09:15:00",
  "lastUpdated": "2026-11-20T09:15:00",
  "status": "PENDING",
  "eventId": 42,
  "eventTitle": "Campus Workshop",
  "eventDateTime": "2026-11-25T10:00:00",
  "venueName": "Main Hall"
}
```

The ticket DTO contains `ticketId`, `ticketCode`, `issueDate`, `used`,
`eventName`, `eventDateTime`, `venue`, and `studentName`.

## Organizer, venue, and student profile

| Method and path | Access | Request | Success response |
|---|---|---|---|
| `GET /api/organizers/me/events?status={status}` | Organizer; admin may list all | Optional status filter | `200` array with `id`, `name`, `dateTime`, `venueName`, `status`, `registrationCount`. |
| `GET /api/venues` | Any authenticated role | None | `200` array of venue summaries (`venueId`, `venueName`, `campus`, `locationDetails`, `capacity`). |
| `GET /api/users/me` | Student | None | `200` student profile DTO. |
| `PATCH /api/users/me` | Student | `firstName`, `surname`, `phoneNumber`, `residence`, `faculty`, `yearOfStudy` | `200` updated student profile DTO. |
| `PATCH /api/users/me/preferences` | Student | `emailReminders`, `eventUpdates` | `200` updated student profile DTO. |

Student profile response fields are `userId`, `firstName`, `surname`, `name`,
`email`, `phoneNumber`, `studentNumber`, `residence`, `faculty`, `department`,
`yearOfStudy`, and nested `preferences` (`emailReminders`, `eventUpdates`).

## Notifications

All notification routes are scoped to the authenticated student.

| Method and path | Request | Success response |
|---|---|---|
| `GET /api/notifications` | None | `200` array with `id`, `type`, `title`, `message`, `createdAt`, `read`. |
| `GET /api/notifications/unread-count` | None | `200` `{ "unreadCount": 2 }`. |
| `PATCH /api/notifications/read-all` | No body | `204` no content. |
| `PATCH /api/notifications/{id}/read` | No body | `200` updated notification DTO. |
| `DELETE /api/notifications/{id}` | None | `204` no content. |

## Administration

| Method and path | Access | Request | Success response |
|---|---|---|---|
| `GET /api/admin/stats` | Admin | None | `200` `{ "eventCount": 10, "userCount": 20, "bookingCount": 30 }`. |
| `GET /api/admin/events?status=ALL` | Admin | Optional `status` | `200` array of admin event summaries (`id`, `name`, `organizerName`, `dateTime`, `status`, `registrationCount`). |
| `PATCH /api/admin/events/{id}/approve` | Admin | No body | `200` admin event summary; `409` unless pending. |
| `PATCH /api/admin/events/{id}/disable` | Admin | No body | `200` admin event summary; `409` if already disabled. |
| `GET /api/admin/users?search=&role=&page=0&size=20` | Admin | Optional query parameters | `200` paged user DTOs (`id`, `name`, `email`, `role`, `accountStatus`, `createdAt`). |
| `PATCH /api/admin/users/{id}/disable` | Admin | No body | `200` user DTO; `400` if the target is the current admin. |
| `PATCH /api/admin/users/{id}/approve` | Admin | No body | `200` user DTO; only pending organizers can be approved. |

Errors use a single body shape:

```json
{
  "timestamp": "2026-11-20T09:15:00Z",
  "status": 409,
  "error": "CONFLICT",
  "message": "The requested state transition is not allowed",
  "path": "/api/events/42/approve"
}
```

Invalid or missing authentication is `401`, role/ownership denial is `403`,
missing resources are `404`, conflicts are `409`, and validation errors are
`400`.

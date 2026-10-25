# UniVent frontend/backend integration

## Repository and branch

- Backend: `C:\Users\amand\IdeaProjects\UniVent-Backend`, branch `integration/frontend-backend`.
- Frontend: `C:\Users\amand\UniVent-Frontend`, branch `integration/frontend-backend`.
- Existing local edits were present in both worktrees before integration. They have been preserved.

## Inspection findings

### Backend

- Framework/build: Spring Boot 4.1.0, Java 17, Maven wrapper (`mvnw.cmd`).
- Persistence: Spring Data JPA with MySQL at runtime and H2 in tests. Hibernate currently uses `ddl-auto=update`; the repository has `database/migrations/V001__venue_ids_to_uuid.sql`, but no Flyway or Liquibase dependency/configuration was found. Existing migrations are SQL files and must not be edited.
- Existing entities: `User` with joined `Student`, `Organizer`, and `Administrator` subtypes; `Event`, `Venue`, `Booking`, `Ticket`, and `Notification`. Event date/time is currently a string, venue is a JPA relation, and event status includes pending, approved, disabled, cancelled, and lifecycle statuses. Student profile and notification preference fields already exist.
- Security: stateless Spring Security with a JWT filter and BCrypt. Role restrictions are present for users, bookings, tickets, notifications, organizers, and admins. CORS currently permits localhost/127.0.0.1 origins. Auth endpoints are public at `/api/auth/**`.
- Auth: `POST /api/auth/register` and `POST /api/auth/login` support student, organizer, and invited-admin registration. The auth DTO emits `token`, `role`, `userId`, `name`, `email`, and `accountStatus`; organizers are created as pending and can log in.
- API: controllers/services exist for events, bookings, tickets, notifications, venues, profiles, organizer events, and administration, but several CRUD/legacy routes expose entities, some methods are placeholders, and frontend routes are not consistently implemented. There is a global `ApiError` handler for several errors.
- Existing rules: events created through the event service are set to `PENDING_APPROVAL`; admin approval requires that state and disable conflicts when already disabled. Booking service enforces duplicate-booking, capacity, future-event, and repeated-cancellation checks; cancellation of confirmed bookings is currently allowed. No organizer self-service review workflow or cancellation deadline was found.
- Tests: Maven/JUnit/Spring test suite exists, with auth, service, controller, repository, and JWT-filter coverage. No migration framework dependency is configured.
- Current configured backend defaults use MySQL on port 3306 and HTTP port 8080. Environment-backed DB/JWT settings exist in `application-example.properties`; do not copy machine-local secrets.

### Frontend

- Framework/build: React 19 with Vite 8; scripts include `npm run dev`, `npm run build`, and `npm run lint`. No frontend test script/framework is configured.
- API client: Axios reads `VITE_API_URL` (default `http://localhost:8080`); API modules include `/api` in each request URL. The provided `.env.example` uses `VITE_API_URL=http://localhost:8080`.
- Auth: registration/login call `/api/auth/register` and `/api/auth/login`; the context stores `token`, `userId`, `name`, `email`, and `role` in `sessionStorage` keys `univent_token` and `univent_user`. It currently drops account status. Role redirects currently send students to `/student/profile`, organizers to `/organizer`, and admins to `/admin`; the requested destination for students is `/student`.
- Registration payload names: shared `name`, `email`, `password`, `phoneNumber`, `role`; student `studentNumber`, `faculty`, `department`, numeric `yearOfStudy`; organizer `staffNumber`, `organizationName`, `organizationType`, `contactEmail`; admin `staffNumber`, `inviteCode`. The backend `User` model stores staff numbers, and the organizer service maps `contactEmail` into the entity's `organizationEmail` property.
- Frontend endpoint contracts: `/api/events`, `/api/events/{id}`, `/api/events` POST, `/api/events/{id}` PUT, `/api/events/{id}/cancel`, `/api/events/{id}/approve`, `/api/events/{id}/disable`; `/api/bookings` POST with `{eventId}`, `/api/bookings/my` GET, and `/api/bookings/{id}/cancel` PATCH; `/api/tickets/booking/{bookingId}`; notifications at `/api/notifications`, `/unread-count`, `/read-all`, `/{id}/read`, and `/{id}` DELETE; user profile at `/api/users/me` and `/preferences`; venues at `/api/venues`; admin data at `/api/admin/events`, `/users`, `/stats`, and `/users/{id}/disable`.
- Event card reads `id`, `title`, `description`, `dateTime`, optional `endDateTime`, `venueName`, optional `category`/`imageUrl`, `maxAttendees`, and `status`. Notification UI reads `id`, `type`, `title`, `message`, `createdAt`, and `read`. Student profile reads the existing profile response names and nested `preferences`.
- Organizer event screens and most student/admin screens are placeholders, so they currently impose no additional implemented form payload or response contract. Existing API modules are the source for those endpoints.
- Role guards are client-side route guards only and must remain backed by server authorization. Frontend source was readable.

## Differences and compatibility choices

- Follow the frontend's `/api/bookings/my` path (not the proposed `/api/bookings/me`) and its PATCH cancellation method.
- Follow the frontend's `/api/events/{id}/approve` and `/api/events/{id}/disable` helpers while retaining the existing admin collection endpoints.
- Use `contactEmail` and `staffNumber` as the registration request field names because those are what the frontend submits; map them to existing model fields where possible.
- Preserve event card and notification field names in DTO responses; do not expose JPA relationships.
- Update the student post-auth destination to `/student`, as explicitly required, while retaining organizer/admin destinations.

## Short implementation plan

1. Complete auth validation and admin invite-code registration using hashed, one-time invite codes and account statuses without changing existing entity identities.
2. Add DTO-based API adapters for frontend event, booking, ticket, notification, profile, venue, organizer, and admin contracts; enforce role and ownership in the backend and preserve the existing event/booking rules.
3. Add forward-only SQL migrations in `database/migrations/` for required new columns/tables; do not edit V001. Document environment setup, migration execution, first-admin bootstrapping, and secure invite-code creation.
4. Make only the necessary frontend API/auth adjustments, including response names/status retention and the requested role redirects.
5. Extend the existing Maven test suite for registration, authorization, ownership, DTO exposure, and repository filters; run backend tests and the frontend build. Run HTTP smoke tests only if both services and a usable database can be started in this environment.

## Decisions and known boundaries

- The original code had no organizer approval transition. The user confirmed adding `PATCH /api/admin/users/{id}/approve`, which changes a pending organizer to active. New organizers can log in and view organizer routes but cannot create/edit/cancel events until approved.
- The current booking service allows cancellation of confirmed bookings and has no cancellation deadline. Preserve that behavior.
- The migration directory is not wired to a migration runner. Preserve its existing SQL-file convention and document manual execution unless repository configuration provides a migration runner during implementation.
- The organizer/admin/student frontend pages other than login, registration, student event feed, profile, and notifications are placeholders. API modules/contracts are wired, but no request body or UI behavior was defined by those placeholder pages; those screens were not redesigned.
- Organizer registration follow-up: the request model already included `staffNumber` and accepted the frontend's `contactEmail`; neither field was missing. The registration UI had labeled a faculty list as `organizationType`, although the entity persists that as an organization category. The frontend now asks for an organization type. Primary email and contact-email validation now match the form, and V004 adds a unique staff-number index after a documented duplicate-data preflight.

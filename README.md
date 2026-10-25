# UniVent Backend

## Local baseline

Requirements: JDK 17 or newer. The repository includes the Maven wrapper.

```powershell
.\mvnw.cmd spring-boot:run
```

The backend starts on `http://localhost:8080`. Verify it with:

```text
GET http://localhost:8080/api/health
```

The application connects to a local MySQL server by default and creates the
`univent` database if it does not already exist. Set your MySQL password before
starting the application:

```powershell
$env:DB_PASSWORD = "<your MySQL password>"
.\mvnw.cmd spring-boot:run
```

You can override `DB_URL` or `DB_USERNAME` with environment variables as well.
The test suite uses an isolated in-memory H2 database.

Do not commit real database credentials or JWT secrets. Optional machine-only
Spring settings can be placed in `application-local.properties`, which is
ignored by Git.

## Frontend integration

The frontend is in `C:\Users\amand\UniVent-Frontend`. Its Vite API base is
`VITE_API_URL=http://localhost:8080`; the frontend API modules include `/api`
in each endpoint path. The backend CORS configuration permits
`http://localhost:5173` and other localhost development ports.
See [`API_CONTRACT.md`](API_CONTRACT.md) for the request and response contracts.

Copy `.env.example` values into the process environment or IDE run configuration.
Spring Boot does not load `.env` files automatically. Set a real, randomly
generated `JWT_SECRET` and the local database password in your environment;
the example values are placeholders only.

Before starting against an existing MySQL database, check that user email values
are unique:

```sql
SELECT email, COUNT(*) FROM users GROUP BY email HAVING COUNT(*) > 1;
```

Resolve any duplicates before applying the unique-email migration. Before adding
the unique staff-number index, also check for existing duplicates:

```sql
SELECT LOWER(TRIM(staff_number)), COUNT(*) FROM users
WHERE staff_number IS NOT NULL AND TRIM(staff_number) <> ''
GROUP BY LOWER(TRIM(staff_number)) HAVING COUNT(*) > 1;
```

Resolve any reported duplicates without deleting account data. The existing
repository has no baseline migration for creating its original tables. On a new
database, start the backend once so Hibernate creates the existing schema, then
stop it before applying the forward migrations. The migrations are safe to
re-run and account for schema fields that Hibernate may already have added.
In PowerShell, from the backend repository:

```powershell
Get-Content -Raw .\database\migrations\V002__account_invites_and_staff_numbers.sql | mysql --user=root --password univent
Get-Content -Raw .\database\migrations\V003__event_frontend_fields.sql | mysql --user=root --password univent
Get-Content -Raw .\database\migrations\V004__unique_user_staff_numbers.sql | mysql --user=root --password univent
Get-Content -Raw .\database\migrations\V005__allow_pending_account_status.sql | mysql --user=root --password univent
```

The existing migration directory is not configured with Flyway or Liquibase, so
these SQL migrations are applied manually. Do not modify or rerun the legacy
`V001__venue_ids_to_uuid.sql` migration; it is only for older databases that
still use integer venue IDs.

Event creation requires an existing venue. If the database has no venue rows,
insert a venue through MySQL using the existing venue schema:

```sql
INSERT INTO venues (venue_id, venue_name, campus, location_details, capacity)
SELECT UUID(), 'Example Hall', 'Main Campus', 'Example venue', 250
WHERE NOT EXISTS (SELECT 1 FROM venues WHERE venue_name = 'Example Hall');
```

### First administrator and additional administrator invites

Admin signup from the public frontend is disabled by default
(`ADMIN_REGISTRATION_ENABLED=false`). For a local demo only, enable the
`demo` Spring profile. This allows public Admin signup without an invite code;
do not activate this profile in a deployed or shared environment:

```powershell
$env:SPRING_PROFILES_ACTIVE = "demo"
.\mvnw.cmd spring-boot:run
```

Unset `SPRING_PROFILES_ACTIVE` after local testing. Production/default
configuration remains invite-protected and requires no change to the frontend
registration payload.

For production and other non-demo environments, provision Admin accounts using
the one-time hashed invite process below. Invite codes remain required when
`ADMIN_REGISTRATION_ENABLED` is false, and are consumed transactionally with
account creation.

Create a one-time invite code in the MySQL client. The query inserts only its
SHA-256 hash; the final `SELECT` displays the plaintext code once for immediate
use in administrator registration:

```sql
SET @invite_code = CONCAT('ADM-', UPPER(REPLACE(UUID(), '-', '')));
INSERT INTO admin_invite_codes (code_hash, status, created_at)
VALUES (SHA2(@invite_code, 256), 'AVAILABLE', UTC_TIMESTAMP(6));
SELECT @invite_code AS invite_code;
```

Register the administrator through `POST /api/auth/register` using role
`ADMIN`, the assigned staff number, a university email, a password of at least
eight characters, and the invite code. The same one-time process is used to
grant later administrators; no default password or public invite code exists.

After setting database/JWT environment variables and applying migrations, start
the backend with:

```powershell
.\mvnw.cmd spring-boot:run
```

Start the frontend in a second terminal:

```powershell
Set-Location C:\Users\amand\UniVent-Frontend
npm run dev
```

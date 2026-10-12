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

No local database setup is required for development: the default profile uses
an in-memory H2 database. Data is reset whenever the application stops.

To use MySQL, set these environment variables before starting the application:

```text
DB_URL=jdbc:mysql://localhost:3306/univent
DB_USERNAME=<your username>
DB_PASSWORD=<your password>
JWT_SECRET=<a private secret of at least 32 characters>
```

Do not commit real database credentials or JWT secrets. Optional machine-only
Spring settings can be placed in `application-local.properties`, which is
ignored by Git.

-- Run once against older UniVent MySQL databases created before Venue IDs
-- changed from auto-increment integers to UUID strings. New databases created
-- from the current entities already use VARCHAR and do not need this script.

SET @venue_fk = (
    SELECT CONSTRAINT_NAME
    FROM information_schema.KEY_COLUMN_USAGE
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'events'
      AND COLUMN_NAME = 'venue_id'
      AND REFERENCED_TABLE_NAME = 'venues'
    LIMIT 1
);
SET @drop_venue_fk = IF(
    @venue_fk IS NULL,
    'SELECT 1',
    CONCAT('ALTER TABLE events DROP FOREIGN KEY `', @venue_fk, '`')
);
PREPARE drop_venue_fk_statement FROM @drop_venue_fk;
EXECUTE drop_venue_fk_statement;
DEALLOCATE PREPARE drop_venue_fk_statement;

ALTER TABLE venues MODIFY COLUMN venue_id VARCHAR(36) NOT NULL;
ALTER TABLE events MODIFY COLUMN venue_id VARCHAR(36) NULL;
ALTER TABLE events
    ADD CONSTRAINT FK_events_venue
    FOREIGN KEY (venue_id) REFERENCES venues (venue_id);

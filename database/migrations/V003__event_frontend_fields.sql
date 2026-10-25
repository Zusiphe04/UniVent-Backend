SET @migration_sql = IF(
    (SELECT COUNT(*) FROM information_schema.columns
     WHERE table_schema = DATABASE() AND table_name = 'events' AND column_name = 'end_date_time') = 0,
    'ALTER TABLE events ADD COLUMN end_date_time VARCHAR(255) NULL',
    'SELECT 1'
);
PREPARE add_event_end_date_time FROM @migration_sql;
EXECUTE add_event_end_date_time;
DEALLOCATE PREPARE add_event_end_date_time;

SET @migration_sql = IF(
    (SELECT COUNT(*) FROM information_schema.columns
     WHERE table_schema = DATABASE() AND table_name = 'events' AND column_name = 'category') = 0,
    'ALTER TABLE events ADD COLUMN category VARCHAR(255) NULL',
    'SELECT 1'
);
PREPARE add_event_category FROM @migration_sql;
EXECUTE add_event_category;
DEALLOCATE PREPARE add_event_category;

SET @migration_sql = IF(
    (SELECT COUNT(*) FROM information_schema.columns
     WHERE table_schema = DATABASE() AND table_name = 'events' AND column_name = 'image_url') = 0,
    'ALTER TABLE events ADD COLUMN image_url VARCHAR(2048) NULL',
    'SELECT 1'
);
PREPARE add_event_image_url FROM @migration_sql;
EXECUTE add_event_image_url;
DEALLOCATE PREPARE add_event_image_url;

ALTER TABLE events MODIFY COLUMN description VARCHAR(5000) NULL;

SET @migration_sql = IF(
    (SELECT COUNT(*) FROM information_schema.statistics
     WHERE table_schema = DATABASE() AND table_name = 'users'
       AND index_name = 'ux_users_staff_number') = 0,
    'CREATE UNIQUE INDEX ux_users_staff_number ON users (staff_number)',
    'SELECT 1'
);
PREPARE add_unique_staff_number FROM @migration_sql;
EXECUTE add_unique_staff_number;
DEALLOCATE PREPARE add_unique_staff_number;

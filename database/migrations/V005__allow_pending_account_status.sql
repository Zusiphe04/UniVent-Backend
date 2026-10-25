SET @migration_sql = IF(
    (SELECT COUNT(*) FROM information_schema.columns
     WHERE table_schema = DATABASE() AND table_name = 'users'
       AND column_name = 'account_status'
       AND (data_type <> 'varchar' OR character_maximum_length < 20)) > 0,
    'ALTER TABLE users MODIFY COLUMN account_status VARCHAR(20) NULL',
    'SELECT 1'
);
PREPARE widen_account_status FROM @migration_sql;
EXECUTE widen_account_status;
DEALLOCATE PREPARE widen_account_status;

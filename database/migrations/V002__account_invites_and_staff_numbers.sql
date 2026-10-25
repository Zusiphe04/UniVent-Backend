SET @migration_sql = IF(
    (SELECT COUNT(*) FROM information_schema.columns
     WHERE table_schema = DATABASE() AND table_name = 'users' AND column_name = 'staff_number') = 0,
    'ALTER TABLE users ADD COLUMN staff_number VARCHAR(255) NULL',
    'SELECT 1'
);
PREPARE add_staff_number FROM @migration_sql;
EXECUTE add_staff_number;
DEALLOCATE PREPARE add_staff_number;

SET @migration_sql = IF(
    (SELECT COUNT(*) FROM information_schema.columns
     WHERE table_schema = DATABASE() AND table_name = 'users' AND column_name = 'account_status') = 0,
    'ALTER TABLE users ADD COLUMN account_status VARCHAR(20) NULL',
    'SELECT 1'
);
PREPARE add_account_status FROM @migration_sql;
EXECUTE add_account_status;
DEALLOCATE PREPARE add_account_status;

SET @migration_sql = IF(
    (SELECT COUNT(*) FROM information_schema.columns
     WHERE table_schema = DATABASE() AND table_name = 'users' AND column_name = 'disabled') = 0,
    'UPDATE users SET account_status = COALESCE(account_status, ''ACTIVE'')',
    'UPDATE users SET account_status = CASE WHEN disabled = 1 THEN ''DISABLED'' ELSE ''ACTIVE'' END WHERE account_status IS NULL'
);
PREPARE initialize_account_status FROM @migration_sql;
EXECUTE initialize_account_status;
DEALLOCATE PREPARE initialize_account_status;

SET @migration_sql = IF(
    (SELECT COUNT(*) FROM information_schema.statistics
     WHERE table_schema = DATABASE() AND table_name = 'users' AND index_name = 'ux_users_email') = 0,
    'CREATE UNIQUE INDEX ux_users_email ON users (email)',
    'SELECT 1'
);
PREPARE add_unique_email_index FROM @migration_sql;
EXECUTE add_unique_email_index;
DEALLOCATE PREPARE add_unique_email_index;

CREATE TABLE IF NOT EXISTS admin_invite_codes (
    code_hash CHAR(64) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    used_at TIMESTAMP(6) NULL,
    PRIMARY KEY (code_hash)
);

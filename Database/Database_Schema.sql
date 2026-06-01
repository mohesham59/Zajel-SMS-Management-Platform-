-- ============================================
-- Custom Type for Address
-- ============================================
CREATE TYPE customer_address_type AS (
    street   TEXT,
    city     TEXT,
    state    TEXT,
    zip      TEXT,
    country  TEXT
);

-- ============================================
-- TABLE: admins
-- ============================================
CREATE TABLE admins (
    admin_id    SERIAL PRIMARY KEY,
    name        TEXT NOT NULL,
    email       VARCHAR(255) NOT NULL UNIQUE,
    passwd_hash TEXT NOT NULL,
    is_super    BOOLEAN NOT NULL DEFAULT FALSE
);

-- ============================================
-- TABLE: customers
-- ============================================
CREATE TABLE customers (
    customer_id      SERIAL PRIMARY KEY,
    name             TEXT NOT NULL,
    email            TEXT NOT NULL UNIQUE,
    passwd           TEXT NOT NULL,
    msisdn           TEXT NOT NULL,
    birthday         DATE,
    job              TEXT,
    customer_address customer_address_type,
    sid              TEXT,          -- Twilio Account SID
    token            TEXT,          -- Twilio Auth Token
    created_at       TIMESTAMPTZ DEFAULT NOW()
);

-- ============================================
-- TABLE: errcod (Error Codes lookup)
-- ============================================
CREATE TABLE errcod (
    error_id    SERIAL PRIMARY KEY,
    error_msg   TEXT NOT NULL,
    description TEXT
);

-- ============================================
-- TABLE: msgs
-- ============================================
CREATE TABLE msgs (
    msg_id          SERIAL PRIMARY KEY,
    sender_id       INTEGER NOT NULL REFERENCES customers(customer_id) ON DELETE CASCADE,
    receiver_msisdn TEXT NOT NULL,
    msg_body        TEXT NOT NULL,
    msg_stamp       TIMESTAMPTZ DEFAULT NOW(),
    error_code      INTEGER REFERENCES errcod(error_id)
);

-- ============================================
-- TABLE: logs
-- ============================================
CREATE TABLE logs (
    log_id    SERIAL PRIMARY KEY,
    rqst_ip   INET,
    log_body  TEXT,
    log_stamp TIMESTAMPTZ DEFAULT NOW()
);

-- ============================================
-- INDEXES
-- ============================================
CREATE INDEX idx_msgs_sender ON msgs(sender_id);
CREATE INDEX idx_msgs_stamp ON msgs(msg_stamp);
CREATE INDEX idx_msgs_receiver ON msgs(receiver_msisdn);
CREATE INDEX idx_logs_stamp ON logs(log_stamp);

-- ============================================
-- Default super admin (password: admin123)
-- SHA-256 of 'admin123'
-- ============================================
INSERT INTO admins (name, email, passwd_hash, is_super)
VALUES ('Super Admin', 'admin@system.com',
        '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', TRUE);

-- ============================================
-- Seed error codes
-- ============================================
INSERT INTO errcod (error_msg, description) VALUES
('SUCCESS', 'SMS sent successfully'),
('INVALID_NUMBER', 'The recipient phone number is invalid'),
('AUTH_FAILED', 'Twilio authentication failed'),
('QUOTA_EXCEEDED', 'SMS sending quota exceeded'),
('NETWORK_ERROR', 'Network error while sending SMS'),
('UNKNOWN_ERROR', 'An unknown error occurred');

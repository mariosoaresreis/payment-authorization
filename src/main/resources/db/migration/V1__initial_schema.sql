-- Payment Authorization Database Schema
-- PostgreSQL 15+

-- Create schema
CREATE SCHEMA IF NOT EXISTS payment;

-- Transactions table
CREATE TABLE IF NOT EXISTS payment.transactions (
    transaction_id VARCHAR(100) PRIMARY KEY,
    merchant_id VARCHAR(50) NOT NULL,
    card_number VARCHAR(20) NOT NULL,
    card_holder VARCHAR(100) NOT NULL,
    bin VARCHAR(20) NOT NULL,
    card_brand VARCHAR(20),
    amount NUMERIC(19, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(20) NOT NULL,
    authorization_code VARCHAR(50),
    decline_reason VARCHAR(500),
    fraud_score VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT amount_positive CHECK (amount > 0)
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_merchant_id ON payment.transactions(merchant_id);
CREATE INDEX IF NOT EXISTS idx_status ON payment.transactions(status);
CREATE INDEX IF NOT EXISTS idx_created_at ON payment.transactions(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_card_number ON payment.transactions(card_number);

-- Create audit table
CREATE TABLE IF NOT EXISTS payment.transaction_audit (
    audit_id BIGSERIAL PRIMARY KEY,
    transaction_id VARCHAR(100) NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    old_status VARCHAR(20),
    new_status VARCHAR(20),
    reason TEXT,
    user_id VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transaction FOREIGN KEY (transaction_id) REFERENCES payment.transactions(transaction_id)
);

-- Create index on audit
CREATE INDEX IF NOT EXISTS idx_audit_transaction_id ON payment.transaction_audit(transaction_id);
CREATE INDEX IF NOT EXISTS idx_audit_created_at ON payment.transaction_audit(created_at DESC);

-- Merchants table (mock)
CREATE TABLE IF NOT EXISTS payment.merchants (
    merchant_id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    mcc VARCHAR(4) NOT NULL,
    country VARCHAR(2) NOT NULL,
    daily_limit NUMERIC(19, 2) NOT NULL,
    risk_level VARCHAR(20) DEFAULT 'MEDIUM',
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert sample merchants
INSERT INTO payment.merchants (merchant_id, name, mcc, country, daily_limit, risk_level)
VALUES
    ('MERCHANT_LOW', 'Low Risk Merchant', '5411', 'BR', 50000.00, 'LOW'),
    ('MERCHANT_MED', 'Medium Risk Merchant', '6211', 'BR', 10000.00, 'MEDIUM'),
    ('MERCHANT_HIGH', 'High Risk Merchant', '7995', 'BR', 5000.00, 'HIGH')
ON CONFLICT DO NOTHING;

-- Create function for updated_at trigger
CREATE OR REPLACE FUNCTION payment.update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create triggers
DROP TRIGGER IF EXISTS transactions_update_timestamp ON payment.transactions;
CREATE TRIGGER transactions_update_timestamp
    BEFORE UPDATE ON payment.transactions
    FOR EACH ROW
    EXECUTE FUNCTION payment.update_timestamp();

DROP TRIGGER IF EXISTS merchants_update_timestamp ON payment.merchants;
CREATE TRIGGER merchants_update_timestamp
    BEFORE UPDATE ON payment.merchants
    FOR EACH ROW
    EXECUTE FUNCTION payment.update_timestamp();

-- Grants
GRANT USAGE ON SCHEMA payment TO payment_user;
GRANT SELECT, INSERT, UPDATE ON ALL TABLES IN SCHEMA payment TO payment_user;
GRANT USAGE ON ALL SEQUENCES IN SCHEMA payment TO payment_user;


CREATE TABLE IF NOT EXISTS donation_transaction (
                                                    id SERIAL PRIMARY KEY,
                                                    some FLOAT NOT NULL,
                                                    date DATE NOT NULL,
                                                    time TIME NOT NULL,
                                                    amount FLOAT NOT NULL,
                                                    payment_method VARCHAR(20),
    state VARCHAR(20),
    reference_number VARCHAR(255) NOT NULL,
    charity_action_id INTEGER,
    user_id INTEGER
    );

-- Add constraints after all tables exist
ALTER TABLE donation_transaction
    ADD CONSTRAINT fk_transaction_user
        FOREIGN KEY (user_id) REFERENCES "user"(id);

ALTER TABLE donation_transaction
    ADD CONSTRAINT fk_transaction_charity_action
        FOREIGN KEY (charity_action_id) REFERENCES charity_action(id);

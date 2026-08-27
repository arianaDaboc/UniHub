CREATE TABLE email_reminders (
    id BIGSERIAL PRIMARY KEY,
    assignment_id BIGINT NOT NULL REFERENCES assignments(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    sent_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_email_reminder_assignment_user UNIQUE (assignment_id, user_id)
);

CREATE INDEX idx_email_reminders_user ON email_reminders(user_id);

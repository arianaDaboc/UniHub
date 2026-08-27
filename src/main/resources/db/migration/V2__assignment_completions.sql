CREATE TABLE assignment_completions (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    assignment_id BIGINT NOT NULL REFERENCES assignments(id) ON DELETE CASCADE,
    completed_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_completion_student_assignment UNIQUE (student_id, assignment_id)
);

CREATE INDEX idx_completions_student ON assignment_completions(student_id);


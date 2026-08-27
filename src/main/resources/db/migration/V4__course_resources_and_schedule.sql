CREATE TABLE materials (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    stored_filename VARCHAR(255) NOT NULL UNIQUE,
    content_type VARCHAR(120) NOT NULL,
    file_size BIGINT NOT NULL CHECK (file_size > 0),
    uploaded_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE course_prerequisites (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    prerequisite_course_id BIGINT NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    CONSTRAINT uk_course_prerequisite UNIQUE (course_id, prerequisite_course_id),
    CONSTRAINT ck_course_not_own_prerequisite CHECK (course_id <> prerequisite_course_id)
);

CREATE TABLE course_schedules (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    day_of_week VARCHAR(10) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    room VARCHAR(80) NOT NULL,
    CONSTRAINT ck_schedule_time CHECK (end_time > start_time)
);

CREATE INDEX idx_materials_course ON materials(course_id);
CREATE INDEX idx_prerequisites_course ON course_prerequisites(course_id);
CREATE INDEX idx_schedules_course ON course_schedules(course_id);


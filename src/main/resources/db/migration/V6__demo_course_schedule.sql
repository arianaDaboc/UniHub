INSERT INTO course_schedules (course_id, day_of_week, start_time, end_time, room)
SELECT c.id, schedule.day_of_week, schedule.start_time, schedule.end_time, schedule.room
FROM courses c
JOIN (
    VALUES
        ('Algoritmi fundamentali', 'MONDAY', CAST('10:00' AS time), CAST('12:00' AS time), 'C03'),
        ('Baze de date', 'TUESDAY', CAST('14:00' AS time), CAST('16:00' AS time), 'Laborator 26'),
        ('Programare orientată pe obiecte', 'THURSDAY', CAST('08:00' AS time), CAST('10:00' AS time), 'C2')
) AS schedule(course_name, day_of_week, start_time, end_time, room)
    ON schedule.course_name = c.name
WHERE NOT EXISTS (
    SELECT 1
    FROM course_schedules existing
    WHERE existing.course_id = c.id
);

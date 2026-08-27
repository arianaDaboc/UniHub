INSERT INTO assignments (course_id, title, description, deadline, max_points)
SELECT c.id, seed.title, seed.description, CURRENT_TIMESTAMP + seed.deadline_offset, seed.max_points
FROM courses c
JOIN (VALUES
    ('Computer Architecture', 'Cache Performance Analysis', 'Compare direct-mapped and set-associative cache behaviour for the provided memory traces.', INTERVAL '30 hours', 30),
    ('Computer Architecture', 'MIPS Pipeline Simulator', 'Implement forwarding and hazard detection for a five-stage instruction pipeline.', INTERVAL '8 days', 80),
    ('Databases', 'Advanced SQL Queries', 'Write aggregate, window-function and common-table-expression queries for the university dataset.', INTERVAL '44 hours', 40),
    ('Databases', 'Transaction Isolation Report', 'Reproduce and explain dirty reads, non-repeatable reads and phantom reads.', INTERVAL '10 days', 35),
    ('Fundamental Algorithms', 'Shortest Paths Laboratory', 'Implement Dijkstra and Bellman-Ford and compare their behaviour on sparse graphs.', INTERVAL '54 hours', 50),
    ('Fundamental Algorithms', 'Dynamic Programming Problems', 'Solve the selected optimisation problems and document the recurrence relations.', INTERVAL '12 days', 60),
    ('Object-Oriented Programming', 'Design Patterns Exercise', 'Apply Strategy and Observer patterns to the supplied application.', INTERVAL '68 hours', 40),
    ('Object-Oriented Programming', 'Unit Testing Workshop', 'Add JUnit and Mockito coverage for the service layer.', INTERVAL '9 days', 30),
    ('Operating Systems', 'Process Scheduling Simulation', 'Implement FCFS, SJF and Round Robin scheduling and compare waiting times.', INTERVAL '36 hours', 50),
    ('Operating Systems', 'Thread Synchronisation Laboratory', 'Solve the producer-consumer problem using semaphores and document race conditions.', INTERVAL '13 days', 45)
) AS seed(course_name, title, description, deadline_offset, max_points)
    ON c.name = seed.course_name
WHERE NOT EXISTS (
    SELECT 1 FROM assignments existing
    WHERE existing.course_id = c.id AND existing.title = seed.title
);

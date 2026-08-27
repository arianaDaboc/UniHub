package ro.utcluj.unihub.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ro.utcluj.unihub.model.*;
import ro.utcluj.unihub.repository.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class DemoDataInitializer implements CommandLineRunner {
    private final UserRepository users;
    private final CourseRepository courses;
    private final AssignmentRepository assignments;
    private final PasswordEncoder passwordEncoder;
    private final boolean enabled;

    public DemoDataInitializer(UserRepository users, CourseRepository courses, AssignmentRepository assignments,
                               PasswordEncoder passwordEncoder,
                               @Value("${app.demo.seed-enabled}") boolean enabled) {
        this.users = users; this.courses = courses; this.assignments = assignments;
        this.passwordEncoder = passwordEncoder; this.enabled = enabled;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!enabled) return;

        users.findByEmailIgnoreCase("admin.demo@utcluj.ro")
                .orElseGet(() -> users.save(new User("Admin", "UniHub", "admin.demo@utcluj.ro",
                        passwordEncoder.encode("DemoAdmin2026!"), Role.ADMIN)));

        User professor = users.findByEmailIgnoreCase("profesor.demo@utcluj.ro")
                .orElseGet(() -> users.save(new User("Andrei", "Popescu", "profesor.demo@utcluj.ro",
                        passwordEncoder.encode("DemoProfessor2026!"), Role.PROFESSOR)));

        if (courses.count() > 0) return;

        Course oop = course("Object-Oriented Programming", "Java, OOP principles and software design", professor);
        Course databases = course("Databases", "Relational modelling, SQL and PostgreSQL", professor);
        Course algorithms = course("Fundamental Algorithms", "Data structures and algorithm analysis", professor);
        Course systems = course("Operating Systems", "Processes, threads and memory management", professor);

        assignment(oop, "Project: Java Application", 3, 100);
        assignment(oop, "Inheritance and Interfaces Lab", 8, 20);
        assignment(databases, "SQL Assignment: Queries and Joins", 2, 30);
        assignment(databases, "Relational Schema Design", 12, 50);
        assignment(algorithms, "Graph Implementation", 5, 40);
        assignment(systems, "Processes and Threads Lab", 10, 25);
    }

    private Course course(String name, String description, User professor) {
        return courses.save(new Course(name, description, professor));
    }

    private void assignment(Course course, String title, long daysFromNow, int points) {
        assignments.save(new Assignment(course, title, "Full details will be published in the course.",
                Instant.now().plus(daysFromNow, ChronoUnit.DAYS), points));
    }
}

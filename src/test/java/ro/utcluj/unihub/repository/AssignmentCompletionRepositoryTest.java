package ro.utcluj.unihub.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ro.utcluj.unihub.model.*;
import java.time.Instant;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class AssignmentCompletionRepositoryTest {
    @Autowired UserRepository users;
    @Autowired CourseRepository courses;
    @Autowired EnrollmentRepository enrollments;
    @Autowired AssignmentRepository assignments;
    @Autowired AssignmentCompletionRepository completions;

    @Test void persistsPerStudentAssignmentProgress() {
        User professor = users.save(new User("Ada", "Lovelace", "prof@example.com", "hash", Role.PROFESSOR));
        User student = users.save(new User("Alan", "Turing", "student@example.com", "hash", Role.STUDENT));
        Course course = courses.save(new Course("Algorithms", "Core algorithms", professor));
        enrollments.save(new Enrollment(student, course));
        Assignment assignment = assignments.save(new Assignment(course, "Graph traversal", "Implement BFS",
                Instant.now().plusSeconds(86400), 100));

        completions.save(new AssignmentCompletion(student, assignment, Instant.now()));

        assertThat(completions.existsByStudentIdAndAssignmentId(student.getId(), assignment.getId())).isTrue();
        assertThat(completions.countByStudentIdAndAssignmentCourseId(student.getId(), course.getId())).isEqualTo(1);
        assertThat(assignments.countByCourseId(course.getId())).isEqualTo(1);
    }
}


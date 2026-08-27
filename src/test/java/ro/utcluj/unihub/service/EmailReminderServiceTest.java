package ro.utcluj.unihub.service;

import org.junit.jupiter.api.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import ro.utcluj.unihub.model.Assignment;
import ro.utcluj.unihub.model.Course;
import ro.utcluj.unihub.model.Enrollment;
import ro.utcluj.unihub.model.User;
import ro.utcluj.unihub.repository.AssignmentRepository;
import ro.utcluj.unihub.repository.EmailReminderRepository;
import ro.utcluj.unihub.repository.EnrollmentRepository;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmailReminderServiceTest {
    @Test
    void sendsReminderToEnrolledStudentAndRecordsDelivery() {
        Fixture fixture = new Fixture(false);

        fixture.service.sendUpcomingDeadlineReminders();

        verify(fixture.mailSender).send(any(SimpleMailMessage.class));
        verify(fixture.reminders).save(any());
    }

    @Test
    void doesNotSendReminderTwice() {
        Fixture fixture = new Fixture(true);

        fixture.service.sendUpcomingDeadlineReminders();

        verifyNoInteractions(fixture.mailSender);
        verify(fixture.reminders, never()).save(any());
    }

    private static final class Fixture {
        final JavaMailSender mailSender = mock(JavaMailSender.class);
        final EmailReminderRepository reminders = mock(EmailReminderRepository.class);
        final EmailReminderService service;

        Fixture(boolean alreadySent) {
            AssignmentRepository assignments = mock(AssignmentRepository.class);
            EnrollmentRepository enrollments = mock(EnrollmentRepository.class);
            Assignment assignment = mock(Assignment.class);
            Course course = mock(Course.class);
            User student = mock(User.class);
            Enrollment enrollment = mock(Enrollment.class);

            when(assignment.getId()).thenReturn(11L);
            when(assignment.getTitle()).thenReturn("Database project");
            when(assignment.getDeadline()).thenReturn(java.time.Instant.now().plusSeconds(3600));
            when(assignment.getCourse()).thenReturn(course);
            when(course.getId()).thenReturn(4L);
            when(course.getName()).thenReturn("Databases");
            when(student.getId()).thenReturn(8L);
            when(student.getFirstName()).thenReturn("Ariana");
            when(student.getEmail()).thenReturn("ariana@students.example.edu");
            when(enrollment.getStudent()).thenReturn(student);
            when(assignments.findByDeadlineBetweenOrderByDeadlineAsc(any(), any())).thenReturn(List.of(assignment));
            when(enrollments.findByCourseId(4L)).thenReturn(List.of(enrollment));
            when(reminders.existsByAssignmentIdAndUserId(11L, 8L)).thenReturn(alreadySent);

            service = new EmailReminderService(mailSender, assignments, enrollments, reminders,
                    "notifications@unihub.test", 72);
        }
    }
}

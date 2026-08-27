package ro.utcluj.unihub.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.utcluj.unihub.model.Assignment;
import ro.utcluj.unihub.model.EmailReminder;
import ro.utcluj.unihub.model.Enrollment;
import ro.utcluj.unihub.model.Course;
import ro.utcluj.unihub.model.User;
import ro.utcluj.unihub.repository.AssignmentRepository;
import ro.utcluj.unihub.repository.EmailReminderRepository;
import ro.utcluj.unihub.repository.EnrollmentRepository;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
@ConditionalOnProperty(name = "app.mail.enabled", havingValue = "true")
public class EmailReminderService {
    private static final Logger log = LoggerFactory.getLogger(EmailReminderService.class);
    private static final DateTimeFormatter DEADLINE_FORMAT = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm").withZone(ZoneId.of("Europe/Bucharest"));

    private final JavaMailSender mailSender;
    private final AssignmentRepository assignments;
    private final EnrollmentRepository enrollments;
    private final EmailReminderRepository reminders;
    private final String from;
    private final long reminderHours;

    public EmailReminderService(JavaMailSender mailSender, AssignmentRepository assignments,
                                EnrollmentRepository enrollments, EmailReminderRepository reminders,
                                @Value("${app.mail.from}") String from,
                                @Value("${app.mail.reminder-hours}") long reminderHours) {
        this.mailSender = mailSender;
        this.assignments = assignments;
        this.enrollments = enrollments;
        this.reminders = reminders;
        this.from = from;
        this.reminderHours = reminderHours;
    }

    @Scheduled(cron = "${app.mail.reminder-schedule}")
    @Transactional
    public void sendUpcomingDeadlineReminders() {
        Instant now = Instant.now();
        assignments.findByDeadlineBetweenOrderByDeadlineAsc(now, now.plus(Duration.ofHours(reminderHours)))
                .forEach(this::notifyStudents);
    }

    @Transactional
    public int sendCourseTest(Course course) {
        int sent = 0;
        for (Enrollment enrollment : enrollments.findByCourseId(course.getId())) {
            User student = enrollment.getStudent();
            SimpleMailMessage email = new SimpleMailMessage();
            email.setFrom(from);
            email.setTo(student.getEmail());
            email.setSubject("UniHub course notification: " + course.getName());
            email.setText("Hello " + student.getFirstName() + ",\n\n"
                    + "This is a test notification for " + course.getName() + ".\n\nUniHub");
            mailSender.send(email);
            sent++;
        }
        return sent;
    }

    private void notifyStudents(Assignment assignment) {
        for (Enrollment enrollment : enrollments.findByCourseId(assignment.getCourse().getId())) {
            User student = enrollment.getStudent();
            if (reminders.existsByAssignmentIdAndUserId(assignment.getId(), student.getId())) continue;
            send(assignment, student);
            reminders.save(new EmailReminder(assignment, student));
        }
    }

    private void send(Assignment assignment, User student) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(from);
        email.setTo(student.getEmail());
        email.setSubject("UniHub reminder: " + assignment.getTitle());
        email.setText("Hello " + student.getFirstName() + ",\n\n"
                + "The deadline for " + assignment.getTitle() + " in " + assignment.getCourse().getName()
                + " is " + DEADLINE_FORMAT.format(assignment.getDeadline()) + ".\n\n"
                + "Open UniHub to review the assignment details.\n\nUniHub");
        mailSender.send(email);
        log.info("Deadline reminder sent for assignment {} to user {}", assignment.getId(), student.getId());
    }
}

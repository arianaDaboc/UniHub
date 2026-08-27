package ro.utcluj.unihub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.utcluj.unihub.model.EmailReminder;

public interface EmailReminderRepository extends JpaRepository<EmailReminder, Long> {
    boolean existsByAssignmentIdAndUserId(Long assignmentId, Long userId);
}

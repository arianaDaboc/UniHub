package ro.utcluj.unihub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.utcluj.unihub.model.AssignmentCompletion;
import java.util.*;

public interface AssignmentCompletionRepository extends JpaRepository<AssignmentCompletion, Long> {
    boolean existsByStudentIdAndAssignmentId(Long studentId, Long assignmentId);
    Optional<AssignmentCompletion> findByStudentIdAndAssignmentId(Long studentId, Long assignmentId);
    long countByStudentIdAndAssignmentCourseId(Long studentId, Long courseId);
    List<AssignmentCompletion> findByStudentId(Long studentId);
}


package ro.utcluj.unihub.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ro.utcluj.unihub.model.Assignment;
import java.time.Instant;
import java.util.List;
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByCourseIdOrderByDeadlineAsc(Long courseId);
    long countByCourseId(Long courseId);
    List<Assignment> findByCourseIdInAndDeadlineAfterOrderByDeadlineAsc(List<Long> courseIds, Instant now);
    List<Assignment> findByDeadlineBetweenOrderByDeadlineAsc(Instant from, Instant to);
}

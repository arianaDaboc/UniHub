package ro.utcluj.unihub.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ro.utcluj.unihub.model.Enrollment;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);
    List<Enrollment> findByStudentId(Long studentId);
    List<Enrollment> findByCourseId(Long courseId);
    long countByCourseId(Long courseId);
    java.util.Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);
    @Query(value="SELECT e2.course_id, COUNT(DISTINCT e1.student_id) score FROM enrollments e1 JOIN enrollments e2 ON e1.student_id=e2.student_id AND e1.course_id<>e2.course_id WHERE e1.course_id IN (:courseIds) AND e2.course_id NOT IN (:courseIds) GROUP BY e2.course_id ORDER BY score DESC",nativeQuery=true)
    List<Object[]> findCoEnrollmentRecommendations(@Param("courseIds") List<Long> courseIds);
}

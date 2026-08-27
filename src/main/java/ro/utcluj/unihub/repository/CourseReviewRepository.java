package ro.utcluj.unihub.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ro.utcluj.unihub.model.CourseReview;
import java.util.*;
public interface CourseReviewRepository extends JpaRepository<CourseReview,Long>{
    List<CourseReview> findByCourseIdOrderByCreatedAtDesc(Long courseId);
    Optional<CourseReview> findByCourseIdAndStudentId(Long courseId,Long studentId);
    @Query("select avg(r.rating) from CourseReview r where r.course.id=:courseId") Double averageForCourse(Long courseId);
    long countByCourseId(Long courseId);
}


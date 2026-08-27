package ro.utcluj.unihub.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ro.utcluj.unihub.model.CoursePrerequisite;
import java.util.*;
public interface CoursePrerequisiteRepository extends JpaRepository<CoursePrerequisite,Long>{
    List<CoursePrerequisite> findByCourseId(Long courseId);
    boolean existsByCourseIdAndPrerequisiteCourseId(Long courseId,Long prerequisiteId);
    Optional<CoursePrerequisite> findByCourseIdAndPrerequisiteCourseId(Long courseId,Long prerequisiteId);
}


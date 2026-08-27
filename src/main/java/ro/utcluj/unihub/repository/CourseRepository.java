package ro.utcluj.unihub.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ro.utcluj.unihub.model.Course;
import java.util.List;
public interface CourseRepository extends JpaRepository<Course, Long> { List<Course> findAllByOrderByNameAsc(); List<Course> findByProfessorIdOrderByNameAsc(Long professorId); }


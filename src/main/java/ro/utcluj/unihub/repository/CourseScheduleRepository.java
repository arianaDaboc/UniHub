package ro.utcluj.unihub.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ro.utcluj.unihub.model.CourseSchedule;
import java.util.List;
public interface CourseScheduleRepository extends JpaRepository<CourseSchedule,Long>{List<CourseSchedule> findByCourseIdOrderByDayOfWeekAscStartTimeAsc(Long courseId);List<CourseSchedule> findByCourseIdIn(List<Long> courseIds);}


package ro.utcluj.unihub.service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.utcluj.unihub.dto.ApiDtos.*;
import ro.utcluj.unihub.model.*;
import ro.utcluj.unihub.repository.AssignmentRepository;
import java.time.*;
import java.util.List;
@Service
public class AssignmentService {
    private final AssignmentRepository assignments; private final CourseService courses; private final NotificationService notifications;
    public AssignmentService(AssignmentRepository a, CourseService c,NotificationService n) { assignments=a; courses=c;notifications=n; }
    @Transactional public AssignmentResponse create(Long courseId, AssignmentRequest r, User user) {
        Course c=courses.find(courseId); courses.assertOwnerOrAdmin(c,user);
        Assignment saved=assignments.save(new Assignment(c,r.title().trim(),r.description(),r.deadline(),r.maxPoints()));
        notifications.notifyCourse(courseId,"ASSIGNMENT","New assignment",saved.getTitle()+" · "+c.getName(),"assignment:"+saved.getId());
        return dto(saved);
    }
    @Transactional(readOnly=true) public List<AssignmentResponse> byCourse(Long courseId) { courses.find(courseId); return assignments.findByCourseIdOrderByDeadlineAsc(courseId).stream().map(this::dto).toList(); }
    private AssignmentResponse dto(Assignment a) { return new AssignmentResponse(a.getId(),a.getCourse().getId(),a.getTitle(),a.getDescription(),a.getDeadline(),a.getMaxPoints(),priority(a.getDeadline())); }
    static String priority(Instant deadline) { long hours=Duration.between(Instant.now(),deadline).toHours(); return hours<24?"URGENT":hours<72?"SOON":"OK"; }
}

package ro.utcluj.unihub.service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.utcluj.unihub.dto.ApiDtos.*;
import ro.utcluj.unihub.exception.ResourceNotFoundException;
import ro.utcluj.unihub.model.*;
import ro.utcluj.unihub.repository.*;
import java.time.*;
import java.util.List;
@Service
public class NotificationService {
    private final NotificationRepository notifications;private final EnrollmentRepository enrollments;private final AssignmentRepository assignments;
    public NotificationService(NotificationRepository n,EnrollmentRepository e,AssignmentRepository a){notifications=n;enrollments=e;assignments=a;}
    @Transactional public void notifyCourse(Long courseId,String type,String title,String message,String reference){for(Enrollment e:enrollments.findByCourseId(courseId))create(e.getStudent(),type,title,message,reference);}
    @Transactional public NotificationSummary list(User user){refreshDeadlines(user);List<NotificationResponse> items=notifications.findTop50ByUserIdOrderByCreatedAtDesc(user.getId()).stream().map(this::dto).toList();return new NotificationSummary(notifications.countByUserIdAndReadFalse(user.getId()),items);}
    @Transactional public NotificationResponse markRead(Long id,User user){Notification n=notifications.findById(id).filter(item->item.getUser().getId().equals(user.getId())).orElseThrow(()->new ResourceNotFoundException("Notification not found"));n.markRead();return dto(n);}
    @Transactional public void markAllRead(User user){notifications.findTop50ByUserIdOrderByCreatedAtDesc(user.getId()).forEach(Notification::markRead);}
    private void refreshDeadlines(User user){List<Long> ids=enrollments.findByStudentId(user.getId()).stream().map(e->e.getCourse().getId()).toList();if(ids.isEmpty())return;Instant now=Instant.now();assignments.findByCourseIdInAndDeadlineAfterOrderByDeadlineAsc(ids,now).stream().filter(a->a.getDeadline().isBefore(now.plus(72,java.time.temporal.ChronoUnit.HOURS))).forEach(a->create(user,"DEADLINE","Upcoming deadline",a.getTitle()+" · "+a.getCourse().getName(),"deadline:"+a.getId()));}
    private void create(User user,String type,String title,String message,String key){if(!notifications.existsByUserIdAndReferenceKey(user.getId(),key))notifications.save(new Notification(user,type,title,message,key));}
    private NotificationResponse dto(Notification n){return new NotificationResponse(n.getId(),n.getType(),n.getTitle(),n.getMessage(),n.isRead(),n.getCreatedAt());}
}

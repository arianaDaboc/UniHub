package ro.utcluj.unihub.service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.utcluj.unihub.dto.ApiDtos.*;
import ro.utcluj.unihub.exception.*;
import ro.utcluj.unihub.model.*;
import ro.utcluj.unihub.repository.*;
import java.util.List;
@Service
public class CourseResourceService {
    private final CoursePrerequisiteRepository prerequisites;private final CourseScheduleRepository schedules;private final CourseService courses;private final EnrollmentRepository enrollments;private final CourseRepository courseRepository;
    public CourseResourceService(CoursePrerequisiteRepository p,CourseScheduleRepository s,CourseService c,EnrollmentRepository e,CourseRepository courseRepository){prerequisites=p;schedules=s;courses=c;enrollments=e;this.courseRepository=courseRepository;}
    @Transactional public PrerequisiteResponse addPrerequisite(Long courseId,Long prerequisiteId,User user){Course course=courses.find(courseId);courses.assertOwnerOrAdmin(course,user);Course prerequisite=courses.find(prerequisiteId);if(courseId.equals(prerequisiteId))throw new BadRequestException("A course cannot be its own prerequisite");if(prerequisites.existsByCourseIdAndPrerequisiteCourseId(courseId,prerequisiteId))throw new ConflictException("The prerequisite already exists");return prerequisiteDto(prerequisites.save(new CoursePrerequisite(course,prerequisite)));}
    @Transactional public void removePrerequisite(Long courseId,Long prerequisiteId,User user){Course course=courses.find(courseId);courses.assertOwnerOrAdmin(course,user);CoursePrerequisite p=prerequisites.findByCourseIdAndPrerequisiteCourseId(courseId,prerequisiteId).orElseThrow(()->new ResourceNotFoundException("Prerequisite not found"));prerequisites.delete(p);}
    @Transactional(readOnly=true) public List<PrerequisiteResponse> prerequisites(Long courseId){courses.find(courseId);return prerequisites.findByCourseId(courseId).stream().map(this::prerequisiteDto).toList();}
    @Transactional public ScheduleResponse addSchedule(Long courseId,ScheduleRequest request,User user){Course course=courses.find(courseId);courses.assertOwnerOrAdmin(course,user);if(!request.endTime().isAfter(request.startTime()))throw new BadRequestException("The end time must be after the start time");return scheduleDto(schedules.save(new CourseSchedule(course,request.dayOfWeek(),request.startTime(),request.endTime(),request.room().trim())));}
    @Transactional(readOnly=true) public List<ScheduleResponse> schedule(Long courseId){courses.find(courseId);return schedules.findByCourseIdOrderByDayOfWeekAscStartTimeAsc(courseId).stream().map(this::scheduleDto).toList();}
    @Transactional(readOnly=true) public List<WeeklyScheduleResponse> weeklySchedule(User user){
        List<Long> ids=switch(user.getRole()){
            case STUDENT -> enrollments.findByStudentId(user.getId()).stream().map(e->e.getCourse().getId()).toList();
            case PROFESSOR -> courseRepository.findByProfessorIdOrderByNameAsc(user.getId()).stream().map(Course::getId).toList();
            case ADMIN -> courseRepository.findAll().stream().map(Course::getId).toList();
        };
        if(ids.isEmpty())return List.of();
        return schedules.findByCourseIdIn(ids).stream().sorted(java.util.Comparator.comparing(CourseSchedule::getDayOfWeek).thenComparing(CourseSchedule::getStartTime)).map(s->new WeeklyScheduleResponse(s.getId(),s.getCourse().getId(),s.getCourse().getName(),s.getDayOfWeek(),s.getStartTime(),s.getEndTime(),s.getRoom())).toList();
    }
    private PrerequisiteResponse prerequisiteDto(CoursePrerequisite p){return new PrerequisiteResponse(p.getCourse().getId(),p.getPrerequisiteCourse().getId(),p.getPrerequisiteCourse().getName());}
    private ScheduleResponse scheduleDto(CourseSchedule s){return new ScheduleResponse(s.getId(),s.getCourse().getId(),s.getDayOfWeek(),s.getStartTime(),s.getEndTime(),s.getRoom());}
}

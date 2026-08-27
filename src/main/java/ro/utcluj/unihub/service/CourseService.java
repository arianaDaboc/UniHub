package ro.utcluj.unihub.service;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.utcluj.unihub.dto.ApiDtos.*;
import ro.utcluj.unihub.exception.*;
import ro.utcluj.unihub.model.*;
import ro.utcluj.unihub.repository.*;
import java.util.*;
import java.util.stream.Stream;

@Service
public class CourseService {
    private final CourseRepository courses; private final EnrollmentRepository enrollments; private final UserRepository users; private final CoursePrerequisiteRepository prerequisites;
    public CourseService(CourseRepository c, EnrollmentRepository e, UserRepository u, CoursePrerequisiteRepository p) { courses=c; enrollments=e; users=u; prerequisites=p; }
    @Transactional(readOnly=true) public List<CourseResponse> all(String search, String faculty, String department, String sort, User viewer) {
        Stream<Course> result = courses.findAll().stream();
        if (search != null && !search.isBlank()) {
            String term = search.trim().toLowerCase();
            result = result.filter(c -> c.getName().toLowerCase().contains(term)
                    || c.getProfessor().getFirstName().toLowerCase().contains(term)
                    || c.getProfessor().getLastName().toLowerCase().contains(term)
                    || c.getDepartment().toLowerCase().contains(term));
        }
        if (faculty != null && !faculty.isBlank()) result = result.filter(c -> c.getFaculty().equalsIgnoreCase(faculty));
        if (department != null && !department.isBlank()) result = result.filter(c -> c.getDepartment().equalsIgnoreCase(department));
        Comparator<Course> comparator = "ects".equalsIgnoreCase(sort) ? Comparator.comparing(Course::getEcts)
                : "available".equalsIgnoreCase(sort) ? Comparator.comparingLong(c -> -(c.getCapacity() - enrollments.countByCourseId(c.getId())))
                : Comparator.comparing(Course::getName, String.CASE_INSENSITIVE_ORDER);
        return result.sorted(comparator).map(c -> toResponse(c, viewer)).toList();
    }
    @Transactional public CourseResponse create(CourseRequest r, User professor) {
        if (professor.getRole()!=Role.PROFESSOR && professor.getRole()!=Role.ADMIN) throw new AccessDeniedException("Professor role required");
        return toResponse(courses.save(new Course(r.name().trim(), r.description(), r.ects(), r.faculty().trim(),
                r.department().trim(), r.capacity(), professor)));
    }
    @Transactional public EnrollmentResponse enroll(Long courseId, User student) {
        if (student.getRole()!=Role.STUDENT) throw new AccessDeniedException("Student role required");
        Course c=find(courseId);
        if (enrollments.existsByStudentIdAndCourseId(student.getId(), courseId)) throw new ConflictException("Already enrolled in this course");
        if (enrollments.countByCourseId(courseId) >= c.getCapacity()) throw new ConflictException("Course capacity has been reached");
        List<String> missing = prerequisites.findByCourseId(courseId).stream().map(CoursePrerequisite::getPrerequisiteCourse)
                .filter(required -> !enrollments.existsByStudentIdAndCourseId(student.getId(), required.getId()))
                .map(Course::getName).toList();
        if (!missing.isEmpty()) throw new ConflictException("Enrol in the following courses first: " + String.join(", ", missing));
        Enrollment e=enrollments.save(new Enrollment(student,c)); return enrollmentDto(e);
    }
    @Transactional(readOnly=true) public List<EnrollmentResponse> students(Long courseId, User requester) {
        Course c=find(courseId); assertOwnerOrAdmin(c,requester);
        return enrollments.findByCourseId(courseId).stream().map(this::enrollmentDto).toList();
    }
    public Course find(Long id) { return courses.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course not found")); }
    public void assertOwnerOrAdmin(Course c, User u) { if (u.getRole()!=Role.ADMIN && !c.getProfessor().getId().equals(u.getId())) throw new AccessDeniedException("You do not own this course"); }
    public CourseResponse toResponse(Course c) { User p=c.getProfessor(); return new CourseResponse(c.getId(),c.getName(),c.getDescription(),
            c.getEcts(),c.getFaculty(),c.getDepartment(),c.getCapacity(),enrollments.countByCourseId(c.getId()),false,
            p.getId(),p.getFirstName()+" "+p.getLastName()); }
    public CourseResponse toResponse(Course c, User viewer) { User p=c.getProfessor(); boolean isEnrolled=viewer!=null&&viewer.getRole()==Role.STUDENT&&enrollments.existsByStudentIdAndCourseId(viewer.getId(),c.getId());return new CourseResponse(c.getId(),c.getName(),c.getDescription(),c.getEcts(),c.getFaculty(),c.getDepartment(),c.getCapacity(),enrollments.countByCourseId(c.getId()),isEnrolled,p.getId(),p.getFirstName()+" "+p.getLastName()); }
    private EnrollmentResponse enrollmentDto(Enrollment e) { User s=e.getStudent(); return new EnrollmentResponse(e.getId(),s.getId(),s.getFirstName()+" "+s.getLastName(),e.getCourse().getId()); }
}

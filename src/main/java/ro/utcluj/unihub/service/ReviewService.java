package ro.utcluj.unihub.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.utcluj.unihub.dto.ApiDtos.*;
import ro.utcluj.unihub.exception.ResourceNotFoundException;
import ro.utcluj.unihub.model.*;
import ro.utcluj.unihub.repository.*;
import java.time.*;
import java.util.List;
@Service
public class ReviewService {
    private final CourseReviewRepository reviews;private final EnrollmentRepository enrollments;private final CourseService courses;private final long minimumDays;
    public ReviewService(CourseReviewRepository r,EnrollmentRepository e,CourseService c,@Value("${app.reviews.minimum-enrollment-days}") long days){reviews=r;enrollments=e;courses=c;minimumDays=days;}
    @Transactional public ReviewResponse save(Long courseId,ReviewRequest request,User student){if(student.getRole()!=Role.STUDENT)throw new AccessDeniedException("Student role required");Enrollment enrollment=enrollments.findByStudentIdAndCourseId(student.getId(),courseId).orElseThrow(()->new AccessDeniedException("You must be enrolled in the course to leave a review"));if(enrollment.getEnrolledAt().isAfter(Instant.now().minus(minimumDays,java.time.temporal.ChronoUnit.DAYS)))throw new AccessDeniedException("You can review the course after "+minimumDays+" days from enrolment");CourseReview review=reviews.findByCourseIdAndStudentId(courseId,student.getId()).orElseGet(()->new CourseReview(courses.find(courseId),student,request.rating(),request.comment()));review.update(request.rating(),request.comment());return dto(reviews.save(review));}
    @Transactional(readOnly=true) public List<ReviewResponse> byCourse(Long courseId){courses.find(courseId);return reviews.findByCourseIdOrderByCreatedAtDesc(courseId).stream().map(this::dto).toList();}
    @Transactional(readOnly=true) public Double average(Long courseId){return reviews.averageForCourse(courseId);}
    public long count(Long courseId){return reviews.countByCourseId(courseId);}
    private ReviewResponse dto(CourseReview r){User s=r.getStudent();return new ReviewResponse(r.getId(),r.getCourse().getId(),s.getId(),s.getFirstName()+" "+s.getLastName(),r.getRating(),r.getComment(),r.getCreatedAt());}
}

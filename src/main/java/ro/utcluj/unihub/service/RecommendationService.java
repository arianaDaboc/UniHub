package ro.utcluj.unihub.service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.utcluj.unihub.dto.ApiDtos.RecommendationResponse;
import ro.utcluj.unihub.model.User;
import ro.utcluj.unihub.repository.*;
import java.util.*;
@Service
public class RecommendationService {
    private final EnrollmentRepository enrollments;private final CourseRepository courses;private final CourseService courseService;
    public RecommendationService(EnrollmentRepository e,CourseRepository c,CourseService s){enrollments=e;courses=c;courseService=s;}
    @Transactional(readOnly=true) public List<RecommendationResponse> forStudent(User student){List<Long> own=enrollments.findByStudentId(student.getId()).stream().map(e->e.getCourse().getId()).toList();if(own.isEmpty())return courses.findAllByOrderByNameAsc().stream().limit(3).map(c->new RecommendationResponse(courseService.toResponse(c),0)).toList();return enrollments.findCoEnrollmentRecommendations(own).stream().limit(5).map(row->courses.findById(((Number)row[0]).longValue()).map(c->new RecommendationResponse(courseService.toResponse(c),((Number)row[1]).longValue())).orElse(null)).filter(Objects::nonNull).toList();}
}

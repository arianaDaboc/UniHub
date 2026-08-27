package ro.utcluj.unihub.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.utcluj.unihub.dto.ApiDtos.*;
import ro.utcluj.unihub.exception.*;
import ro.utcluj.unihub.model.*;
import ro.utcluj.unihub.repository.*;
import java.time.Instant;
import java.util.*;

@Service
public class ProgressService {
    private final AssignmentRepository assignments;
    private final AssignmentCompletionRepository completions;
    private final EnrollmentRepository enrollments;

    public ProgressService(AssignmentRepository assignments, AssignmentCompletionRepository completions,
                           EnrollmentRepository enrollments) {
        this.assignments = assignments; this.completions = completions; this.enrollments = enrollments;
    }

    @Transactional
    public CompletionResponse complete(Long assignmentId, User student) {
        requireStudent(student);
        Assignment assignment = assignment(assignmentId);
        requireEnrollment(student, assignment.getCourse().getId());
        if (completions.existsByStudentIdAndAssignmentId(student.getId(), assignmentId))
            throw new ConflictException("Assignment is already completed");
        AssignmentCompletion completion = completions.save(new AssignmentCompletion(student, assignment, Instant.now()));
        return new CompletionResponse(assignmentId, true, completion.getCompletedAt());
    }

    @Transactional
    public CompletionResponse undo(Long assignmentId, User student) {
        requireStudent(student);
        AssignmentCompletion completion = completions.findByStudentIdAndAssignmentId(student.getId(), assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment completion not found"));
        completions.delete(completion);
        return new CompletionResponse(assignmentId, false, null);
    }

    @Transactional(readOnly = true)
    public DashboardResponse dashboard(User student) {
        requireStudent(student);
        List<Enrollment> studentEnrollments = enrollments.findByStudentId(student.getId());
        List<Long> courseIds = studentEnrollments.stream().map(e -> e.getCourse().getId()).toList();
        List<CourseProgress> progress = studentEnrollments.stream().map(e -> {
            Course course = e.getCourse();
            return new CourseProgress(course.getId(), course.getName(),
                    completions.countByStudentIdAndAssignmentCourseId(student.getId(), course.getId()),
                    assignments.countByCourseId(course.getId()));
        }).toList();
        if (courseIds.isEmpty()) return new DashboardResponse(progress, List.of());

        Set<Long> completedIds = new HashSet<>(completions.findByStudentId(student.getId()).stream()
                .map(c -> c.getAssignment().getId()).toList());
        List<DashboardAssignment> upcoming = assignments
                .findByCourseIdInAndDeadlineAfterOrderByDeadlineAsc(courseIds, Instant.now()).stream().limit(10)
                .map(a -> new DashboardAssignment(a.getId(), a.getCourse().getId(), a.getCourse().getName(),
                        a.getTitle(), a.getDeadline(), AssignmentService.priority(a.getDeadline()), completedIds.contains(a.getId())))
                .toList();
        return new DashboardResponse(progress, upcoming);
    }

    private Assignment assignment(Long id) { return assignments.findById(id).orElseThrow(() -> new ResourceNotFoundException("Assignment not found")); }
    private void requireEnrollment(User student, Long courseId) {
        if (!enrollments.existsByStudentIdAndCourseId(student.getId(), courseId)) throw new AccessDeniedException("Enrollment required");
    }
    private void requireStudent(User user) { if (user.getRole() != Role.STUDENT) throw new AccessDeniedException("Student role required"); }
}


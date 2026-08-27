package ro.utcluj.unihub.dto;

import jakarta.validation.constraints.*;
import java.time.Instant;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import ro.utcluj.unihub.model.Role;

public final class ApiDtos {
    private ApiDtos() {}
    public record CourseRequest(@NotBlank @Size(max=160) String name, @Size(max=5000) String description,
                                @NotNull @Min(1) @Max(30) Integer ects, @NotBlank @Size(max=160) String faculty,
                                @NotBlank @Size(max=160) String department, @NotNull @Min(1) @Max(1000) Integer capacity) {}
    public record CourseResponse(Long id, String name, String description, Integer ects, String faculty,
                                 String department, Integer capacity, long enrolled, boolean enrolledByCurrentUser,
                                 Long professorId, String professorName) {}
    public record MaterialResponse(Long id,Long courseId,String title,String filename,String contentType,Long fileSize,Instant uploadedAt) {}
    public record PrerequisiteResponse(Long courseId,Long prerequisiteCourseId,String prerequisiteCourseName) {}
    public record ScheduleRequest(@NotNull DayOfWeek dayOfWeek,@NotNull LocalTime startTime,@NotNull LocalTime endTime,
                                  @NotBlank @Size(max=80) String room) {}
    public record ScheduleResponse(Long id,Long courseId,DayOfWeek dayOfWeek,LocalTime startTime,LocalTime endTime,String room) {}
    public record WeeklyScheduleResponse(Long id,Long courseId,String courseName,DayOfWeek dayOfWeek,
                                         LocalTime startTime,LocalTime endTime,String room) {}
    public record NotificationResponse(Long id,String type,String title,String message,boolean read,Instant createdAt) {}
    public record NotificationSummary(long unread,List<NotificationResponse> items) {}
    public record ReviewRequest(@NotNull @Min(1) @Max(5) Integer rating,@Size(max=2000) String comment) {}
    public record ReviewResponse(Long id,Long courseId,Long studentId,String studentName,Integer rating,String comment,Instant createdAt) {}
    public record RecommendationResponse(CourseResponse course,long sharedStudents) {}
    public record CourseDetailResponse(CourseResponse course,List<AssignmentResponse> assignments,List<MaterialResponse> materials,
                                       List<PrerequisiteResponse> prerequisites,List<ScheduleResponse> schedule,List<ReviewResponse> reviews,
                                       Double averageRating,long reviewCount) {}
    public record AssignmentRequest(@NotBlank @Size(max=200) String title, @Size(max=10000) String description,
                                    @NotNull @Future Instant deadline, @NotNull @Positive Integer maxPoints) {}
    public record AssignmentResponse(Long id, Long courseId, String title, String description, Instant deadline,
                                     Integer maxPoints, String priority) {}
    public record EnrollmentResponse(Long id, Long studentId, String studentName, Long courseId) {}
    public record UserResponse(Long id, String firstName, String lastName, String email, String role) {}
    public record RoleRequest(@NotNull Role role) {}
    public record CompletionResponse(Long assignmentId, boolean completed, Instant completedAt) {}
    public record CourseProgress(Long courseId, String courseName, long completed, long total) {}
    public record DashboardAssignment(Long id, Long courseId, String courseName, String title, Instant deadline,
                                      String priority, boolean completed) {}
    public record DashboardResponse(List<CourseProgress> courses, List<DashboardAssignment> upcomingDeadlines) {}
}

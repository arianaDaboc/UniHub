package ro.utcluj.unihub.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "enrollments", uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "course_id"}))
public class Enrollment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "student_id")
    private User student;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "course_id")
    private Course course;
    @Column(name = "enrolled_at", nullable = false)
    private Instant enrolledAt;

    protected Enrollment() {}
    public Enrollment(User student, Course course) { this.student = student; this.course = course; this.enrolledAt = Instant.now(); }
    public Long getId() { return id; }
    public User getStudent() { return student; }
    public Course getCourse() { return course; }
    public Instant getEnrolledAt() { return enrolledAt; }
}


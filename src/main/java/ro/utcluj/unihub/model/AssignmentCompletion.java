package ro.utcluj.unihub.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "assignment_completions", uniqueConstraints =
        @UniqueConstraint(columnNames = {"student_id", "assignment_id"}))
public class AssignmentCompletion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id")
    private User student;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;
    @Column(name = "completed_at", nullable = false)
    private Instant completedAt;

    protected AssignmentCompletion() {}
    public AssignmentCompletion(User student, Assignment assignment, Instant completedAt) {
        this.student = student; this.assignment = assignment; this.completedAt = completedAt;
    }
    public Long getId() { return id; }
    public User getStudent() { return student; }
    public Assignment getAssignment() { return assignment; }
    public Instant getCompletedAt() { return completedAt; }
}


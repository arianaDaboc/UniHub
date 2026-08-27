package ro.utcluj.unihub.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "assignments")
public class Assignment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "course_id")
    private Course course;
    @Column(nullable = false, length = 200)
    private String title;
    @Column(columnDefinition = "text")
    private String description;
    @Column(nullable = false)
    private Instant deadline;
    @Column(name = "max_points", nullable = false)
    private Integer maxPoints;

    protected Assignment() {}
    public Assignment(Course course, String title, String description, Instant deadline, Integer maxPoints) {
        this.course = course; this.title = title; this.description = description; this.deadline = deadline; this.maxPoints = maxPoints;
    }
    public Long getId() { return id; }
    public Course getCourse() { return course; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Instant getDeadline() { return deadline; }
    public Integer getMaxPoints() { return maxPoints; }
}

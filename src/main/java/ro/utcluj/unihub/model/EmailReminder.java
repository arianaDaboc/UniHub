package ro.utcluj.unihub.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "email_reminders", uniqueConstraints = @UniqueConstraint(columnNames = {"assignment_id", "user_id"}))
public class EmailReminder {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "assignment_id")
    private Assignment assignment;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "sent_at", nullable = false)
    private Instant sentAt;

    protected EmailReminder() {}
    public EmailReminder(Assignment assignment, User user) {
        this.assignment = assignment;
        this.user = user;
        this.sentAt = Instant.now();
    }
    public Long getId() { return id; }
    public Assignment getAssignment() { return assignment; }
    public User getUser() { return user; }
    public Instant getSentAt() { return sentAt; }
}

package ro.utcluj.unihub.model;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="notifications",uniqueConstraints=@UniqueConstraint(columnNames={"user_id","reference_key"}))
public class Notification {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="user_id") private User user;
    @Column(nullable=false,length=30) private String type;
    @Column(nullable=false,length=200) private String title;
    @Column(nullable=false,length=500) private String message;
    @Column(name="reference_key",nullable=false,length=160) private String referenceKey;
    @Column(name="is_read",nullable=false) private boolean read;
    @Column(name="created_at",nullable=false) private Instant createdAt;
    protected Notification(){} public Notification(User user,String type,String title,String message,String key){this.user=user;this.type=type;this.title=title;this.message=message;this.referenceKey=key;this.createdAt=Instant.now();}
    public Long getId(){return id;}public User getUser(){return user;}public String getType(){return type;}public String getTitle(){return title;}public String getMessage(){return message;}public String getReferenceKey(){return referenceKey;}public boolean isRead(){return read;}public Instant getCreatedAt(){return createdAt;}public void markRead(){read=true;}
}


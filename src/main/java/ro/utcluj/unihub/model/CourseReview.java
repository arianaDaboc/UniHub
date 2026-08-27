package ro.utcluj.unihub.model;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="course_reviews",uniqueConstraints=@UniqueConstraint(columnNames={"course_id","student_id"}))
public class CourseReview {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="course_id") private Course course;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="student_id") private User student;
    @Column(nullable=false) private Integer rating;
    @Column(length=2000) private String comment;
    @Column(name="created_at",nullable=false) private Instant createdAt;
    @Column(name="updated_at",nullable=false) private Instant updatedAt;
    protected CourseReview(){} public CourseReview(Course course,User student,Integer rating,String comment){this.course=course;this.student=student;this.rating=rating;this.comment=comment;this.createdAt=Instant.now();this.updatedAt=createdAt;}
    public Long getId(){return id;}public Course getCourse(){return course;}public User getStudent(){return student;}public Integer getRating(){return rating;}public String getComment(){return comment;}public Instant getCreatedAt(){return createdAt;}public Instant getUpdatedAt(){return updatedAt;}
    public void update(Integer rating,String comment){this.rating=rating;this.comment=comment;this.updatedAt=Instant.now();}
}

package ro.utcluj.unihub.model;
import jakarta.persistence.*;
@Entity @Table(name="course_prerequisites",uniqueConstraints=@UniqueConstraint(columnNames={"course_id","prerequisite_course_id"}))
public class CoursePrerequisite {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="course_id") private Course course;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="prerequisite_course_id") private Course prerequisiteCourse;
    protected CoursePrerequisite(){} public CoursePrerequisite(Course course,Course prerequisite){this.course=course;this.prerequisiteCourse=prerequisite;}
    public Long getId(){return id;} public Course getCourse(){return course;} public Course getPrerequisiteCourse(){return prerequisiteCourse;}
}


package ro.utcluj.unihub.model;
import jakarta.persistence.*;
import java.time.*;
@Entity @Table(name="course_schedules")
public class CourseSchedule {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="course_id") private Course course;
    @Enumerated(EnumType.STRING) @Column(name="day_of_week",nullable=false,length=10) private DayOfWeek dayOfWeek;
    @Column(name="start_time",nullable=false) private LocalTime startTime;
    @Column(name="end_time",nullable=false) private LocalTime endTime;
    @Column(nullable=false,length=80) private String room;
    protected CourseSchedule(){} public CourseSchedule(Course course,DayOfWeek day,LocalTime start,LocalTime end,String room){this.course=course;this.dayOfWeek=day;this.startTime=start;this.endTime=end;this.room=room;}
    public Long getId(){return id;} public Course getCourse(){return course;} public DayOfWeek getDayOfWeek(){return dayOfWeek;}
    public LocalTime getStartTime(){return startTime;} public LocalTime getEndTime(){return endTime;} public String getRoom(){return room;}
}

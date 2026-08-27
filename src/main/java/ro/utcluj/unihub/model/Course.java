package ro.utcluj.unihub.model;

import jakarta.persistence.*;

@Entity
@Table(name = "courses")
public class Course {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 160)
    private String name;
    @Column(columnDefinition = "text")
    private String description;
    @Column(nullable = false)
    private Integer ects;
    @Column(nullable = false, length = 160)
    private String faculty;
    @Column(nullable = false, length = 160)
    private String department;
    @Column(nullable = false)
    private Integer capacity;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "professor_id")
    private User professor;

    protected Course() {}
    public Course(String name, String description, Integer ects, String faculty, String department, Integer capacity, User professor) {
        this.name = name; this.description = description; this.ects = ects; this.faculty = faculty;
        this.department = department; this.capacity = capacity; this.professor = professor;
    }
    public Course(String name, String description, User professor) {
        this(name, description, 5, "Faculty of Automation and Computer Science", "Computer Science", 120, professor);
    }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Integer getEcts() { return ects; }
    public String getFaculty() { return faculty; }
    public String getDepartment() { return department; }
    public Integer getCapacity() { return capacity; }
    public User getProfessor() { return professor; }
}

package ro.utcluj.unihub.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "materials")
public class Material {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "course_id") private Course course;
    @Column(nullable = false, length = 200) private String title;
    @Column(name = "original_filename", nullable = false) private String originalFilename;
    @Column(name = "stored_filename", nullable = false, unique = true) private String storedFilename;
    @Column(name = "content_type", nullable = false, length = 120) private String contentType;
    @Column(name = "file_size", nullable = false) private Long fileSize;
    @Column(name = "uploaded_at", nullable = false) private Instant uploadedAt;
    protected Material() {}
    public Material(Course course,String title,String originalFilename,String storedFilename,String contentType,Long fileSize,Instant uploadedAt){this.course=course;this.title=title;this.originalFilename=originalFilename;this.storedFilename=storedFilename;this.contentType=contentType;this.fileSize=fileSize;this.uploadedAt=uploadedAt;}
    public Long getId(){return id;} public Course getCourse(){return course;} public String getTitle(){return title;}
    public String getOriginalFilename(){return originalFilename;} public String getStoredFilename(){return storedFilename;}
    public String getContentType(){return contentType;} public Long getFileSize(){return fileSize;} public Instant getUploadedAt(){return uploadedAt;}
}


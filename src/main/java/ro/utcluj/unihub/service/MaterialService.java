package ro.utcluj.unihub.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ro.utcluj.unihub.dto.ApiDtos.MaterialResponse;
import ro.utcluj.unihub.exception.BadRequestException;
import ro.utcluj.unihub.exception.ResourceNotFoundException;
import ro.utcluj.unihub.model.Course;
import ro.utcluj.unihub.model.Material;
import ro.utcluj.unihub.model.User;
import ro.utcluj.unihub.repository.MaterialRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MaterialService {
    static final long MAX_FILE_SIZE = 10L * 1024 * 1024;
    private static final byte[] PDF_SIGNATURE = {'%', 'P', 'D', 'F', '-'};

    private final MaterialRepository materials;
    private final CourseService courses;
    private final Path root;
    private final NotificationService notifications;

    public MaterialService(MaterialRepository materials, CourseService courses, NotificationService notifications,
                           @Value("${app.storage.uploads-directory}") String directory) {
        this.materials = materials;
        this.courses = courses;
        this.notifications = notifications;
        this.root = Paths.get(directory).toAbsolutePath().normalize();
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new IllegalStateException("Could not initialize upload directory", e);
        }
    }

    @Transactional
    public MaterialResponse upload(Long courseId, String title, MultipartFile file, User user) {
        Course course = courses.find(courseId);
        courses.assertOwnerOrAdmin(course, user);
        validate(file);

        String type = Optional.ofNullable(file.getContentType()).orElse("application/octet-stream");
        String original = file.getOriginalFilename();
        String stored = UUID.randomUUID() + ".pdf";
        Path target = root.resolve(stored).normalize();
        if (!target.startsWith(root)) throw new SecurityException("Invalid storage path");

        try {
            Files.copy(file.getInputStream(), target);
        } catch (IOException e) {
            throw new IllegalStateException("The material could not be saved", e);
        }

        Material saved = materials.save(new Material(course, title.trim(), original, stored, type, file.getSize(), Instant.now()));
        notifications.notifyCourse(courseId, "MATERIAL", "New material",
                saved.getTitle() + " · " + course.getName(), "material:" + saved.getId());
        return dto(saved);
    }

    private void validate(MultipartFile file) {
        if (file.isEmpty()) throw new BadRequestException("Select a file to upload");
        if (file.getSize() > MAX_FILE_SIZE) throw new BadRequestException("The file cannot exceed 10 MB");
        if (!"application/pdf".equalsIgnoreCase(file.getContentType()))
            throw new BadRequestException("Only PDF files are accepted");

        String original = file.getOriginalFilename();
        if (original == null || original.isBlank() || original.contains("..") || original.contains("/") || original.contains("\\"))
            throw new BadRequestException("The file name is invalid");

        try (var input = file.getInputStream()) {
            if (!Arrays.equals(input.readNBytes(PDF_SIGNATURE.length), PDF_SIGNATURE))
                throw new BadRequestException("The file content is not a valid PDF");
        } catch (IOException e) {
            throw new BadRequestException("The file could not be read");
        }
    }

    @Transactional(readOnly = true)
    public List<MaterialResponse> byCourse(Long courseId) {
        courses.find(courseId);
        return materials.findByCourseIdOrderByUploadedAtDesc(courseId).stream().map(this::dto).toList();
    }

    @Transactional(readOnly = true)
    public Download download(Long id) {
        Material material = materials.findById(id).orElseThrow(() -> new ResourceNotFoundException("Material not found"));
        Resource resource = new FileSystemResource(root.resolve(material.getStoredFilename()));
        if (!resource.exists()) throw new ResourceNotFoundException("Material file not found");
        return new Download(resource, material.getOriginalFilename(), material.getContentType());
    }

    private MaterialResponse dto(Material material) {
        return new MaterialResponse(material.getId(), material.getCourse().getId(), material.getTitle(),
                material.getOriginalFilename(), material.getContentType(), material.getFileSize(), material.getUploadedAt());
    }

    public record Download(Resource resource, String filename, String contentType) {}
}

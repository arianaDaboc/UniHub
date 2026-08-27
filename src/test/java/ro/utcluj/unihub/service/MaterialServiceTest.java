package ro.utcluj.unihub.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;
import ro.utcluj.unihub.exception.BadRequestException;
import ro.utcluj.unihub.model.Course;
import ro.utcluj.unihub.model.Material;
import ro.utcluj.unihub.model.User;
import ro.utcluj.unihub.repository.MaterialRepository;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MaterialServiceTest {
    @TempDir Path uploads;

    @Test
    void upload_whenUserDoesNotOwnCourse_doesNotPersistFileOrMetadata() {
        MaterialRepository materials = mock(MaterialRepository.class);
        CourseService courses = mock(CourseService.class);
        NotificationService notifications = mock(NotificationService.class);
        Course course = mock(Course.class);
        User otherProfessor = mock(User.class);
        when(courses.find(7L)).thenReturn(course);
        doThrow(new AccessDeniedException("You do not own this course"))
                .when(courses).assertOwnerOrAdmin(course, otherProfessor);
        MaterialService service = new MaterialService(materials, courses, notifications, uploads.toString());

        assertThrows(AccessDeniedException.class,
                () -> service.upload(7L, "Curs", pdf("curs.pdf"), otherProfessor));

        verify(materials, never()).save(any());
        assertDirectoryEntries(0);
    }

    @Test
    void upload_persistsSafeNameAndCorrectMetadata() {
        MaterialRepository materials = mock(MaterialRepository.class);
        CourseService courses = mock(CourseService.class);
        NotificationService notifications = mock(NotificationService.class);
        Course course = mock(Course.class);
        User professor = mock(User.class);
        when(course.getId()).thenReturn(7L);
        when(course.getName()).thenReturn("Baze de date");
        when(courses.find(7L)).thenReturn(course);
        when(materials.save(any(Material.class))).thenAnswer(invocation -> invocation.getArgument(0));
        MaterialService service = new MaterialService(materials, courses, notifications, uploads.toString());

        service.upload(7L, "  Curs 1  ", pdf("curs1.pdf"), professor);

        verify(materials).save(argThat(material ->
                material.getTitle().equals("Curs 1") &&
                material.getOriginalFilename().equals("curs1.pdf") &&
                material.getStoredFilename().matches("[0-9a-f-]{36}\\.pdf") &&
                material.getContentType().equals("application/pdf") &&
                material.getFileSize() > 0));
        assertDirectoryEntries(1);
    }

    @Test
    void upload_whenContentDoesNotHavePdfSignature_rejectsFile() {
        MaterialRepository materials = mock(MaterialRepository.class);
        CourseService courses = mock(CourseService.class);
        when(courses.find(7L)).thenReturn(mock(Course.class));
        MaterialService service = new MaterialService(materials, courses, mock(NotificationService.class), uploads.toString());
        var fakePdf = new MockMultipartFile("file", "virus.pdf", "application/pdf", "MZ executable".getBytes());

        assertThrows(BadRequestException.class,
                () -> service.upload(7L, "Fișier", fakePdf, mock(User.class)));
        verify(materials, never()).save(any());
        assertDirectoryEntries(0);
    }

    @Test
    void upload_whenOriginalNameContainsPathTraversal_rejectsFile() {
        MaterialRepository materials = mock(MaterialRepository.class);
        CourseService courses = mock(CourseService.class);
        when(courses.find(7L)).thenReturn(mock(Course.class));
        MaterialService service = new MaterialService(materials, courses, mock(NotificationService.class), uploads.toString());

        assertThrows(BadRequestException.class,
                () -> service.upload(7L, "Fișier", pdf("../secret.pdf"), mock(User.class)));
        verify(materials, never()).save(any());
        assertDirectoryEntries(0);
    }

    private MockMultipartFile pdf(String filename) {
        return new MockMultipartFile("file", filename, "application/pdf", "%PDF-1.7\ncontent".getBytes());
    }

    private void assertDirectoryEntries(long expected) {
        try (var files = java.nio.file.Files.list(uploads)) {
            assertEquals(expected, files.count());
        } catch (java.io.IOException e) {
            throw new AssertionError(e);
        }
    }
}

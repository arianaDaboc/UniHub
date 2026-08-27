package ro.utcluj.unihub.service;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import ro.utcluj.unihub.model.*;
import ro.utcluj.unihub.repository.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class ProgressServiceTest {
    private final ProgressService service = new ProgressService(mock(AssignmentRepository.class),
            mock(AssignmentCompletionRepository.class), mock(EnrollmentRepository.class));

    @Test void professorsCannotUseStudentProgress() {
        User professor = new User("Grace", "Hopper", "grace@example.com", "hash", Role.PROFESSOR);
        assertThatThrownBy(() -> service.dashboard(professor)).isInstanceOf(AccessDeniedException.class)
                .hasMessage("Student role required");
    }
}

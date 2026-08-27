package ro.utcluj.unihub.service;
import org.junit.jupiter.api.Test;
import java.time.*;
import java.time.temporal.ChronoUnit;
import static org.assertj.core.api.Assertions.assertThat;
class AssignmentServiceTest {
    @Test void classifiesDeadlinesByRemainingTime() {
        assertThat(AssignmentService.priority(Instant.now().plus(12, ChronoUnit.HOURS))).isEqualTo("URGENT");
        assertThat(AssignmentService.priority(Instant.now().plus(48, ChronoUnit.HOURS))).isEqualTo("SOON");
        assertThat(AssignmentService.priority(Instant.now().plus(96, ChronoUnit.HOURS))).isEqualTo("OK");
    }
}

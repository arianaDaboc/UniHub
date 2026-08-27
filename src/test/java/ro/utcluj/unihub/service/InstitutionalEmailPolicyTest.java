package ro.utcluj.unihub.service;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class InstitutionalEmailPolicyTest {
    private final InstitutionalEmailPolicy policy = new InstitutionalEmailPolicy(List.of("gmail.com", "yahoo.com", "outlook.com"));

    @Test void acceptsOnlyConfiguredInstitutionalDomains() {
        assertThat(policy.isAllowed("student@student.utcluj.ro")).isTrue();
        assertThat(policy.isAllowed("student@stud.ase.ro")).isTrue();
        assertThat(policy.isAllowed("student@edu.unibuc.ro")).isTrue();
        assertThat(policy.isAllowed("student@gmail.com")).isFalse();
        assertThat(policy.isAllowed("student@yahoo.com")).isFalse();
    }
}

package ro.utcluj.unihub.security;
import org.junit.jupiter.api.Test;
import ro.utcluj.unihub.model.*;
import static org.assertj.core.api.Assertions.assertThat;
class JwtServiceTest {
    @Test void tokenContainsAuthenticatedUser() {
        JwtService jwt=new JwtService("a-test-secret-key-that-is-longer-than-32-bytes",30);
        User user=new User("Ana","Pop","ana@example.com","hash",Role.STUDENT);
        assertThat(jwt.extractSubject(jwt.generate(user))).isEqualTo("ana@example.com");
    }
}

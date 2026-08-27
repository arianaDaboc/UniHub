package ro.utcluj.unihub.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ro.utcluj.unihub.model.User;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey key;
    private final Duration expiration;
    public JwtService(@Value("${app.jwt.secret}") String secret, @Value("${app.jwt.expiration-minutes}") long minutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = Duration.ofMinutes(minutes);
    }
    public String generate(User user) {
        Instant now = Instant.now();
        return Jwts.builder().subject(user.getEmail()).claim("role", user.getRole().name()).claim("uid", user.getId())
                .issuedAt(Date.from(now)).expiration(Date.from(now.plus(expiration))).signWith(key).compact();
    }
    public String extractSubject(String token) { return parser().parseSignedClaims(token).getPayload().getSubject(); }
    private JwtParser parser() { return Jwts.parser().verifyWith(key).build(); }
}


package ro.utcluj.unihub.service;

import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.utcluj.unihub.dto.AuthDtos.*;
import ro.utcluj.unihub.exception.*;
import ro.utcluj.unihub.model.*;
import ro.utcluj.unihub.repository.UserRepository;
import ro.utcluj.unihub.security.JwtService;

@Service
public class AuthService {
    private final UserRepository users; private final PasswordEncoder encoder; private final AuthenticationManager auth; private final JwtService jwt; private final InstitutionalEmailPolicy emailPolicy;
    public AuthService(UserRepository users, PasswordEncoder encoder, AuthenticationManager auth, JwtService jwt, InstitutionalEmailPolicy emailPolicy) { this.users=users; this.encoder=encoder; this.auth=auth; this.jwt=jwt; this.emailPolicy=emailPolicy; }
    @Transactional public AuthResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (!emailPolicy.isAllowed(email)) throw new BadRequestException("Use the email address provided by your university");
        if (users.existsByEmailIgnoreCase(email)) throw new ConflictException("Email is already registered");
        User user = users.save(new User(request.firstName().trim(), request.lastName().trim(), email, encoder.encode(request.password()), Role.STUDENT));
        return response(user);
    }
    public AuthResponse login(LoginRequest request) {
        auth.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        return response(users.findByEmailIgnoreCase(request.email()).orElseThrow());
    }
    private AuthResponse response(User u) { return new AuthResponse(jwt.generate(u), u.getId(), u.getFirstName(), u.getLastName(), u.getEmail(), u.getRole()); }
}

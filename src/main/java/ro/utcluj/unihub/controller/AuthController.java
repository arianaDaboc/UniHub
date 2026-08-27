package ro.utcluj.unihub.controller;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import ro.utcluj.unihub.dto.AuthDtos.*;
import ro.utcluj.unihub.service.AuthService;
@RestController @RequestMapping("/api/auth")
public class AuthController {
    private final AuthService service; public AuthController(AuthService service) { this.service=service; }
    @PostMapping("/register") ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest r) { return ResponseEntity.status(201).body(service.register(r)); }
    @PostMapping("/login") AuthResponse login(@Valid @RequestBody LoginRequest r) { return service.login(r); }
}

package ro.utcluj.unihub.service;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ro.utcluj.unihub.exception.ResourceNotFoundException;
import ro.utcluj.unihub.model.User;
import ro.utcluj.unihub.repository.UserRepository;
@Service
public class CurrentUserService {
    private final UserRepository users; public CurrentUserService(UserRepository users) { this.users=users; }
    public User get(Authentication auth) { return users.findByEmailIgnoreCase(auth.getName()).orElseThrow(() -> new ResourceNotFoundException("User not found")); }
}


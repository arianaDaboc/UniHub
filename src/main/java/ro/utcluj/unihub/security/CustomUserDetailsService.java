package ro.utcluj.unihub.security;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import ro.utcluj.unihub.model.User;
import ro.utcluj.unihub.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository users;
    public CustomUserDetailsService(UserRepository users) { this.users = users; }
    @Override public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = users.findByEmailIgnoreCase(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User.withUsername(user.getEmail()).password(user.getPassword())
                .roles(user.getRole().name()).build();
    }
}


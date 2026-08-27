package ro.utcluj.unihub.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.utcluj.unihub.dto.ApiDtos.UserResponse;
import ro.utcluj.unihub.dto.ApiDtos.RoleRequest;
import ro.utcluj.unihub.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import ro.utcluj.unihub.repository.*;
import java.util.List;
@RestController @RequestMapping("/api/admin")
public class AdminController {
    private final UserRepository users; private final CourseRepository courses;
    public AdminController(UserRepository u,CourseRepository c){users=u;courses=c;}
    @GetMapping("/users") List<UserResponse> users(){return users.findAll().stream().map(u->new UserResponse(u.getId(),u.getFirstName(),u.getLastName(),u.getEmail(),u.getRole().name())).toList();}
    @DeleteMapping("/courses/{id}") ResponseEntity<Void> deleteCourse(@PathVariable("id") Long id){courses.deleteById(id);return ResponseEntity.noContent().build();}
    @PatchMapping("/users/{id}/role") UserResponse role(@PathVariable("id") Long id,@Valid @RequestBody RoleRequest request){
        var user=users.findById(id).orElseThrow(()->new ResourceNotFoundException("User not found"));
        user.setRole(request.role()); users.save(user);
        return new UserResponse(user.getId(),user.getFirstName(),user.getLastName(),user.getEmail(),user.getRole().name());
    }
}

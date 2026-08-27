package ro.utcluj.unihub.controller;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.ObjectProvider;
import ro.utcluj.unihub.dto.ApiDtos.*;
import ro.utcluj.unihub.model.User;
import ro.utcluj.unihub.service.*;
import java.util.List;
@RestController @RequestMapping("/api/courses")
public class CourseController {
    private final CourseService courses; private final AssignmentService assignments; private final CurrentUserService current; private final MaterialService materials; private final CourseResourceService resources;private final ReviewService reviews;private final ObjectProvider<EmailReminderService> emailReminders;
    public CourseController(CourseService c, AssignmentService a, CurrentUserService u,MaterialService m,CourseResourceService r,ReviewService reviews,ObjectProvider<EmailReminderService> emailReminders) { courses=c; assignments=a; current=u;materials=m;resources=r;this.reviews=reviews;this.emailReminders=emailReminders; }
    @GetMapping List<CourseResponse> all(@RequestParam(name="search",required=false) String search,
            @RequestParam(name="faculty",required=false) String faculty, @RequestParam(name="department",required=false) String department,
            @RequestParam(name="sort",defaultValue="name") String sort,Authentication auth) { return courses.all(search,faculty,department,sort,current.get(auth)); }
    @GetMapping("/{id}") CourseDetailResponse detail(@PathVariable("id") Long id,Authentication auth) {
        CourseResponse course = courses.all(null,null,null,"name",current.get(auth)).stream().filter(c -> c.id().equals(id))
                .findFirst().orElseThrow(() -> new ro.utcluj.unihub.exception.ResourceNotFoundException("Course not found"));
        return new CourseDetailResponse(course, assignments.byCourse(id),materials.byCourse(id),resources.prerequisites(id),resources.schedule(id),reviews.byCourse(id),reviews.average(id),reviews.count(id));
    }
    @PostMapping ResponseEntity<CourseResponse> create(@Valid @RequestBody CourseRequest r, Authentication a) { return ResponseEntity.status(201).body(courses.create(r,current.get(a))); }
    @PostMapping("/{id}/enroll") ResponseEntity<EnrollmentResponse> enroll(@PathVariable("id") Long id, Authentication a) { return ResponseEntity.status(201).body(courses.enroll(id,current.get(a))); }
    @GetMapping("/{id}/students") List<EnrollmentResponse> students(@PathVariable("id") Long id, Authentication a) { return courses.students(id,current.get(a)); }
    @GetMapping("/{id}/assignments") List<AssignmentResponse> assignments(@PathVariable("id") Long id) { return assignments.byCourse(id); }
    @PostMapping("/{id}/assignments") ResponseEntity<AssignmentResponse> assignment(@PathVariable("id") Long id,@Valid @RequestBody AssignmentRequest r,Authentication a) { return ResponseEntity.status(201).body(assignments.create(id,r,current.get(a))); }
    @PostMapping(value="/{id}/materials",consumes=MediaType.MULTIPART_FORM_DATA_VALUE) ResponseEntity<MaterialResponse> material(@PathVariable("id") Long id,@RequestParam("title") String title,@RequestPart("file") MultipartFile file,Authentication a){return ResponseEntity.status(201).body(materials.upload(id,title,file,current.get(a)));}
    @GetMapping("/{id}/materials") List<MaterialResponse> materials(@PathVariable("id") Long id){return materials.byCourse(id);}
    @PostMapping("/{id}/prerequisites/{requiredId}") ResponseEntity<PrerequisiteResponse> prerequisite(@PathVariable("id") Long id,@PathVariable("requiredId") Long requiredId,Authentication a){return ResponseEntity.status(201).body(resources.addPrerequisite(id,requiredId,current.get(a)));}
    @DeleteMapping("/{id}/prerequisites/{requiredId}") ResponseEntity<Void> removePrerequisite(@PathVariable("id") Long id,@PathVariable("requiredId") Long requiredId,Authentication a){resources.removePrerequisite(id,requiredId,current.get(a));return ResponseEntity.noContent().build();}
    @PostMapping("/{id}/schedule") ResponseEntity<ScheduleResponse> schedule(@PathVariable("id") Long id,@Valid @RequestBody ScheduleRequest r,Authentication a){return ResponseEntity.status(201).body(resources.addSchedule(id,r,current.get(a)));}
    @PostMapping("/{id}/reviews") ReviewResponse review(@PathVariable("id") Long id,@Valid @RequestBody ReviewRequest r,Authentication a){return reviews.save(id,r,current.get(a));}
    @PostMapping("/{id}/email/test") ResponseEntity<java.util.Map<String,Integer>> testEmail(@PathVariable("id") Long id,Authentication a){User user=current.get(a);var course=courses.find(id);courses.assertOwnerOrAdmin(course,user);var service=emailReminders.getIfAvailable();if(service==null)throw new ro.utcluj.unihub.exception.BadRequestException("Email reminders are not enabled");int sent=service.sendCourseTest(course);if(sent==0)throw new ro.utcluj.unihub.exception.BadRequestException("No students are enrolled in this course");return ResponseEntity.ok(java.util.Map.of("sent",sent));}
}

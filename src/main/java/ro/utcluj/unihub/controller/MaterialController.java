package ro.utcluj.unihub.controller;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import ro.utcluj.unihub.service.MaterialService;
@RestController @RequestMapping("/api/materials")
public class MaterialController {
    private final MaterialService materials;public MaterialController(MaterialService materials){this.materials=materials;}
    @GetMapping("/{id}/download") ResponseEntity<org.springframework.core.io.Resource> download(@PathVariable("id") Long id){var d=materials.download(id);return ResponseEntity.ok().contentType(MediaType.parseMediaType(d.contentType())).header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename*=UTF-8''"+java.net.URLEncoder.encode(d.filename(),java.nio.charset.StandardCharsets.UTF_8)).body(d.resource());}
}

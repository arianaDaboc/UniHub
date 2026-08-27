package ro.utcluj.unihub.controller;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ro.utcluj.unihub.dto.ApiDtos.RecommendationResponse;
import ro.utcluj.unihub.service.*;
import java.util.List;
@RestController @RequestMapping("/api/recommendations")
public class RecommendationController {
    private final RecommendationService recommendations;private final CurrentUserService current;
    public RecommendationController(RecommendationService r,CurrentUserService c){recommendations=r;current=c;}
    @GetMapping List<RecommendationResponse> list(Authentication a){return recommendations.forStudent(current.get(a));}
}

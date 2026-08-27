package ro.utcluj.unihub.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ro.utcluj.unihub.dto.ApiDtos.*;
import ro.utcluj.unihub.service.*;

@RestController
@RequestMapping("/api")
public class ProgressController {
    private final ProgressService progress; private final CurrentUserService current; private final CalendarExportService calendar;private final CourseResourceService resources;
    public ProgressController(ProgressService progress, CurrentUserService current,CalendarExportService calendar,CourseResourceService resources) { this.progress = progress; this.current = current;this.calendar=calendar;this.resources=resources; }
    @PutMapping("/assignments/{id}/completion") CompletionResponse complete(@PathVariable("id") Long id, Authentication auth) { return progress.complete(id, current.get(auth)); }
    @DeleteMapping("/assignments/{id}/completion") CompletionResponse undo(@PathVariable("id") Long id, Authentication auth) { return progress.undo(id, current.get(auth)); }
    @GetMapping("/dashboard") DashboardResponse dashboard(Authentication auth) { return progress.dashboard(current.get(auth)); }
    @GetMapping(value="/calendar/export.ics",produces="text/calendar") org.springframework.http.ResponseEntity<String> calendar(Authentication auth){return org.springframework.http.ResponseEntity.ok().header("Content-Disposition","attachment; filename=unihub-calendar.ics").body(calendar.export(current.get(auth)));}
    @GetMapping("/schedule") java.util.List<WeeklyScheduleResponse> schedule(Authentication auth){return resources.weeklySchedule(current.get(auth));}
}

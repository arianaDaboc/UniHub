package ro.utcluj.unihub.service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.utcluj.unihub.model.*;
import ro.utcluj.unihub.repository.*;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
@Service
public class CalendarExportService {
    private final EnrollmentRepository enrollments;private final AssignmentRepository assignments;
    public CalendarExportService(EnrollmentRepository e,AssignmentRepository a){enrollments=e;assignments=a;}
    @Transactional(readOnly=true) public String export(User student){List<Long> ids=enrollments.findByStudentId(student.getId()).stream().map(e->e.getCourse().getId()).toList();StringBuilder out=new StringBuilder("BEGIN:VCALENDAR\r\nVERSION:2.0\r\nPRODID:-//UniHub//Academic Calendar//RO\r\nCALSCALE:GREGORIAN\r\n");if(!ids.isEmpty())for(Assignment a:assignments.findByCourseIdInAndDeadlineAfterOrderByDeadlineAsc(ids,java.time.Instant.EPOCH)){String stamp=DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'").withZone(ZoneOffset.UTC).format(a.getDeadline());out.append("BEGIN:VEVENT\r\nUID:assignment-").append(a.getId()).append("@unihub\r\nDTSTAMP:").append(stamp).append("\r\nDTSTART:").append(stamp).append("\r\nSUMMARY:").append(escape(a.getTitle()+" - "+a.getCourse().getName())).append("\r\nDESCRIPTION:").append(escape(a.getDescription())).append("\r\nEND:VEVENT\r\n");}return out.append("END:VCALENDAR\r\n").toString();}
    private String escape(String value){return value==null?"":value.replace("\\","\\\\").replace(";","\\;").replace(",","\\,").replace("\n","\\n");}
}

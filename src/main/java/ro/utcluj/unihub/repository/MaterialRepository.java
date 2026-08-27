package ro.utcluj.unihub.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ro.utcluj.unihub.model.Material;
import java.util.List;
public interface MaterialRepository extends JpaRepository<Material,Long>{List<Material> findByCourseIdOrderByUploadedAtDesc(Long courseId);}


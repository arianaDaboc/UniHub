package ro.utcluj.unihub.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ro.utcluj.unihub.model.Notification;
import java.util.*;
public interface NotificationRepository extends JpaRepository<Notification,Long>{
    List<Notification> findTop50ByUserIdOrderByCreatedAtDesc(Long userId);
    boolean existsByUserIdAndReferenceKey(Long userId,String referenceKey);
    long countByUserIdAndReadFalse(Long userId);
}


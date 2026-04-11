package TKPMHDT.Repository.nguoidung;

import TKPMHDT.Entity.nguoidung.PasswordHistory;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, UUID> {
    List<PasswordHistory> findTop3ByNguoiDungIdOrderByCreatedAtDesc(UUID nguoiDungId);
}

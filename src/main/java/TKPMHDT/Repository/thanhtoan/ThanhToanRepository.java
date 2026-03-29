package TKPMHDT.Repository.thanhtoan;

import TKPMHDT.Entity.thanhtoan.ThanhToan;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThanhToanRepository extends JpaRepository<ThanhToan, UUID> {
    Optional<ThanhToan> findByDonHangId(UUID donHangId);
}


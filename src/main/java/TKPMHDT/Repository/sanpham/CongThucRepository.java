package TKPMHDT.Repository.sanpham;

import TKPMHDT.Entity.sanpham.CongThuc;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CongThucRepository extends JpaRepository<CongThuc, UUID> {
}


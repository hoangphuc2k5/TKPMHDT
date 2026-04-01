package TKPMHDT.Repository.sanpham;

import TKPMHDT.Entity.sanpham.LichSuKho;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LichSuKhoRepository extends JpaRepository<LichSuKho, UUID> {
    List<LichSuKho> findTop100ByOrderByThoiGianDesc();
}

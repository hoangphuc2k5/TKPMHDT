package TKPMHDT.Repository.donhang;

import TKPMHDT.Entity.donhang.ChiTietDonHang;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChiTietDonHangRepository extends JpaRepository<ChiTietDonHang, UUID> {
    List<ChiTietDonHang> findByDonHangId(UUID donHangId);
}


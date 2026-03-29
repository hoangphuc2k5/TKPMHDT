package TKPMHDT.Repository.giohang;

import TKPMHDT.Entity.giohang.ChiTietGioHang;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChiTietGioHangRepository extends JpaRepository<ChiTietGioHang, UUID> {
    List<ChiTietGioHang> findByGioHangId(UUID gioHangId);
}


package TKPMHDT.Repository.giohang;

import TKPMHDT.Entity.giohang.GioHang;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GioHangRepository extends JpaRepository<GioHang, UUID> {
    Optional<GioHang> findByKhachHangId(UUID khachHangId);
}


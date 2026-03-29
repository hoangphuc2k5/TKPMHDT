package TKPMHDT.Repository.nguoidung;

import TKPMHDT.Entity.nguoidung.NhanVienBanHang;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NhanVienBanHangRepository extends JpaRepository<NhanVienBanHang, UUID> {
}


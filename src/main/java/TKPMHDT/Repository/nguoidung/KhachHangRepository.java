package TKPMHDT.Repository.nguoidung;

import TKPMHDT.Entity.nguoidung.KhachHang;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KhachHangRepository extends JpaRepository<KhachHang, UUID> {
}


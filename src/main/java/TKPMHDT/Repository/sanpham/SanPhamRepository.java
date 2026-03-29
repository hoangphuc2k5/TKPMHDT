package TKPMHDT.Repository.sanpham;

import TKPMHDT.Entity.sanpham.SanPham;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SanPhamRepository extends JpaRepository<SanPham, UUID> {
}


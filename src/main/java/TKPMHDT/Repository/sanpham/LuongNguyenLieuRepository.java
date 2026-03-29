package TKPMHDT.Repository.sanpham;

import TKPMHDT.Entity.sanpham.LuongNguyenLieu;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LuongNguyenLieuRepository extends JpaRepository<LuongNguyenLieu, UUID> {
}


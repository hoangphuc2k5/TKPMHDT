package TKPMHDT.Repository.sanpham;

import TKPMHDT.Entity.sanpham.NguyenLieu;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NguyenLieuRepository extends JpaRepository<NguyenLieu, UUID> {
    List<NguyenLieu> findByTenContainingIgnoreCase(String ten);
}


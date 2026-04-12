package TKPMHDT.Repository.sanpham;

import TKPMHDT.Entity.sanpham.NuocUongSan;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NuocUongSanRepository extends JpaRepository<NuocUongSan, UUID> {
    List<NuocUongSan> findByTenContainingIgnoreCase(String ten);
    List<NuocUongSan> findByDanhMucIgnoreCase(String danhMuc);
    long countByCongThucCoBanId(UUID congThucId);

    /** Tải công thức gốc + từng dòng định lượng + nguyên liệu (tránh lazy / thiếu dữ liệu khi trả JSON). */
    @Query(
            "SELECT DISTINCT n FROM NuocUongSan n "
                    + "LEFT JOIN FETCH n.congThucCoBan c "
                    + "LEFT JOIN FETCH c.luongNguyenLieus l "
                    + "LEFT JOIN FETCH l.nguyenLieu "
                    + "WHERE n.id = :id")
    Optional<NuocUongSan> findByIdWithCongThucVaLuongNguyenLieu(@Param("id") UUID id);
}


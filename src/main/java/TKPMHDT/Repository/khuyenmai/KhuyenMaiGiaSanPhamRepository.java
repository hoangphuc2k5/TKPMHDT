package TKPMHDT.Repository.khuyenmai;

import TKPMHDT.Entity.khuyenmai.KhuyenMaiGiaSanPham;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface KhuyenMaiGiaSanPhamRepository extends JpaRepository<KhuyenMaiGiaSanPham, UUID> {

    @Query(
            "SELECT DISTINCT k FROM KhuyenMaiGiaSanPham k "
                    + "LEFT JOIN FETCH k.sanPhams "
                    + "LEFT JOIN FETCH k.sanPhamDon "
                    + "WHERE k.kichHoat = true "
                    + "AND k.thoiGianBatDau <= :luc AND k.thoiGianKetThuc >= :luc")
    List<KhuyenMaiGiaSanPham> findHieuLucTai(@Param("luc") LocalDateTime luc);

    @Query(
            "SELECT DISTINCT k FROM KhuyenMaiGiaSanPham k "
                    + "LEFT JOIN FETCH k.sanPhamDon "
                    + "LEFT JOIN FETCH k.sanPhams")
    List<KhuyenMaiGiaSanPham> findAllWithRelations();

    @Query(
            "SELECT DISTINCT k FROM KhuyenMaiGiaSanPham k "
                    + "LEFT JOIN FETCH k.sanPhamDon "
                    + "LEFT JOIN FETCH k.sanPhams "
                    + "WHERE k.id = :id")
    Optional<KhuyenMaiGiaSanPham> findByIdWithRelations(@Param("id") UUID id);
}

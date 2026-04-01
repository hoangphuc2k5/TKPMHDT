package TKPMHDT.Repository.donhang;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import TKPMHDT.Entity.donhang.PhieuGiao;

public interface PhieuGiaoRepository extends JpaRepository<PhieuGiao, UUID> {
    Optional<PhieuGiao> findByDonHangId(UUID donHangId);

    Optional<PhieuGiao> findBySoPhieuGiao(String soPhieuGiao);

    @Query("select p from PhieuGiao p where p.trangThaiGiao = :trangThai order by p.ngayTao desc")
    List<PhieuGiao> findByTrangThaiGiao(@Param("trangThai") String trangThai);

    @Query("select p from PhieuGiao p where p.ngayTao between :from and :to order by p.ngayTao desc")
    List<PhieuGiao> findByNgayTaoBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("select p from PhieuGiao p where p.nhanVienGiao.id = :nhanVienId order by p.ngayTao desc")
    List<PhieuGiao> findByNhanVienGiaoId(@Param("nhanVienId") UUID nhanVienId);

    @Query("select count(p) from PhieuGiao p where p.trangThaiGiao = 'CHO_GIAO' or p.trangThaiGiao = 'DANG_GIAO'")
    long countPhieuGiaoDangXuLy();
}

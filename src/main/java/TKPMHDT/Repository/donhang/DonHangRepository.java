package TKPMHDT.Repository.donhang;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import TKPMHDT.Entity.donhang.DonHang;

public interface DonHangRepository extends JpaRepository<DonHang, UUID> {
    List<DonHang> findByKhachHangId(UUID khachHangId);

    @Query("select d from DonHang d where d.trangThaiDb = :trangThai")
    List<DonHang> findByTrangThai(@Param("trangThai") String trangThai);

    @Query("select coalesce(sum(d.tongTien),0) from DonHang d where d.ngayDat between :from and :to")
    BigDecimal tinhDoanhThuTrongKhoang(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}


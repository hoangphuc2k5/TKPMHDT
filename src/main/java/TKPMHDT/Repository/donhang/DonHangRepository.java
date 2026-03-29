package TKPMHDT.Repository.donhang;

import TKPMHDT.Entity.donhang.DonHang;
import TKPMHDT.Entity.donhang.enums.TrangThaiDonHangEnum;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DonHangRepository extends JpaRepository<DonHang, UUID> {
    List<DonHang> findByKhachHangId(UUID khachHangId);

    List<DonHang> findByTrangThai(TrangThaiDonHangEnum trangThai);

    @Query("select coalesce(sum(d.tongTien),0) from DonHang d where d.ngayDat between :from and :to")
    BigDecimal tinhDoanhThuTrongKhoang(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}


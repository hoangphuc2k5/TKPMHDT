package TKPMHDT.Repository.donhang;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import TKPMHDT.Entity.donhang.HoaDon;

public interface HoaDonRepository extends JpaRepository<HoaDon, UUID> {
    Optional<HoaDon> findByDonHangId(UUID donHangId);

    Optional<HoaDon> findBySoHoaDon(String soHoaDon);

    @Query("select h from HoaDon h where h.ngayLap between :from and :to order by h.ngayLap desc")
    java.util.List<HoaDon> findByNgayLapBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("select count(h) from HoaDon h where h.trangThaiHoaDon = 'CHO_IN'")
    long countHoaDonChoIn();
}

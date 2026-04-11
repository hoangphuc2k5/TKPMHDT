package TKPMHDT.Service.donhang;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import TKPMHDT.Entity.donhang.DonHang;
import TKPMHDT.Entity.donhang.HoaDon;
import TKPMHDT.Entity.thanhtoan.ThanhToan;
import TKPMHDT.Repository.donhang.HoaDonRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class HoaDonService {
    private final HoaDonRepository hoaDonRepository;

    // Tạo hóa đơn cho đơn hàng sau khi thanh toán thành công
    public HoaDon taoHoaDonChoDonHangKhiThanhToanThanhCong(ThanhToan thanhToan) {
        DonHang donHang = thanhToan.getDonHang();
        HoaDon hoaDon = HoaDon.builder()
                .donHang(donHang)
                .soHoaDon(generateSoHoaDon())
                .ngayLap(LocalDateTime.now())
                .tongTien(donHang.getTongTien())
                .tienGiam(donHang.getMaGiamGia() != null ? donHang.getMaGiamGia().getGiaTri() : BigDecimal.ZERO) // tiền giảm nếu có mã giảm giá không thì để 0
                .phuongThucThanhToan(thanhToan.getPhuongThuc())
                .tienThanhToan(donHang.getTongTien().subtract(donHang.getMaGiamGia() != null ? donHang.getMaGiamGia().getGiaTri() : BigDecimal.ZERO))
                .trangThaiHoaDon("CHO_IN")
                .build();
        return hoaDonRepository.save(hoaDon);
    }


    public String generateSoHoaDon() {
        LocalDateTime now = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

        return "HD-" + now.format(formatter);
    }

}

package TKPMHDT.Service.nguyenlieu;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import TKPMHDT.Entity.donhang.ChiTietDonHang;
import TKPMHDT.Entity.donhang.DonHang;
import TKPMHDT.Entity.sanpham.ChiTietTopping;
import TKPMHDT.Entity.sanpham.NguyenLieu;
import TKPMHDT.Repository.donhang.DonHangRepository;
import TKPMHDT.Repository.sanpham.NguyenLieuRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import TKPMHDT.Entity.sanpham.enums.LoaiNguyenLieu;

@Service
@RequiredArgsConstructor
public class NguyeLieuService {

    private final NguyenLieuRepository nguyenLieuRepository;
    private final DonHangRepository donHangRepository;

    //Lấy các nguyên liệu là topping
    public List<NguyenLieu> layNguyenLieuTopping() {
        return nguyenLieuRepository.findAllNguyenLieuByLoaNguyenLieu(LoaiNguyenLieu.TOPPING);
    }

    // Tiến hành trừ nguyên liệu khi đơn hàng đã xác nhận và nếu nguyên liệu không đủ sẽ trả cảnh báo lỗi

    @Transactional
    public void truNguyenLieu(UUID donHangId) {

        DonHang donHang = donHangRepository.findById(donHangId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Không tìm thấy đơn hàng với ID: " + donHangId));

        if (!"DA_XAC_NHAN".equals(donHang.getTrangThai().getTenTrangThai())) {
            throw new IllegalStateException(
                    "Đơn hàng chưa được xác nhận, không thể trừ nguyên liệu.");
        }
        // Duyệt qua từng chi tiết đơn hàng để trừ nguyên liệu
        donHang.getChiTietDonHangs().forEach(chiTiet -> {
            
            // Trừ nguyên liệu chính của nước uống
            chiTiet.getNuocUong().getCongThucCoBan().getLuongNguyenLieus().forEach(luongNguyenLieu -> {

                BigDecimal soLuongCanTru = luongNguyenLieu.getSoLuong();

                NguyenLieu nguyenLieu = luongNguyenLieu.getNguyenLieu();

                if (nguyenLieu.getSoLuongTon().compareTo(soLuongCanTru) < 0) {
                    throw new IllegalStateException(
                            "Nguyên liệu " + nguyenLieu.getTen()
                                    + " không đủ. Cần: " + soLuongCanTru
                                    + ", hiện có: " + nguyenLieu.getSoLuongTon());
                }

                nguyenLieu.setSoLuongTon(
                        nguyenLieu.getSoLuongTon().subtract(soLuongCanTru)
                );

                nguyenLieuRepository.save(nguyenLieu);
            });

            // Trừ nguyên liệu của các topping (nếu có)
            chiTiet.getToppings().forEach(topping -> {

                NguyenLieu nguyenLieu = topping.getNguyenLieu();

                BigDecimal soLuongCanTru = tinhSoLuongCanTru(topping, nguyenLieu);

                if (nguyenLieu.getSoLuongTon().compareTo(soLuongCanTru) < 0) {
                    throw new IllegalStateException(
                            "Nguyên liệu " + nguyenLieu.getTen()
                                    + " không đủ. Cần: " + soLuongCanTru
                                    + ", hiện có: " + nguyenLieu.getSoLuongTon());
                }

                nguyenLieu.setSoLuongTon(
                        nguyenLieu.getSoLuongTon().subtract(soLuongCanTru)
                );

                nguyenLieuRepository.save(nguyenLieu);
            });
        });
    }

    private static final BigDecimal HE_SO_GRAM_ML = BigDecimal.TEN;

    private BigDecimal tinhSoLuongCanTru(ChiTietTopping topping, NguyenLieu nguyenLieu) {

        BigDecimal soLuong = BigDecimal.valueOf(topping.getSoLuong());

        if (laDonViKhoNho(nguyenLieu.getDonVi())) {
            return soLuong.multiply(HE_SO_GRAM_ML);
        }

        return soLuong;
    }
    private boolean laDonViKhoNho(String donVi) {
        return "g".equalsIgnoreCase(donVi)
                || "ml".equalsIgnoreCase(donVi);
    }
}

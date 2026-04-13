package TKPMHDT.DTO.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import TKPMHDT.Entity.nguoidung.DiaChi;
import lombok.*;


@Builder
@Data
@Getter
@Setter
public class XemDonHangResponse {

    // Thông tin chung về đơn hàng
    private UUID idDonHang;
    private String trangThai;
    private LocalDateTime ngayDat;
    private String tenKhachHang;

    // Thông tin chi tiết về sản phẩm trong đơn hàng
    private List<ChiTietDonHangResponse> chiTietDonHang;

    private DiaChi diaChiGiaoHang;

    private String phuongThucThanhToan;
    private String trangThaiThanhToan;

    private BigDecimal tamTinhHang;
    private BigDecimal tienGiamApDung;
    private String maGiamGia;
    private BigDecimal tongTien;
}

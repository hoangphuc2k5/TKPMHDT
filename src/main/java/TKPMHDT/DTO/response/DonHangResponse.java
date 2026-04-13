package TKPMHDT.DTO.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


import lombok.*;


@Builder
@Data
@Getter
@Setter
public class DonHangResponse {
    private UUID id;
    private LocalDateTime ngayDat;
    private String trangThai;
    private String phuongThucThanhToan;
    private String trangThaiThanhToan;
    private String tenKhachHang;
    /** Tổng thành tiền các dòng (trước mã voucher). */
    private BigDecimal tamTinhHang;
    private BigDecimal tienGiamApDung;
    private String maGiamGia;
    private BigDecimal tongTien;
    private List<ChiTietDonHangResponse> chiTietDonHang;
}

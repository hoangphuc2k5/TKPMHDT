package TKPMHDT.Entity.donhang;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * HoaDon (Invoice) - Đại diện cho hoá đơn được in/lưu
 * Mục đích: Ghi nhận hoá đơn chính thức cho đơn hàng
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "hoa_don")
public class HoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "don_hang_id", nullable = false, unique = true)
    private DonHang donHang;

    @Column(name = "so_hoa_don", nullable = false, unique = true, length = 50)
    private String soHoaDon;

    @Column(name = "ngay_lap", nullable = false)
    private LocalDateTime ngayLap;

    @Column(name = "tong_tien", nullable = false, precision = 18, scale = 2)
    private BigDecimal tongTien;

    @Column(name = "tien_giam", nullable = false, precision = 18, scale = 2)
    private BigDecimal tienGiam = BigDecimal.ZERO;

    @Column(name = "tien_thanh_toan", nullable = false, precision = 18, scale = 2)
    private BigDecimal tienThanhToan;

    @Column(name = "phuong_thuc_thanh_toan", length = 100)
    private String phuongThucThanhToan;

    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

    @Column(name = "trang_thai_hoa_don", nullable = false, length = 50)
    private String trangThaiHoaDon = "CHO_IN"; // CHO_IN, DA_IN, HUY

    @Column(name = "ngay_in")
    private LocalDateTime ngayIn;
}

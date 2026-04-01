package TKPMHDT.Entity.donhang;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;

import TKPMHDT.Entity.nguoidung.NhanVienBanHang;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * PhieuGiao (Delivery Note) - Đại diện cho phiếu giao hàng
 * Mục đích: Ghi nhận thông tin giao hàng và theo dõi quá trình vận chuyển
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "phieu_giao")
public class PhieuGiao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JsonBackReference
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "don_hang_id", nullable = false, unique = true)
    private DonHang donHang;

    @Column(name = "so_phieu_giao", nullable = false, unique = true, length = 50)
    private String soPhieuGiao;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nhan_vien_giao_id")
    private NhanVienBanHang nhanVienGiao;

    @Column(name = "dia_chi_giao", nullable = false, length = 500)
    private String diaChiGiao;

    @Column(name = "so_dien_thoai_nhan", length = 20)
    private String soDienThoaiNhan;

    @Column(name = "ten_nhan", length = 255)
    private String tenNhan;

    @Column(name = "trang_thai_giao", nullable = false, length = 50)
    private String trangThaiGiao = "CHO_GIAO"; // CHO_GIAO, DANG_GIAO, DA_GIAO, KHONG_GIAO

    @Column(name = "ngay_giao_du_kien")
    private LocalDateTime ngayGiaoDuKien;

    @Column(name = "ngay_giao_thuc_te")
    private LocalDateTime ngayGiaoThucTe;

    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

    @Column(name = "ngay_in")
    private LocalDateTime ngayIn;
}

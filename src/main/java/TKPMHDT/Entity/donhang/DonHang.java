package TKPMHDT.Entity.donhang;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import TKPMHDT.Entity.donhang.trangthai.TrangThaiChoXacNhan;
import TKPMHDT.Entity.donhang.trangthai.TrangThaiDaGiao;
import TKPMHDT.Entity.donhang.trangthai.TrangThaiDaHuy;
import TKPMHDT.Entity.donhang.trangthai.TrangThaiDaXacNhan;
import TKPMHDT.Entity.donhang.trangthai.TrangThaiDangGiao;
import TKPMHDT.Entity.donhang.trangthai.TrangThaiDonHang;
import TKPMHDT.Entity.khuyenmai.MaGiamGia;
import TKPMHDT.Entity.nguoidung.KhachHang;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "don_hang")
public class DonHang {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "khach_hang_id", nullable = false)
    private KhachHang khachHang;

    @Column(name = "ngay_dat", nullable = false)
    private LocalDateTime ngayDat;

    @Column(name = "trang_thai", nullable = false, length = 50)
    private String trangThaiDb;

    @Transient
    @Builder.Default
    private TrangThaiDonHang trangThai = new TrangThaiChoXacNhan();

    @Column(name = "tong_tien", nullable = false, precision = 18, scale = 2)
    private BigDecimal tongTien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_giam_gia_id")
    private MaGiamGia maGiamGia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dia_chi_giao_hang_id")
    private TKPMHDT.Entity.nguoidung.DiaChi diaChiGiaoHang;

    @JsonManagedReference
    @Builder.Default
    @OneToMany(mappedBy = "donHang", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChiTietDonHang> chiTietDonHangs = new ArrayList<>();

    @PrePersist
    @PreUpdate
    private void preSave() {
        if (trangThai != null) {
            this.trangThaiDb = trangThai.getTenTrangThai();
        }
    }

    @PostLoad
    private void postLoad() {
        if (trangThaiDb != null) {
            switch (trangThaiDb) {
                case "CHO_XAC_NHAN":
                    this.trangThai = new TrangThaiChoXacNhan();
                    break;
                case "DA_XAC_NHAN":
                    this.trangThai = new TrangThaiDaXacNhan();
                    break;
                case "DANG_GIAO":
                    this.trangThai = new TrangThaiDangGiao();
                    break;
                case "DA_GIAO":
                    this.trangThai = new TrangThaiDaGiao();
                    break;
                case "DA_HUY":
                    this.trangThai = new TrangThaiDaHuy();
                    break;
                default:
                    this.trangThai = new TrangThaiChoXacNhan();
            }
        }
    }

    public void xacNhan() {
        trangThai.daXacNhan(this);
    }

    public void giaoHang() {
        trangThai.dangGiao(this);
    }

    public void hoanThanh() {
        trangThai.daGiao(this);
    }

    public void huyDon() {
        trangThai.daHuy(this);
    }
}


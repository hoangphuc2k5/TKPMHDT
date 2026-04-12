package TKPMHDT.Entity.khuyenmai;

import TKPMHDT.Entity.khuyenmai.enums.LoaiGiamGiaEnum;
import TKPMHDT.Entity.khuyenmai.enums.PhamViKhuyenMaiGia;
import TKPMHDT.Entity.sanpham.SanPham;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
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
@Table(name = "khuyen_mai_gia_san_pham")
public class KhuyenMaiGiaSanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "ten", length = 200)
    private String ten;

    @Enumerated(EnumType.STRING)
    @Column(name = "pham_vi", nullable = false, length = 30)
    private PhamViKhuyenMaiGia phamVi;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_giam", nullable = false, length = 30)
    private LoaiGiamGiaEnum loaiGiam;

    @Column(name = "gia_tri", nullable = false, precision = 18, scale = 2)
    private BigDecimal giaTri;

    @Column(name = "thoi_gian_bat_dau", nullable = false)
    private LocalDateTime thoiGianBatDau;

    @Column(name = "thoi_gian_ket_thuc", nullable = false)
    private LocalDateTime thoiGianKetThuc;

    @Column(name = "kich_hoat", nullable = false)
    @Builder.Default
    private boolean kichHoat = true;

    /** Khi phamVi = MOT_SAN_PHAM */
    @ManyToOne
    @JoinColumn(name = "san_pham_don_id")
    private SanPham sanPhamDon;

    @ManyToMany
    @JoinTable(
            name = "khuyen_mai_gia_san_pham_ap_dung",
            joinColumns = @JoinColumn(name = "khuyen_mai_id"),
            inverseJoinColumns = @JoinColumn(name = "san_pham_id")
    )
    @Builder.Default
    private Set<SanPham> sanPhams = new HashSet<>();

    /** Khi phamVi = DANH_MUC — khớp SanPham.danhMuc (không phân biệt hoa thường). */
    @Column(name = "danh_muc", length = 120)
    private String danhMuc;
}

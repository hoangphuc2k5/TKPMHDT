package TKPMHDT.Entity.khuyenmai;

import TKPMHDT.Entity.khuyenmai.enums.LoaiGiamGiaEnum;
import TKPMHDT.Entity.sanpham.SanPham;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
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
@Table(name = "ma_giam_gia")
public class MaGiamGia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "ma", nullable = false, unique = true, length = 50)
    private String ma;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_giam", nullable = false, length = 30)
    private LoaiGiamGiaEnum loaiGiam;

    @Column(name = "gia_tri", nullable = false, precision = 18, scale = 2)
    private BigDecimal giaTri;

    @Column(name = "ngay_bat_dau")
    private LocalDate ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    private LocalDate ngayKetThuc;

    @Column(name = "kich_hoat")
    @Builder.Default
    private boolean kichHoat = true;

    @Column(name = "ap_dung_toan_he_thong")
    @Builder.Default
    private boolean apDungToanHeThong = false;

    @ManyToMany
    @JoinTable(
            name = "ma_giam_gia_san_pham",
            joinColumns = @JoinColumn(name = "ma_giam_gia_id"),
            inverseJoinColumns = @JoinColumn(name = "san_pham_id")
    )
    @Builder.Default
    private Set<SanPham> sanPhamApDung = new HashSet<>();

    /**
     * Nếu không rỗng: chỉ các ngày này được áp dụng (vẫn phải nằm trong {@link #ngayBatDau}–{@link #ngayKetThuc} nếu hai trường đó có giá trị).
     * Nếu rỗng: chỉ kiểm tra khoảng ngày bắt đầu/kết thúc.
     */
    @ElementCollection
    @CollectionTable(name = "ma_giam_gia_ngay_ap_dung", joinColumns = @JoinColumn(name = "ma_giam_gia_id"))
    @Column(name = "ngay_ap_dung")
    @Builder.Default
    private List<LocalDate> cacNgayApDung = new ArrayList<>();

    /** Danh mục sản phẩm (so khớp {@code SanPham.danhMuc}, không phân biệt hoa thường). */
    @ElementCollection
    @CollectionTable(name = "ma_giam_gia_danh_muc", joinColumns = @JoinColumn(name = "ma_giam_gia_id"))
    @Column(name = "danh_muc", length = 120)
    @Builder.Default
    private Set<String> danhMucApDung = new LinkedHashSet<>();
}


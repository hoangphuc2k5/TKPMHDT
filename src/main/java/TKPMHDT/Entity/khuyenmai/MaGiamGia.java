package TKPMHDT.Entity.khuyenmai;

import TKPMHDT.Entity.khuyenmai.enums.LoaiGiamGiaEnum;
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
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
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
}


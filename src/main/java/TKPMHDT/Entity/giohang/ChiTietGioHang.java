package TKPMHDT.Entity.giohang;

import TKPMHDT.Entity.sanpham.NuocUongSan;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
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
@Table(name = "chi_tiet_gio_hang")
public class ChiTietGioHang {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "gio_hang_id", nullable = false)
    private GioHang gioHang;

    @Column(name = "so_luong", nullable = false)
    private Integer soLuong;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "nuoc_uong_san_id", nullable = false)
    private NuocUongSan nuocUong;

    @Column(name = "muc_duong")
    private Integer mucDuong;

    @Column(name = "muc_da")
    private Integer mucDa;

    @Column(name = "ghi_chu_tuy_chinh", length = 500)
    private String ghiChuTuyChinh;

    @Column(name = "thanh_tien", nullable = false, precision = 18, scale = 2)
    private BigDecimal thanhTien;
}


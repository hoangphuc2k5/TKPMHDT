package TKPMHDT.Entity.thanhtoan;

import TKPMHDT.Entity.donhang.DonHang;
import com.fasterxml.jackson.annotation.JsonBackReference;
import TKPMHDT.Entity.thanhtoan.enums.PhuongThucThanhToanEnum;
import TKPMHDT.Entity.thanhtoan.enums.TrangThaiThanhToanEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Table(name = "thanh_toan")
public class ThanhToan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JsonBackReference
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "don_hang_id", nullable = false, unique = true)
    private DonHang donHang;

    @Column(name = "so_tien", nullable = false, precision = 18, scale = 2)
    private BigDecimal soTien;

    @Enumerated(EnumType.STRING)
    @Column(name = "phuong_thuc", nullable = false, length = 30)
    private PhuongThucThanhToanEnum phuongThuc;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false, length = 30)
    private TrangThaiThanhToanEnum trangThai;
}


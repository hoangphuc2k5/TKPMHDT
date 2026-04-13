package TKPMHDT.Entity.sanpham;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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
@Embeddable
public class TuyChinhKhachHang {

    @Column(name = "muc_da")
    private Integer mucDa;

    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

    @Transient
    @Builder.Default
    private List<LuongNguyenLieu> nguyenLieuThem = new ArrayList<>();

    public BigDecimal tinhGiaCuoiCung(BigDecimal giaCoBan) {
        return giaCoBan == null ? BigDecimal.ZERO : giaCoBan;
    }

}


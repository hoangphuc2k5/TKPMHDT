package TKPMHDT.Entity.sanpham;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "tuy_chon_tuy_chinh")
public class TuyChonTuyChinh {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "ten", nullable = false, length = 120)
    private String ten;

    @Column(name = "nhom", nullable = false, length = 50)
    private String nhom;

    @Column(name = "gia_them", nullable = false, precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal giaThem = BigDecimal.ZERO;

    @Column(name = "kich_hoat", nullable = false)
    @Builder.Default
    private boolean kichHoat = true;
}

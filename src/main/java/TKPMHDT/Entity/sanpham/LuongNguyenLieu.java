package TKPMHDT.Entity.sanpham;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@Table(name = "luong_nguyen_lieu")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class LuongNguyenLieu {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cong_thuc_id", nullable = false)
    private CongThuc congThuc;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "nguyen_lieu_id", nullable = false)
    private NguyenLieu nguyenLieu;

    @Column(name = "so_luong", nullable = false, precision = 18, scale = 3)
    private BigDecimal soLuong;

    @Column(name = "don_vi", nullable = false, length = 50)
    private String donVi;
}


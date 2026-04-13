package TKPMHDT.Entity.sanpham;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import TKPMHDT.Entity.sanpham.enums.LoaiNguyenLieu;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "nguyen_lieu")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class NguyenLieu {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "ten", nullable = false, length = 255)
    private String ten;

    @Column(name = "don_vi", nullable = false, length = 50)
    private String donVi;

    @Column(name = "so_luong_ton", nullable = false, precision = 18, scale = 3)
    private BigDecimal soLuongTon;

    @Column(name = "gia_don_vi", nullable = false, precision = 18, scale = 2)
    private BigDecimal giaDonVi;

    @Column(name = "nguong_canh_bao", precision = 18, scale = 3)
    @Builder.Default
    private BigDecimal nguongCanhBao = BigDecimal.ZERO;
    
    @Column(name = "gia", precision = 18, scale = 2)
    private BigDecimal gia;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "loai", nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'INGREDIENT'")
    @Builder.Default
    private LoaiNguyenLieu loaiNguyenLieu = LoaiNguyenLieu.INGREDIENT;
    
    
}


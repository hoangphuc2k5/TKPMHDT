package TKPMHDT.Entity.sanpham;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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
@Table(name = "cong_thuc")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CongThuc {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "ten", nullable = false, length = 255)
    private String ten;

    @Column(name = "mo_ta", columnDefinition = "nvarchar(max)")
    private String moTa;

    @Column(name = "gia_co_ban", nullable = false, precision = 18, scale = 2)
    private BigDecimal giaCoBan;

    @Builder.Default
    @OneToMany(mappedBy = "congThuc", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LuongNguyenLieu> luongNguyenLieus = new ArrayList<>();
    
}


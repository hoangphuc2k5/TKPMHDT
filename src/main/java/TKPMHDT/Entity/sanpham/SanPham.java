package TKPMHDT.Entity.sanpham;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
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
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "san_pham")
@Inheritance(strategy = InheritanceType.JOINED)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public abstract class SanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "ten", nullable = false, length = 255)
    private String ten;

    @Column(name = "gia", nullable = false, precision = 18, scale = 2)
    private BigDecimal gia;

    @Column(name = "mo_ta", columnDefinition = "nvarchar(max)")
    private String moTa;

    @Column(name = "danh_muc", length = 120)
    private String danhMuc;

    @Column(name = "dang_kinh_doanh", columnDefinition = "bit default 1")
    @Builder.Default
    private Boolean dangKinhDoanh = true;

    @ElementCollection
    @CollectionTable(name = "san_pham_hinh_anh", joinColumns = @JoinColumn(name = "san_pham_id"))
    @Column(name = "hinh_anh_url")
    @Builder.Default
    private List<String> hinhAnh = new ArrayList<>();

    @PrePersist
    @PreUpdate
    @PostLoad
    void normalizeDefaults() {
        if (dangKinhDoanh == null) {
            dangKinhDoanh = true;
        }
        if (hinhAnh == null) {
            hinhAnh = new ArrayList<>();
        }
    }

    public boolean isDangKinhDoanh() {
        return dangKinhDoanh == null || dangKinhDoanh;
    }
}


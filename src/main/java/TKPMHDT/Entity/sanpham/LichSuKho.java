package TKPMHDT.Entity.sanpham;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
@Table(name = "lich_su_kho")
public class LichSuKho {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "nguyen_lieu_id", nullable = false)
    private NguyenLieu nguyenLieu;

    @Column(name = "loai", nullable = false, length = 20)
    private String loai;

    @Column(name = "so_luong", nullable = false, precision = 18, scale = 3)
    private BigDecimal soLuong;

    @Column(name = "ton_truoc", nullable = false, precision = 18, scale = 3)
    private BigDecimal tonTruoc;

    @Column(name = "ton_sau", nullable = false, precision = 18, scale = 3)
    private BigDecimal tonSau;

    @Column(name = "ghi_chu", columnDefinition = "nvarchar(max)")
    private String ghiChu;

    @Column(name = "nguoi_thuc_hien", length = 100)
    private String nguoiThucHien;

    @Column(name = "thoi_gian", nullable = false)
    private LocalDateTime thoiGian;

    @PrePersist
    void prePersist() {
        if (thoiGian == null) {
            thoiGian = LocalDateTime.now();
        }
    }
}

package TKPMHDT.Entity.hethong;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
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
@Table(name = "nhat_ky_he_thong")
public class NhatKyHeThong {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "mo_dun", nullable = false, length = 60)
    private String moDun;

    @Column(name = "hanh_dong", nullable = false, length = 120)
    private String hanhDong;

    @Column(name = "chi_tiet", columnDefinition = "nvarchar(max)")
    private String chiTiet;

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

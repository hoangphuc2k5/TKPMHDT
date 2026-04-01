package TKPMHDT.Entity.sanpham;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
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
@Table(name = "nuoc_uong_san")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class NuocUongSan extends SanPham {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cong_thuc_id", nullable = false)
    private CongThuc congThucCoBan;

    @ManyToMany
    @JoinTable(
            name = "nuoc_uong_san_nguyen_lieu",
            joinColumns = @JoinColumn(name = "nuoc_uong_san_id"),
            inverseJoinColumns = @JoinColumn(name = "nguyen_lieu_id")
    )
    @Builder.Default
    private Set<NguyenLieu> nguyenLieuSuDung = new HashSet<>();

    @Column(name = "co_the_tuy_chinh", nullable = false)
    @Builder.Default
    private boolean coTheTuyChinh = true;

    @Column(name = "muc_duong_tuy_chon", length = 500)
    private String mucDuongTuyChon;

    @Column(name = "muc_duong_mac_dinh", length = 100)
    private String mucDuongMacDinh;

    @Column(name = "muc_da_tuy_chon", length = 500)
    private String mucDaTuyChon;

    @Column(name = "muc_da_mac_dinh", length = 100)
    private String mucDaMacDinh;

    @Column(name = "kich_co_tuy_chon", length = 500)
    private String kichCoTuyChon;

    @Column(name = "kich_co_mac_dinh", length = 100)
    private String kichCoMacDinh;

    @Column(name = "co_ap_dung_size")
    @Builder.Default
    private Boolean coApDungSize = true;

    @Column(name = "topping_cho_phep", length = 2000)
    private String toppingChoPhep;
}


package TKPMHDT.Entity.sanpham;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

import TKPMHDT.Entity.donhang.ChiTietDonHang;
import TKPMHDT.Entity.giohang.ChiTietGioHang;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "chi_tiet_topping")
public class ChiTietTopping {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private NguyenLieu nguyenLieu;

    private Integer soLuong;

    private BigDecimal donGia;

    @Column(name = "ten", length = 120)
    private String ten;

    @ManyToOne
    @JoinColumn(name = "chi_tiet_don_hang_id")
    private ChiTietDonHang chiTietDonHang;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "chi_tiet_gio_hang_id")
    private ChiTietGioHang chiTietGioHang;
}

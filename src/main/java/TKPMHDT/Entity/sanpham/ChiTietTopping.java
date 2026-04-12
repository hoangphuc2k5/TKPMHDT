package TKPMHDT.Entity.sanpham;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

import TKPMHDT.Entity.donhang.ChiTietDonHang;
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

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "chi_tiet_don_hang_id")
    private ChiTietDonHang chiTietDonHang;
}

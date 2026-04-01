package TKPMHDT.Entity.nguoidung;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "dia_chi")
public class DiaChi {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "khach_hang_id", nullable = false)
    private KhachHang khachHang;

    @Column(name = "ten_nguoi_nhan", nullable = false, length = 255)
    private String tenNguoiNhan;

    @Column(name = "so_dien_thoai", nullable = false, length = 20)
    private String soDienThoai;

    @Column(name = "dia_chi_cu_the", nullable = false, columnDefinition = "nvarchar(max)")
    private String diaChiCuThe; // Số nhà, tên đường

    @Column(name = "phuong_xa", nullable = false, length = 255)
    private String phuongXa;

    @Column(name = "quan_huyen", nullable = false, length = 255)
    private String quanHuyen;

    @Column(name = "tinh_thanh_pho", nullable = false, length = 255)
    private String tinhThanhPho;

    @Column(name = "la_mac_dinh", nullable = false)
    private boolean laMacDinh = false;
}

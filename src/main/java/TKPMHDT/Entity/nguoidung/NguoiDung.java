package TKPMHDT.Entity.nguoidung;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import TKPMHDT.Entity.nguoidung.enums.VaiTro;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
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
@Table(name = "nguoi_dung")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "loai_nguoi_dung")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class NguoiDung {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "ten_dang_nhap", nullable = false, unique = true, length = 100)
    private String tenDangNhap;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "mat_khau_hash", nullable = false, length = 255)
    private String matKhauHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "vai_tro", nullable = false, length = 50)
    private VaiTro vaiTro;

    @Column(name = "avatar_url", length = 255)
    private String avatar;

    @Column(name = "trang_thai_hoat_dong", nullable = false)
    private boolean trangThaiHoatDong = true;
}


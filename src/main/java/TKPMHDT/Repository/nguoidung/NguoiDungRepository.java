package TKPMHDT.Repository.nguoidung;

import TKPMHDT.Entity.nguoidung.NguoiDung;
import TKPMHDT.Entity.nguoidung.enums.VaiTro;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NguoiDungRepository extends JpaRepository<NguoiDung, UUID> {
    Optional<NguoiDung> findByTenDangNhap(String tenDangNhap);

    Optional<NguoiDung> findByEmail(String email);

    @Query(value = """
            SELECT * FROM nguoi_dung
            WHERE ten_dang_nhap = :dinhDanh
               OR email = :dinhDanh
               OR so_dien_thoai = :dinhDanh
            LIMIT 1
            """, nativeQuery = true)
    Optional<NguoiDung> findByDinhDanhDangNhap(@Param("dinhDanh") String dinhDanh);

    @Query(value = """
            SELECT * FROM nguoi_dung
            WHERE so_dien_thoai = :soDienThoai
            LIMIT 1
            """, nativeQuery = true)
    Optional<NguoiDung> findBySoDienThoai(@Param("soDienThoai") String soDienThoai);

    boolean existsByTenDangNhap(String tenDangNhap);

    boolean existsByEmail(String email);

    List<NguoiDung> findByVaiTro(VaiTro vaiTro);

    List<NguoiDung> findByVaiTroIn(List<VaiTro> vaiTros);
}


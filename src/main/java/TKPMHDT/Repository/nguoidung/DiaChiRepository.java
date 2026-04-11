package TKPMHDT.Repository.nguoidung;

import TKPMHDT.Entity.nguoidung.DiaChi;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaChiRepository extends JpaRepository<DiaChi, UUID> {
    List<DiaChi> findByKhachHangId(UUID khachHangId);
    Optional<DiaChi> findFirstByKhachHangIdAndLaMacDinhTrue(UUID khachHangId);
}

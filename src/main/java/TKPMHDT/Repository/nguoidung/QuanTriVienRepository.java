package TKPMHDT.Repository.nguoidung;

import TKPMHDT.Entity.nguoidung.QuanTriVien;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuanTriVienRepository extends JpaRepository<QuanTriVien, UUID> {
}


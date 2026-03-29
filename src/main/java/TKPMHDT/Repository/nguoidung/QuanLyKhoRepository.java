package TKPMHDT.Repository.nguoidung;

import TKPMHDT.Entity.nguoidung.QuanLyKho;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuanLyKhoRepository extends JpaRepository<QuanLyKho, UUID> {
}


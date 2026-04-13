package TKPMHDT.Repository.khuyenmai;

import TKPMHDT.Entity.khuyenmai.MaGiamGia;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaGiamGiaRepository extends JpaRepository<MaGiamGia, UUID> {
    Optional<MaGiamGia> findByMa(String ma);

    Optional<MaGiamGia> findByMaIgnoreCase(String ma);

    boolean existsByMa(String ma);
}


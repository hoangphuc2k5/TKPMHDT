package TKPMHDT.Repository.sanpham;

import TKPMHDT.Entity.sanpham.NuocUongSan;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NuocUongSanRepository extends JpaRepository<NuocUongSan, UUID> {
    List<NuocUongSan> findByTenContainingIgnoreCase(String ten);
}


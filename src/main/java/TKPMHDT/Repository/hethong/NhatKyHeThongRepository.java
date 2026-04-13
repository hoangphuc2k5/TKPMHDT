package TKPMHDT.Repository.hethong;

import TKPMHDT.Entity.hethong.NhatKyHeThong;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NhatKyHeThongRepository extends JpaRepository<NhatKyHeThong, UUID> {
    List<NhatKyHeThong> findTop200ByOrderByThoiGianDesc();
}

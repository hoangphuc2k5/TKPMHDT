package TKPMHDT.Repository.hethong;

import TKPMHDT.Entity.hethong.CauHinhHeThong;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CauHinhHeThongRepository extends JpaRepository<CauHinhHeThong, UUID> {
    Optional<CauHinhHeThong> findByConfigKey(String configKey);
}

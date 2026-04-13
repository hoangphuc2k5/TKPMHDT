package TKPMHDT.Repository.hethong;

import TKPMHDT.Entity.hethong.VaiTroQuyen;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VaiTroQuyenRepository extends JpaRepository<VaiTroQuyen, UUID> {
    Optional<VaiTroQuyen> findByVaiTro(String vaiTro);
}

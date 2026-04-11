package TKPMHDT.Repository.sanpham;

import TKPMHDT.Entity.sanpham.TuyChonTuyChinh;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TuyChonTuyChinhRepository extends JpaRepository<TuyChonTuyChinh, UUID> {

    List<TuyChonTuyChinh> findByNhomIgnoreCase(String nhom);
}
package TKPMHDT.Service.khuyenmai;

import TKPMHDT.Entity.khuyenmai.MaGiamGia;
import TKPMHDT.Entity.khuyenmai.enums.LoaiGiamGiaEnum;
import TKPMHDT.Repository.khuyenmai.MaGiamGiaRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KhuyenMaiService {

    private final MaGiamGiaRepository maGiamGiaRepository;

    public KhuyenMaiService(MaGiamGiaRepository maGiamGiaRepository) {
        this.maGiamGiaRepository = maGiamGiaRepository;
    }

    @Transactional(readOnly = true)
    public Optional<MaGiamGia> timTheoMa(String ma) {
        return maGiamGiaRepository.findByMa(ma);
    }

    @Transactional(readOnly = true)
    public List<MaGiamGia> danhSachMaGiamGia() {
        return maGiamGiaRepository.findAll();
    }

    @Transactional
    public MaGiamGia luuMaGiamGia(MaGiamGia maGiamGia) {
        return maGiamGiaRepository.save(maGiamGia);
    }

    @Transactional(readOnly = true)
    public BigDecimal tinhTienGiam(MaGiamGia maGiamGia, BigDecimal tongTien) {
        if (maGiamGia == null || tongTien == null || tongTien.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        if (maGiamGia.getLoaiGiam() == LoaiGiamGiaEnum.PHAN_TRAM) {
            BigDecimal phanTram = maGiamGia.getGiaTri();
            BigDecimal tienGiam = tongTien
                    .multiply(phanTram)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            return tienGiam.min(tongTien);
        }

        if (maGiamGia.getLoaiGiam() == LoaiGiamGiaEnum.SO_TIEN_CO_DINH) {
            return maGiamGia.getGiaTri().min(tongTien);
        }

        // MUA_X_TANG_Y can xu ly theo rule chi tiet trong service rieng.
        return BigDecimal.ZERO;
    }
}


package TKPMHDT.Service.thanhtoan;

import TKPMHDT.Entity.donhang.DonHang;
import TKPMHDT.Entity.thanhtoan.ThanhToan;
import TKPMHDT.Entity.thanhtoan.enums.PhuongThucThanhToanEnum;
import TKPMHDT.Entity.thanhtoan.enums.TrangThaiThanhToanEnum;
import TKPMHDT.Repository.donhang.DonHangRepository;
import TKPMHDT.Repository.thanhtoan.ThanhToanRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ThanhToanService {

    private final ThanhToanRepository thanhToanRepository;
    private final DonHangRepository donHangRepository;

    public ThanhToanService(
            ThanhToanRepository thanhToanRepository,
            DonHangRepository donHangRepository
    ) {
        this.thanhToanRepository = thanhToanRepository;
        this.donHangRepository = donHangRepository;
    }

    @Transactional
    public ThanhToan taoThanhToan(UUID donHangId, PhuongThucThanhToanEnum phuongThuc) {
        Optional<ThanhToan> tonTai = thanhToanRepository.findByDonHangId(donHangId);
        if (tonTai.isPresent()) {
            return tonTai.get();
        }

        DonHang donHang = donHangRepository.findById(donHangId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay don hang"));

        ThanhToan thanhToan = ThanhToan.builder()
                .donHang(donHang)
                .soTien(donHang.getTongTien())
                .phuongThuc(phuongThuc)
                .trangThai(TrangThaiThanhToanEnum.CHO_XU_LY)
                .build();
        donHang.setThanhToan(thanhToan);

        return thanhToanRepository.save(thanhToan);
    }

    @Transactional
    public ThanhToan capNhatTrangThai(UUID thanhToanId, TrangThaiThanhToanEnum trangThaiMoi) {
        ThanhToan thanhToan = thanhToanRepository.findById(thanhToanId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay thanh toan"));
        thanhToan.setTrangThai(trangThaiMoi);
        return thanhToanRepository.save(thanhToan);
    }

    @Transactional(readOnly = true)
    public Optional<ThanhToan> layTheoDonHangId(UUID donHangId) {
        return thanhToanRepository.findByDonHangId(donHangId);
    }
}


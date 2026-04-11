package TKPMHDT.Service.thanhtoan;

import TKPMHDT.DTO.request.TaoThanhToanRequest;
import TKPMHDT.DTO.response.ThanhToanResponse;
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
    public ThanhToanResponse taoThanhToan(TaoThanhToanRequest request) {

        UUID donHangId = request.getDonHangId();
        PhuongThucThanhToanEnum phuongThuc = request.getPhuongThuc();

        DonHang donHang = donHangRepository.findById(donHangId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay don hang"));

        ThanhToan thanhToan = ThanhToan.builder()
                .donHang(donHang)
                .soTien(donHang.getTongTien())
                .phuongThuc(phuongThuc)
                .trangThai(TrangThaiThanhToanEnum.CHO_XU_LY)
                .build();

        thanhToanRepository.save(thanhToan);

        ThanhToanResponse response = ThanhToanResponse.builder()
                .id(thanhToan.getId())
                .donHangId(donHangId)
                .soTien(thanhToan.getSoTien())
                .phuongThuc(thanhToan.getPhuongThuc())
                .trangThai(thanhToan.getTrangThai())
                .build();

        
        return response;
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

    // Xác nhận thanh toán thành công
    @Transactional
    public ThanhToan xacNhanThanhToanThanhCong(UUID thanhToanId) {
        ThanhToan thanhToan = thanhToanRepository.findById(thanhToanId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay thanh toan"));
        thanhToan.setTrangThai(TrangThaiThanhToanEnum.THANH_CONG);
        return thanhToanRepository.save(thanhToan);

    }
}


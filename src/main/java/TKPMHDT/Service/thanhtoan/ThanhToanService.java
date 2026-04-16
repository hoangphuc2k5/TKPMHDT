package TKPMHDT.Service.thanhtoan;

import TKPMHDT.DTO.request.TaoThanhToanRequest;
import TKPMHDT.DTO.response.ThanhToanResponse;
import TKPMHDT.Entity.donhang.DonHang;
import TKPMHDT.Entity.thanhtoan.ThanhToan;
import TKPMHDT.Entity.thanhtoan.enums.PhuongThucThanhToanEnum;
import TKPMHDT.Entity.thanhtoan.enums.TrangThaiThanhToanEnum;
import TKPMHDT.Entity.thanhtoan.strategy.ChienLuocThanhToan;
import TKPMHDT.Repository.donhang.DonHangRepository;
import TKPMHDT.Repository.thanhtoan.ThanhToanRepository;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ThanhToanService {

    private final ThanhToanRepository thanhToanRepository;
    private final DonHangRepository donHangRepository;
    private final Map<PhuongThucThanhToanEnum, ChienLuocThanhToan> chienLuocTheoPhuongThuc;

    public ThanhToanService(
            ThanhToanRepository thanhToanRepository,
            DonHangRepository donHangRepository,
            List<ChienLuocThanhToan> tatCaChienLuoc
    ) {
        this.thanhToanRepository = thanhToanRepository;
        this.donHangRepository = donHangRepository;
        this.chienLuocTheoPhuongThuc = new EnumMap<>(PhuongThucThanhToanEnum.class);
        for (ChienLuocThanhToan chienLuoc : tatCaChienLuoc) {
            this.chienLuocTheoPhuongThuc.put(chienLuoc.phuongThucHoTro(), chienLuoc);
        }
    }

    @Transactional
    public ThanhToanResponse taoThanhToan(TaoThanhToanRequest request) {

        UUID donHangId = request.getDonHangId();
        PhuongThucThanhToanEnum phuongThuc = request.getPhuongThuc();

        DonHang donHang = donHangRepository.findById(donHangId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay don hang"));

        ChienLuocThanhToan chienLuoc = chienLuocTheoPhuongThuc.get(phuongThuc);
        if (chienLuoc == null) {
            throw new IllegalArgumentException("Chua ho tro phuong thuc thanh toan: " + phuongThuc);
        }

        ThanhToan thanhToan = chienLuoc.thanhToan(donHang);
        donHang.setThanhToan(thanhToan);

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


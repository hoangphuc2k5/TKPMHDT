package TKPMHDT.Entity.thanhtoan.strategy;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import TKPMHDT.Entity.donhang.DonHang;
import TKPMHDT.Entity.thanhtoan.ThanhToan;
import TKPMHDT.Entity.thanhtoan.enums.PhuongThucThanhToanEnum;
import TKPMHDT.Entity.thanhtoan.enums.TrangThaiThanhToanEnum;
import TKPMHDT.Repository.donhang.DonHangRepository;
import TKPMHDT.Repository.thanhtoan.ThanhToanRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChienLuocTienMat implements ChienLuocThanhToan {

    private final ThanhToanRepository thanhToanRepository;
    private final DonHangRepository donHangRepository;

    @Override
    public ThanhToan thanhToan(DonHang donHang) {

        // 1. Tạo thanh toán
        ThanhToan thanhToan = ThanhToan.builder()
                .donHang(donHang)
                .soTien(donHang.getTongTien())
                .phuongThuc(PhuongThucThanhToanEnum.TIEN_MAT)
                .trangThai(TrangThaiThanhToanEnum.CHO_XU_LY)
                .build();

        thanhToanRepository.save(thanhToan);

        return thanhToan;
    }
}


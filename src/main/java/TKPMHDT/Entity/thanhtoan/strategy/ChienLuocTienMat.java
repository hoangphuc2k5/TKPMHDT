package TKPMHDT.Entity.thanhtoan.strategy;

import org.springframework.stereotype.Component;

import TKPMHDT.Entity.donhang.DonHang;
import TKPMHDT.Entity.thanhtoan.ThanhToan;
import TKPMHDT.Entity.thanhtoan.enums.PhuongThucThanhToanEnum;
import TKPMHDT.Entity.thanhtoan.enums.TrangThaiThanhToanEnum;

@Component
public class ChienLuocTienMat implements ChienLuocThanhToan {

    @Override
    public PhuongThucThanhToanEnum phuongThucHoTro() {
        return PhuongThucThanhToanEnum.TIEN_MAT;
    }

    @Override
    public ThanhToan thanhToan(DonHang donHang) {
        return ThanhToan.builder()
                .donHang(donHang)
                .soTien(donHang.getTongTien())
                .phuongThuc(PhuongThucThanhToanEnum.TIEN_MAT)
                .trangThai(TrangThaiThanhToanEnum.CHO_XU_LY)
                .build();
    }
}

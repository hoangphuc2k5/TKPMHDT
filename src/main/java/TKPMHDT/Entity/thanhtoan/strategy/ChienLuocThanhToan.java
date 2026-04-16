package TKPMHDT.Entity.thanhtoan.strategy;

import TKPMHDT.Entity.donhang.DonHang;
import TKPMHDT.Entity.thanhtoan.ThanhToan;
import TKPMHDT.Entity.thanhtoan.enums.PhuongThucThanhToanEnum;

public interface ChienLuocThanhToan {

    PhuongThucThanhToanEnum phuongThucHoTro();

    ThanhToan thanhToan(DonHang donHang);
}


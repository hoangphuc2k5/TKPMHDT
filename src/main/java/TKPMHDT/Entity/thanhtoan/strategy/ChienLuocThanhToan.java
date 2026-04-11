package TKPMHDT.Entity.thanhtoan.strategy;

import java.math.BigDecimal;

import TKPMHDT.Entity.donhang.DonHang;
import TKPMHDT.Entity.thanhtoan.ThanhToan;

public interface ChienLuocThanhToan {
    ThanhToan thanhToan(DonHang donHang);
    
}


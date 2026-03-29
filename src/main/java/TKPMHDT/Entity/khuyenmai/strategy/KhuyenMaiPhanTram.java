package TKPMHDT.Entity.khuyenmai.strategy;

import TKPMHDT.Entity.donhang.DonHang;
import java.math.BigDecimal;

public class KhuyenMaiPhanTram implements ChienLuocKhuyenMai {
    @Override
    public BigDecimal apDungKhuyenMai(DonHang donHang) {
        return BigDecimal.ZERO;
    }
}


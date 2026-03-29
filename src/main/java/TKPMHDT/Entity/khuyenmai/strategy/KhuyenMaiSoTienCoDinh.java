package TKPMHDT.Entity.khuyenmai.strategy;

import TKPMHDT.Entity.donhang.DonHang;
import java.math.BigDecimal;

public class KhuyenMaiSoTienCoDinh implements ChienLuocKhuyenMai {
    @Override
    public BigDecimal apDungKhuyenMai(DonHang donHang) {
        return BigDecimal.ZERO;
    }
}


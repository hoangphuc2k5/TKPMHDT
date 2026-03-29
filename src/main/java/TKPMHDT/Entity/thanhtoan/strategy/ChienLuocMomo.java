package TKPMHDT.Entity.thanhtoan.strategy;

import java.math.BigDecimal;

public class ChienLuocMomo implements ChienLuocThanhToan {
    @Override
    public boolean thanhToan(BigDecimal soTien) {
        return true;
    }
}


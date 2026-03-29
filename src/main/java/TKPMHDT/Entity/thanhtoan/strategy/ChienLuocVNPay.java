package TKPMHDT.Entity.thanhtoan.strategy;

import java.math.BigDecimal;

public class ChienLuocVNPay implements ChienLuocThanhToan {
    @Override
    public boolean thanhToan(BigDecimal soTien) {
        return true;
    }
}


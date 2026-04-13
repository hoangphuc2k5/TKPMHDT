package TKPMHDT.DTO.request;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record TinhTienGiamGioHangRequest(String ma, List<DongGioTinhGiam> chiTiet) {

    public record DongGioTinhGiam(UUID nuocUongId, BigDecimal thanhTien) {}
}

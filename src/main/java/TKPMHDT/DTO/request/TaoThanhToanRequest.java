package TKPMHDT.DTO.request;

import java.util.UUID;
import lombok.*;
import TKPMHDT.Entity.thanhtoan.enums.PhuongThucThanhToanEnum;

@Data
@Getter
public class TaoThanhToanRequest {
    private UUID donHangId;
    private PhuongThucThanhToanEnum phuongThuc;
}

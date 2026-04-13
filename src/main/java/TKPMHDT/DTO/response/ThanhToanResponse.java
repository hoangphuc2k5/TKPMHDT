package TKPMHDT.DTO.response;

import java.math.BigDecimal;
import java.util.UUID;

import TKPMHDT.Entity.thanhtoan.enums.PhuongThucThanhToanEnum;
import TKPMHDT.Entity.thanhtoan.enums.TrangThaiThanhToanEnum;

import lombok.*;

@Data
@Getter
@Setter

@NoArgsConstructor
@AllArgsConstructor

@Builder

public class ThanhToanResponse {
    private UUID id;
    private UUID donHangId;
    private BigDecimal soTien;
    private PhuongThucThanhToanEnum phuongThuc;
    private TrangThaiThanhToanEnum trangThai;
    

}

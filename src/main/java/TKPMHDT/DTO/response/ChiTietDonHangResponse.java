package TKPMHDT.DTO.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import lombok.*;


@Builder
@Data
@Getter
@Setter
public class ChiTietDonHangResponse {
    private UUID idChiTietDonHang;
    private String tenSanPham;
    
    private BigDecimal giaTien;
    private Integer mucDa;
    private String ghiChu;
    private Integer soLuong;
    private BigDecimal thanhTien;
    private List<ToppingResponse> toppings;

    private CongThucResponse congThuc;
}

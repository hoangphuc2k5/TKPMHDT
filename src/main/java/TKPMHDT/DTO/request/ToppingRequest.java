package TKPMHDT.DTO.request;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Data;

@Data
public class ToppingRequest {
    private UUID nguyenLieuId;
    private UUID toppingId;
    private String ten;
    private BigDecimal giaThem;
    private Integer soLuong;
}

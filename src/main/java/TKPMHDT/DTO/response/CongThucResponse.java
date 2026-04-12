package TKPMHDT.DTO.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.*;

@Builder
@Data
@Getter
@Setter
public class CongThucResponse {
    private String tenCongThuc;
    private BigDecimal giaCoBan;
    private String moTa;

    private List<NguyenLieuTrongCongThucResponse> nguyenLieus;
}

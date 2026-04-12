package TKPMHDT.DTO.response;

import java.math.BigDecimal;

import lombok.*;

@Builder
@Data
@Getter
@Setter
public class NguyenLieuTrongCongThucResponse {
    private String tenNguyenLieu;
    private BigDecimal soLuong;
    private String donVi;
}

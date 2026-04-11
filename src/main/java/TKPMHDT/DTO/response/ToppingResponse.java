package TKPMHDT.DTO.response;


import java.math.BigDecimal;

import lombok.*;


@Builder
@Data
@Getter
@Setter
public class ToppingResponse {
    private String ten;
    private Integer soLuong;
    private BigDecimal giaTien;
}
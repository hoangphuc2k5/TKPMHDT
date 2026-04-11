package TKPMHDT.DTO.request;

import java.util.List;
import java.util.UUID;

import lombok.*;

@Data
@Getter
public class SanPhamOrderRequest {

    private UUID nuocUongId;
    private Integer soLuong;

    private TuyChinhRequest tuyChinh;

    private List<ToppingRequest> toppings;
}

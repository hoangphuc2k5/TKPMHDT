package TKPMHDT.DTO.request;

import java.util.UUID;

import lombok.Data;

@Data
public class ToppingRequest {
    private UUID nguyenLieuId;
    private Integer soLuong;
}

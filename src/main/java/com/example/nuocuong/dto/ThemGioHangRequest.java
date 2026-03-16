package com.example.nuocuong.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThemGioHangRequest {
    private Long sanPhamId; // ID của NuocUongSan hoặc NguyenLieu
    private Integer soLuong;
    private TuyChinhRequest tuyChinh; // Có thể null nếu là nước uống sẵn không tùy chỉnh
}

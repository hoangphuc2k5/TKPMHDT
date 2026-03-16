package com.example.nuocuong.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TuyChinhRequest {
    private Long congThucId;
    private String tuyChinh; // Ví dụ: "ít đường, nhiều đá"
}

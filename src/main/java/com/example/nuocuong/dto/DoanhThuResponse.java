package com.example.nuocuong.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoanhThuResponse {
    private String label; // e.g., "Tháng 3", "2026-03-16"
    private Double value;
}

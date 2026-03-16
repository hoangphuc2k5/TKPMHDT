package com.example.nuocuong.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class NuocUongSanResponse extends SanPhamResponse {
    private String dungTich;
    private String loaiNuoc;
}

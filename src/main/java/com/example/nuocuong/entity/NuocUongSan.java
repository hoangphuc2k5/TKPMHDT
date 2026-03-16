package com.example.nuocuong.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "nuoc_uong_san")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class NuocUongSan extends SanPham {
    private String dungTich;
    private String loaiNuoc;
}

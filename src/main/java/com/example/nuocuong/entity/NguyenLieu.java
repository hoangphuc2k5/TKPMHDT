package com.example.nuocuong.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "nguyen_lieu")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class NguyenLieu extends SanPham {
    private String donViTinh;
    private Double soLuongTon;
    private Double nguongCanhBao;
    private LocalDate hanSuDung;
    private String loHang;
}

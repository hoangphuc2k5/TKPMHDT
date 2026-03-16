package com.example.nuocuong.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "khach_hang")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class KhachHang extends NguoiDung {
    private String maKhachHang;
    private Integer diemTichLuy;
}

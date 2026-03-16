package com.example.nuocuong.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "luong_nguyen_lieu")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LuongNguyenLieu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cong_thuc_id")
    private CongThuc congThuc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguyen_lieu_id")
    private NguyenLieu nguyenLieu;

    @Column(nullable = false)
    private Double soLuong;

    private String ghiChu;
}

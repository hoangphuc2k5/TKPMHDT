package com.example.nuocuong.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cong_thuc")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CongThuc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ten;

    private String moTa;

    @Builder.Default
    private boolean isDeleted = false;

    @OneToMany(mappedBy = "congThuc", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LuongNguyenLieu> danhSachNguyenLieu = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime ngayTao;

    @UpdateTimestamp
    private LocalDateTime ngayCapNhat;
}

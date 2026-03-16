package com.example.nuocuong.repository;

import com.example.nuocuong.entity.QuanTriVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuanTriVienRepository extends JpaRepository<QuanTriVien, Long> {
    Optional<QuanTriVien> findByTenDangNhap(String tenDangNhap);
}

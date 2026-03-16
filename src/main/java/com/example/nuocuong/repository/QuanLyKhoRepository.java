package com.example.nuocuong.repository;

import com.example.nuocuong.entity.QuanLyKho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuanLyKhoRepository extends JpaRepository<QuanLyKho, Long> {
    Optional<QuanLyKho> findByTenDangNhap(String tenDangNhap);
}

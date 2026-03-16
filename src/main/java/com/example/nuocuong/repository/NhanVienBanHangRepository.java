package com.example.nuocuong.repository;

import com.example.nuocuong.entity.NhanVienBanHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NhanVienBanHangRepository extends JpaRepository<NhanVienBanHang, Long> {
    Optional<NhanVienBanHang> findByTenDangNhap(String tenDangNhap);
}

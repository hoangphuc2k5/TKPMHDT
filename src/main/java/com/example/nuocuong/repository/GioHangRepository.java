package com.example.nuocuong.repository;

import com.example.nuocuong.entity.GioHang;
import com.example.nuocuong.entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GioHangRepository extends JpaRepository<GioHang, Long> {
    Optional<GioHang> findByKhachHang(KhachHang khachHang);
}

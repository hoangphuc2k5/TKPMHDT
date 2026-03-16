package com.example.nuocuong.repository;

import com.example.nuocuong.entity.DonHang;
import com.example.nuocuong.entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonHangRepository extends JpaRepository<DonHang, Long> {
    List<DonHang> findByKhachHang(KhachHang khachHang);
    List<DonHang> findByMaDonHang(String maDonHang);
}

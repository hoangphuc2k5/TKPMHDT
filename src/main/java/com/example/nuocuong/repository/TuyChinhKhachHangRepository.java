package com.example.nuocuong.repository;

import com.example.nuocuong.entity.TuyChinhKhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TuyChinhKhachHangRepository extends JpaRepository<TuyChinhKhachHang, Long> {
}

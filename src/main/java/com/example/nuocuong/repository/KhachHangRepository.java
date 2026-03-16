package com.example.nuocuong.repository;

import com.example.nuocuong.entity.KhachHang;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KhachHangRepository extends JpaRepository<KhachHang, Long> {
	Optional<KhachHang> findByEmail(String email);
	boolean existsByEmail(String email);
}


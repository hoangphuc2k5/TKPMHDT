package com.example.nuocuong.repository;

import com.example.nuocuong.entity.QuanTriVien;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuanTriVienRepository extends JpaRepository<QuanTriVien, Long> {
	Optional<QuanTriVien> findByEmail(String email);
	boolean existsByEmail(String email);
}


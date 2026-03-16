package com.example.nuocuong.repository;

import com.example.nuocuong.entity.NguoiDung;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NguoiDungRepository extends JpaRepository<NguoiDung, Long> {
	Optional<NguoiDung> findByEmail(String email);
	boolean existsByEmail(String email);
}


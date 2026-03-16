package com.example.nuocuong.repository;

import com.example.nuocuong.entity.MaGiamGia;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaGiamGiaRepository extends JpaRepository<MaGiamGia, Long> {
	Optional<MaGiamGia> findByMa(String ma);
}


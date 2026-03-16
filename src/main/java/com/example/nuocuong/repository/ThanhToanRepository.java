package com.example.nuocuong.repository;

import com.example.nuocuong.entity.ThanhToan;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ThanhToanRepository extends JpaRepository<ThanhToan, Long> {
	@Query("select tt from ThanhToan tt where tt.donHang.id = :donHangId")
	Optional<ThanhToan> findByDonHangId(@Param("donHangId") Long donHangId);
}


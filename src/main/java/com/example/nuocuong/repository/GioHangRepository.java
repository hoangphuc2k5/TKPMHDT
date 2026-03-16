package com.example.nuocuong.repository;

import com.example.nuocuong.entity.GioHang;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GioHangRepository extends JpaRepository<GioHang, Long> {
	@Query("select gh from GioHang gh where gh.khachHang.id = :khId")
	Optional<GioHang> findByKhachHangId(@Param("khId") Long khachHangId);
}


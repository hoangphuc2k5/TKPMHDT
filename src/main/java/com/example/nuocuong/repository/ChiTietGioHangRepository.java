package com.example.nuocuong.repository;

import com.example.nuocuong.entity.ChiTietGioHang;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChiTietGioHangRepository extends JpaRepository<ChiTietGioHang, Long> {
	@Query("select ct from ChiTietGioHang ct where ct.gioHang.id = :gioHangId")
	List<ChiTietGioHang> findByGioHangId(@Param("gioHangId") Long gioHangId);
}


package com.example.nuocuong.repository;

import com.example.nuocuong.entity.ChiTietDonHang;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChiTietDonHangRepository extends JpaRepository<ChiTietDonHang, Long> {
	@Query("select ct from ChiTietDonHang ct where ct.donHang.id = :donHangId")
	List<ChiTietDonHang> findByDonHangId(@Param("donHangId") Long donHangId);
}


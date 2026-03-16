package com.example.nuocuong.repository;

import com.example.nuocuong.entity.DonHang;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DonHangRepository extends JpaRepository<DonHang, Long> {
	Optional<DonHang> findByMaDonHang(String maDonHang);

	@Query("select dh from DonHang dh where dh.khachHang.id = :khId order by dh.createdAt desc")
	List<DonHang> findByKhachHangIdOrderByCreatedAtDesc(@Param("khId") Long khachHangId);
}


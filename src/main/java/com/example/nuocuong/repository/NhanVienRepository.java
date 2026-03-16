package com.example.nuocuong.repository;

import com.example.nuocuong.entity.NhanVien;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NhanVienRepository extends JpaRepository<NhanVien, Long> {
	@Query("select nv from NhanVien nv where nv.maNhanVien = :ma")
	Optional<NhanVien> findByMaNhanVien(@Param("ma") String maNhanVien);
}


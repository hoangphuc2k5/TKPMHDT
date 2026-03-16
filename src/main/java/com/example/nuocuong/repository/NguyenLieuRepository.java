package com.example.nuocuong.repository;

import com.example.nuocuong.entity.NguyenLieu;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NguyenLieuRepository extends JpaRepository<NguyenLieu, Long> {
	@Query("select nl from NguyenLieu nl where nl.dangKinhDoanh = true")
	List<NguyenLieu> findAllDangKinhDoanh();
}


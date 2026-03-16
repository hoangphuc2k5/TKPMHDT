package com.example.nuocuong.repository;

import com.example.nuocuong.entity.CongThuc;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CongThucRepository extends JpaRepository<CongThuc, Long> {
	@Query("select ct from CongThuc ct where ct.nuocUongSan.id = :nuocId")
	List<CongThuc> findByNuocUongSanId(@Param("nuocId") Long nuocUongSanId);
}


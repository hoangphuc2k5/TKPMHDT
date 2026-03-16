package com.example.nuocuong.repository;

import com.example.nuocuong.entity.LuongNguyenLieu;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LuongNguyenLieuRepository extends JpaRepository<LuongNguyenLieu, Long> {
	@Query("select l from LuongNguyenLieu l where l.congThuc.id = :ctId")
	List<LuongNguyenLieu> findByCongThucId(@Param("ctId") Long congThucId);
}


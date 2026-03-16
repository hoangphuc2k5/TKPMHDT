package com.example.nuocuong.repository;

import com.example.nuocuong.entity.SanPham;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SanPhamRepository extends JpaRepository<SanPham, Long> {
	@Query("select sp from SanPham sp where sp.dangKinhDoanh = true")
	List<SanPham> findAllDangKinhDoanh();
}


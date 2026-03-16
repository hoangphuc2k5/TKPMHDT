package com.example.nuocuong.repository;

import com.example.nuocuong.entity.NuocUongSan;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NuocUongSanRepository extends JpaRepository<NuocUongSan, Long> {
	@Query("select n from NuocUongSan n where n.dangKinhDoanh = true")
	List<NuocUongSan> findAllDangKinhDoanh();
}


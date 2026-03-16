package com.example.nuocuong.repository;

import com.example.nuocuong.entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Long> {
    
    @Query("SELECT s FROM SanPham s WHERE s.isDeleted = false " +
           "AND (:query IS NULL OR LOWER(s.ten) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "AND (:giaMin IS NULL OR s.gia >= :giaMin) " +
           "AND (:giaMax IS NULL OR s.gia <= :giaMax)")
    List<SanPham> search(@Param("query") String query, 
                        @Param("giaMin") Double giaMin, 
                        @Param("giaMax") Double giaMax);
}

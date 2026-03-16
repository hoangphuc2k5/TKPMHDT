package com.example.nuocuong.repository;

import com.example.nuocuong.entity.NguyenLieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NguyenLieuRepository extends JpaRepository<NguyenLieu, Long> {
    List<NguyenLieu> findByIsDeletedFalse();
    List<NguyenLieu> findBySoLuongTonLessThan(Double nguong);
}

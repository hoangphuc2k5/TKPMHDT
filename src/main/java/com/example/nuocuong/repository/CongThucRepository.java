package com.example.nuocuong.repository;

import com.example.nuocuong.entity.CongThuc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CongThucRepository extends JpaRepository<CongThuc, Long> {
    List<CongThuc> findByIsDeletedFalse();
}

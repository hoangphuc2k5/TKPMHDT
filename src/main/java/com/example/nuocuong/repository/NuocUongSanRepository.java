package com.example.nuocuong.repository;

import com.example.nuocuong.entity.NuocUongSan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NuocUongSanRepository extends JpaRepository<NuocUongSan, Long> {
    List<NuocUongSan> findByIsDeletedFalse();
}

package com.example.nuocuong.controller;

import com.example.nuocuong.dto.NguyenLieuResponse;
import com.example.nuocuong.dto.NhapKhoRequest;
import com.example.nuocuong.dto.XuatKhoRequest;
import com.example.nuocuong.service.QuanLyKhoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
@PreAuthorize("hasRole('QUAN_LY_KHO')")
public class QuanLyKhoController {

    private final QuanLyKhoService quanLyKhoService;

    @GetMapping("/inventory")
    public ResponseEntity<List<NguyenLieuResponse>> getInventory() {
        return ResponseEntity.ok(quanLyKhoService.getAllNguyenLieu());
    }

    @GetMapping("/audit")
    public ResponseEntity<List<NguyenLieuResponse>> auditInventory() {
        return ResponseEntity.ok(quanLyKhoService.kiemKeTonKho());
    }

    @PostMapping("/import")
    public ResponseEntity<Void> importStock(@RequestBody NhapKhoRequest request) {
        quanLyKhoService.nhapKho(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/export")
    public ResponseEntity<Void> exportStock(@RequestBody XuatKhoRequest request) {
        quanLyKhoService.xuatKho(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<NguyenLieuResponse>> getAlerts() {
        return ResponseEntity.ok(quanLyKhoService.getCanhBaoHetHang());
    }
}

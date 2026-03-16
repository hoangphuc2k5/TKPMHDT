package com.example.nuocuong.controller;

import com.example.nuocuong.dto.DonHangResponse;
import com.example.nuocuong.dto.ThanhToanRequest;
import com.example.nuocuong.service.DonHangService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class DonHangController {

    private final DonHangService donHangService;

    @PostMapping("/checkout")
    public ResponseEntity<DonHangResponse> checkout(@RequestBody ThanhToanRequest request) {
        return ResponseEntity.ok(donHangService.thanhToan(request));
    }

    @GetMapping("/history")
    public ResponseEntity<List<DonHangResponse>> getLichSu() {
        return ResponseEntity.ok(donHangService.getLichSuDonHang());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DonHangResponse> getChiTiet(@PathVariable Long id) {
        return ResponseEntity.ok(donHangService.getChiTietDonHang(id));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> huyDon(@PathVariable Long id) {
        donHangService.huyDonHang(id);
        return ResponseEntity.ok().build();
    }
}

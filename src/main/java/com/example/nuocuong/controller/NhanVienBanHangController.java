package com.example.nuocuong.controller;

import com.example.nuocuong.dto.DonHangResponse;
import com.example.nuocuong.dto.PosOrderRequest;
import com.example.nuocuong.entity.TrangThaiDonHang;
import com.example.nuocuong.service.NhanVienBanHangService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/staff")
@RequiredArgsConstructor
@PreAuthorize("hasRole('NHAN_VIEN_BAN_HANG')")
public class NhanVienBanHangController {

    private final NhanVienBanHangService nhanVienBanHangService;

    @GetMapping("/orders")
    public ResponseEntity<List<DonHangResponse>> getAllOrders() {
        return ResponseEntity.ok(nhanVienBanHangService.getAllDonHang());
    }

    @PatchMapping("/orders/{id}/status")
    public ResponseEntity<DonHangResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam TrangThaiDonHang status
    ) {
        return ResponseEntity.ok(nhanVienBanHangService.capNhatTrangThai(id, status));
    }

    @GetMapping("/orders/{id}/invoice")
    public ResponseEntity<String> getInvoice(@PathVariable Long id) {
        return ResponseEntity.ok(nhanVienBanHangService.inHoaDon(id));
    }

    @GetMapping("/orders/{id}/delivery-note")
    public ResponseEntity<String> getDeliveryNote(@PathVariable Long id) {
        return ResponseEntity.ok(nhanVienBanHangService.inPhieuGiaoHang(id));
    }

    @PostMapping("/pos-order")
    public ResponseEntity<DonHangResponse> createPosOrder(@RequestBody PosOrderRequest request) {
        return ResponseEntity.ok(nhanVienBanHangService.taoDonHangTaiQuay(request));
    }
}

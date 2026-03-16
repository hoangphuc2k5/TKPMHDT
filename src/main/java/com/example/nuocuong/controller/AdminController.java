package com.example.nuocuong.controller;

import com.example.nuocuong.dto.DoanhThuResponse;
import com.example.nuocuong.dto.DonHangResponse;
import com.example.nuocuong.dto.NguoiDungResponse;
import com.example.nuocuong.dto.NguyenLieuResponse;
import com.example.nuocuong.dto.SanPhamResponse;
import com.example.nuocuong.entity.TrangThaiDonHang;
import com.example.nuocuong.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<List<NguoiDungResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PostMapping("/users")
    public ResponseEntity<NguoiDungResponse> createUser(@RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(adminService.createUser(request));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<NguoiDungResponse> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(adminService.updateUser(id, request));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/products")
    public ResponseEntity<List<SanPhamResponse>> getAllProducts() {
        return ResponseEntity.ok(adminService.getAllProducts());
    }

    @PostMapping("/products")
    public ResponseEntity<SanPhamResponse> createProduct(@RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(adminService.createProduct(request));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<SanPhamResponse> updateProduct(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(adminService.updateProduct(id, request));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        adminService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/orders")
    public ResponseEntity<List<DonHangResponse>> getAllOrders() {
        return ResponseEntity.ok(adminService.getAllOrders());
    }

    @PatchMapping("/orders/{id}/status")
    public ResponseEntity<DonHangResponse> updateOrderStatus(@PathVariable Long id, @RequestParam TrangThaiDonHang status) {
        return ResponseEntity.ok(adminService.updateOrderStatus(id, status));
    }

    @GetMapping("/coupons")
    public ResponseEntity<List<Map<String, Object>>> getAllCoupons() {
        return ResponseEntity.ok(adminService.getAllCoupons());
    }

    @PostMapping("/coupons")
    public ResponseEntity<Map<String, Object>> createCoupon(@RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(adminService.saveCoupon(null, request));
    }

    @PutMapping("/coupons/{id}")
    public ResponseEntity<Map<String, Object>> updateCoupon(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(adminService.saveCoupon(id, request));
    }

    @DeleteMapping("/coupons/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        adminService.deleteCoupon(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/ingredients")
    public ResponseEntity<List<NguyenLieuResponse>> getAllIngredients() {
        return ResponseEntity.ok(adminService.getAllIngredients());
    }

    @GetMapping("/reports/revenue")
    public ResponseEntity<List<DoanhThuResponse>> getRevenueReport() {
        return ResponseEntity.ok(adminService.getDoanhThuReport());
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats() {
        return ResponseEntity.ok(adminService.getSystemStats());
    }
}

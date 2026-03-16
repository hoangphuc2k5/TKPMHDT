package com.example.nuocuong.service;

import com.example.nuocuong.dto.NguoiDungResponse;
import com.example.nuocuong.dto.DoanhThuResponse;
import com.example.nuocuong.dto.DonHangResponse;
import com.example.nuocuong.dto.SanPhamResponse;
import com.example.nuocuong.dto.NguyenLieuResponse;
import com.example.nuocuong.entity.TrangThaiDonHang;
import java.util.List;
import java.util.Map;

public interface AdminService {
    List<NguoiDungResponse> getAllUsers();
    NguoiDungResponse createUser(Map<String, Object> request);
    NguoiDungResponse updateUser(Long id, Map<String, Object> request);
    void deleteUser(Long id);
    List<DoanhThuResponse> getDoanhThuReport();
    Map<String, Long> getSystemStats();
    List<SanPhamResponse> getAllProducts();
    SanPhamResponse createProduct(Map<String, Object> request);
    SanPhamResponse updateProduct(Long id, Map<String, Object> request);
    void deleteProduct(Long id);
    List<DonHangResponse> getAllOrders();
    DonHangResponse updateOrderStatus(Long id, TrangThaiDonHang status);
    List<Map<String, Object>> getAllCoupons();
    Map<String, Object> saveCoupon(Long id, Map<String, Object> request);
    void deleteCoupon(Long id);
    List<NguyenLieuResponse> getAllIngredients();
}

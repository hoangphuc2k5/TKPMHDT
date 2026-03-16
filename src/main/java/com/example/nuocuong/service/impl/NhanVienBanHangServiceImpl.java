package com.example.nuocuong.service.impl;

import com.example.nuocuong.dto.ChiTietDonHangResponse;
import com.example.nuocuong.dto.DonHangResponse;
import com.example.nuocuong.dto.PosOrderRequest;
import com.example.nuocuong.entity.ChiTietDonHang;
import com.example.nuocuong.entity.DonHang;
import com.example.nuocuong.entity.LoaiGiamGia;
import com.example.nuocuong.entity.MaGiamGia;
import com.example.nuocuong.entity.NhanVienBanHang;
import com.example.nuocuong.entity.PhuongThucThanhToan;
import com.example.nuocuong.entity.SanPham;
import com.example.nuocuong.entity.ThanhToan;
import com.example.nuocuong.entity.TrangThaiDonHang;
import com.example.nuocuong.entity.TrangThaiThanhToan;
import com.example.nuocuong.repository.DonHangRepository;
import com.example.nuocuong.repository.MaGiamGiaRepository;
import com.example.nuocuong.repository.NhanVienBanHangRepository;
import com.example.nuocuong.repository.SanPhamRepository;
import com.example.nuocuong.repository.ThanhToanRepository;
import com.example.nuocuong.service.NhanVienBanHangService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NhanVienBanHangServiceImpl implements NhanVienBanHangService {

    private final DonHangRepository donHangRepository;
    private final SanPhamRepository sanPhamRepository;
    private final MaGiamGiaRepository maGiamGiaRepository;
    private final ThanhToanRepository thanhToanRepository;
    private final NhanVienBanHangRepository nhanVienBanHangRepository;

    @Override
    public List<DonHangResponse> getAllDonHang() {
        return donHangRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DonHangResponse capNhatTrangThai(Long id, TrangThaiDonHang trangThai) {
        DonHang dh = donHangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        dh.setTrangThai(trangThai);
        return mapToResponse(donHangRepository.save(dh));
    }

    @Override
    @Transactional
    public DonHangResponse taoDonHangTaiQuay(PosOrderRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Đơn tại quầy phải có ít nhất 1 sản phẩm");
        }
        DonHang order = DonHang.builder()
                .maDonHang("POS" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .diaChiGiaoHang(request.getTenKhach())
                .soDienThoaiGiaoHang(request.getSoDienThoai())
                .ghiChu(request.getGhiChu())
                .trangThai(TrangThaiDonHang.CHO_XU_LY)
                .nhanVienBanHang(getCurrentStaff())
                .danhSachChiTiet(new ArrayList<>())
                .build();

        List<ChiTietDonHang> details = request.getItems().stream().map(item -> {
            SanPham product = sanPhamRepository.findById(item.getSanPhamId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm ID=" + item.getSanPhamId()));
            int quantity = item.getSoLuong() == null || item.getSoLuong() < 1 ? 1 : item.getSoLuong();
            return ChiTietDonHang.builder()
                    .donHang(order)
                    .sanPham(product)
                    .soLuong(quantity)
                    .giaTaiThoiDiem(product.getGia())
                    .build();
        }).collect(Collectors.toList());
        order.setDanhSachChiTiet(details);

        double tongTien = details.stream().mapToDouble(i -> i.getGiaTaiThoiDiem() * i.getSoLuong()).sum();
        double giamGia = 0;
        if (request.getMaGiamGia() != null && !request.getMaGiamGia().isBlank()) {
            MaGiamGia coupon = maGiamGiaRepository.findByMaAndIsDeletedFalse(request.getMaGiamGia())
                    .orElseThrow(() -> new RuntimeException("Mã giảm giá không hợp lệ"));
            if (coupon.getLoaiGiamGia() == LoaiGiamGia.PHAN_TRAM) {
                giamGia = tongTien * coupon.getGiaTriGiam() / 100.0;
            } else {
                giamGia = coupon.getGiaTriGiam();
            }
            coupon.setSoLuongDaSuDung(coupon.getSoLuongDaSuDung() + 1);
            maGiamGiaRepository.save(coupon);
        }
        order.setTongTien(tongTien);
        order.setGiamGia(giamGia);
        order.setThanhTien(Math.max(0, tongTien - giamGia));

        DonHang saved = donHangRepository.save(order);
        ThanhToan payment = ThanhToan.builder()
                .donHang(saved)
                .phuongThuc(PhuongThucThanhToan.valueOf(request.getPhuongThucThanhToan() == null ? PhuongThucThanhToan.TIEN_MAT.name() : request.getPhuongThucThanhToan()))
                .trangThai(TrangThaiThanhToan.DA_THANH_TOAN)
                .soTien(saved.getThanhTien())
                .maGiaoDich("POS-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase())
                .build();
        thanhToanRepository.save(payment);
        return mapToResponse(saved);
    }

    @Override
    public String inHoaDon(Long id) {
        DonHang dh = donHangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        sb.append("<h1>HÓA ĐƠN BÁN HÀNG</h1>");
        sb.append("<p>Mã đơn: ").append(dh.getMaDonHang()).append("</p>");
        sb.append("<p>Ngày: ").append(dh.getNgayTao()).append("</p>");
        sb.append("<table border='1'>");
        sb.append("<tr><th>Sản phẩm</th><th>SL</th><th>Giá</th><th>Thành tiền</th></tr>");
        
        for (var item : dh.getDanhSachChiTiet()) {
            sb.append("<tr>");
            sb.append("<td>").append(item.getSanPham().getTen()).append("</td>");
            sb.append("<td>").append(item.getSoLuong()).append("</td>");
            sb.append("<td>").append(item.getGiaTaiThoiDiem()).append("</td>");
            sb.append("<td>").append(item.getGiaTaiThoiDiem() * item.getSoLuong()).append("</td>");
            sb.append("</tr>");
        }
        
        sb.append("</table>");
        sb.append("<p>Tổng cộng: ").append(dh.getTongTien()).append("</p>");
        sb.append("<p>Giảm giá: ").append(dh.getGiamGia()).append("</p>");
        sb.append("<h3>Thanh toán: ").append(dh.getThanhTien()).append("</h3>");
        sb.append("</body></html>");
        
        return sb.toString();
    }

    @Override
    public String inPhieuGiaoHang(Long id) {
        DonHang dh = donHangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        sb.append("<h1>PHIẾU GIAO HÀNG</h1>");
        sb.append("<p>Mã đơn: ").append(dh.getMaDonHang()).append("</p>");
        sb.append("<p>Khách nhận: ").append(dh.getDiaChiGiaoHang() == null ? "Khách lẻ" : dh.getDiaChiGiaoHang()).append("</p>");
        sb.append("<p>SĐT: ").append(dh.getSoDienThoaiGiaoHang() == null ? "-" : dh.getSoDienThoaiGiaoHang()).append("</p>");
        sb.append("<p>Trạng thái hiện tại: ").append(dh.getTrangThai()).append("</p>");
        sb.append("<ul>");
        dh.getDanhSachChiTiet().forEach(item ->
                sb.append("<li>")
                        .append(item.getSanPham().getTen())
                        .append(" x ")
                        .append(item.getSoLuong())
                        .append("</li>")
        );
        sb.append("</ul>");
        sb.append("</body></html>");
        return sb.toString();
    }

    private NhanVienBanHang getCurrentStaff() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return nhanVienBanHangRepository.findByTenDangNhap(username).orElse(null);
    }

    private DonHangResponse mapToResponse(DonHang dh) {
        return DonHangResponse.builder()
                .id(dh.getId())
                .maDonHang(dh.getMaDonHang())
                .tongTien(dh.getTongTien())
                .giamGia(dh.getGiamGia())
                .thanhTien(dh.getThanhTien())
                .trangThai(dh.getTrangThai().name())
                .ngayTao(dh.getNgayTao())
                .diaChiGiaoHang(dh.getDiaChiGiaoHang())
                .items(dh.getDanhSachChiTiet().stream()
                        .map(item -> ChiTietDonHangResponse.builder()
                                .tenSanPham(item.getSanPham().getTen())
                                .gia(item.getGiaTaiThoiDiem())
                                .soLuong(item.getSoLuong())
                                .tuyChinh(item.getTuyChinh() != null ? item.getTuyChinh().getTuyChinh() : null)
                                .thanhTien(item.getGiaTaiThoiDiem() * item.getSoLuong())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}

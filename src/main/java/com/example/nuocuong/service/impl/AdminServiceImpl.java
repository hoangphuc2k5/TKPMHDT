package com.example.nuocuong.service.impl;

import com.example.nuocuong.dto.ChiTietDonHangResponse;
import com.example.nuocuong.dto.DoanhThuResponse;
import com.example.nuocuong.dto.DonHangResponse;
import com.example.nuocuong.dto.NguoiDungResponse;
import com.example.nuocuong.dto.NguyenLieuResponse;
import com.example.nuocuong.dto.NuocUongSanResponse;
import com.example.nuocuong.dto.SanPhamResponse;
import com.example.nuocuong.entity.DonHang;
import com.example.nuocuong.entity.KhachHang;
import com.example.nuocuong.entity.LoaiGiamGia;
import com.example.nuocuong.entity.MaGiamGia;
import com.example.nuocuong.entity.NguyenLieu;
import com.example.nuocuong.entity.NguoiDung;
import com.example.nuocuong.entity.NhanVienBanHang;
import com.example.nuocuong.entity.NuocUongSan;
import com.example.nuocuong.entity.QuanLyKho;
import com.example.nuocuong.entity.QuanTriVien;
import com.example.nuocuong.entity.Role;
import com.example.nuocuong.entity.SanPham;
import com.example.nuocuong.entity.TrangThaiDonHang;
import com.example.nuocuong.repository.MaGiamGiaRepository;
import com.example.nuocuong.repository.DonHangRepository;
import com.example.nuocuong.repository.NguyenLieuRepository;
import com.example.nuocuong.repository.NguoiDungRepository;
import com.example.nuocuong.repository.NhanVienBanHangRepository;
import com.example.nuocuong.repository.NuocUongSanRepository;
import com.example.nuocuong.repository.QuanLyKhoRepository;
import com.example.nuocuong.repository.QuanTriVienRepository;
import com.example.nuocuong.repository.SanPhamRepository;
import com.example.nuocuong.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final NguoiDungRepository nguoiDungRepository;
    private final DonHangRepository donHangRepository;
    private final SanPhamRepository sanPhamRepository;
    private final NuocUongSanRepository nuocUongSanRepository;
    private final NguyenLieuRepository nguyenLieuRepository;
    private final NhanVienBanHangRepository nhanVienBanHangRepository;
    private final QuanLyKhoRepository quanLyKhoRepository;
    private final QuanTriVienRepository quanTriVienRepository;
    private final MaGiamGiaRepository maGiamGiaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<NguoiDungResponse> getAllUsers() {
        return nguoiDungRepository.findAll().stream()
                .filter(u -> !u.isDeleted())
                .map(u -> NguoiDungResponse.builder()
                        .id(u.getId())
                        .tenDangNhap(u.getTenDangNhap())
                        .hoTen(u.getHoTen())
                        .email(u.getEmail())
                        .vaiTro(u.getVaiTro().name())
                        .isDeleted(u.isDeleted())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public NguoiDungResponse createUser(Map<String, Object> request) {
        Role role = Role.valueOf(getString(request, "vaiTro", Role.KHACH_HANG.name()));
        String username = getString(request, "tenDangNhap", "").trim();
        if (username.isEmpty()) {
            throw new RuntimeException("Tên đăng nhập không được để trống");
        }
        if (nguoiDungRepository.findByTenDangNhap(username).isPresent()) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại");
        }
        String password = getString(request, "matKhau", "password123");
        String hoTen = getString(request, "hoTen", "");
        String email = getString(request, "email", "");
        String soDienThoai = getString(request, "soDienThoai", "");
        String diaChi = getString(request, "diaChi", "");

        NguoiDung created;
        if (role == Role.NHAN_VIEN_BAN_HANG) {
            created = nhanVienBanHangRepository.save(NhanVienBanHang.builder()
                    .tenDangNhap(username)
                    .matKhau(passwordEncoder.encode(password))
                    .hoTen(hoTen)
                    .email(email)
                    .soDienThoai(soDienThoai)
                    .diaChi(diaChi)
                    .vaiTro(role)
                    .maNhanVien("NV" + UUID.randomUUID().toString().substring(0, 6).toUpperCase())
                    .luong(getDouble(request, "luong", 0))
                    .soDonDaBan(0)
                    .build());
        } else if (role == Role.QUAN_LY_KHO) {
            created = quanLyKhoRepository.save(QuanLyKho.builder()
                    .tenDangNhap(username)
                    .matKhau(passwordEncoder.encode(password))
                    .hoTen(hoTen)
                    .email(email)
                    .soDienThoai(soDienThoai)
                    .diaChi(diaChi)
                    .vaiTro(role)
                    .maNhanVien("NV" + UUID.randomUUID().toString().substring(0, 6).toUpperCase())
                    .luong(getDouble(request, "luong", 0))
                    .khuVucQuanLy(getString(request, "khuVucQuanLy", "Kho tổng"))
                    .build());
        } else if (role == Role.ADMIN) {
            created = quanTriVienRepository.save(QuanTriVien.builder()
                    .tenDangNhap(username)
                    .matKhau(passwordEncoder.encode(password))
                    .hoTen(hoTen)
                    .email(email)
                    .soDienThoai(soDienThoai)
                    .diaChi(diaChi)
                    .vaiTro(role)
                    .quyenHan(getString(request, "quyenHan", "FULL_ACCESS"))
                    .build());
        } else {
            created = nguoiDungRepository.save(KhachHang.builder()
                    .tenDangNhap(username)
                    .matKhau(passwordEncoder.encode(password))
                    .hoTen(hoTen)
                    .email(email)
                    .soDienThoai(soDienThoai)
                    .diaChi(diaChi)
                    .vaiTro(role)
                    .maKhachHang("KH" + UUID.randomUUID().toString().substring(0, 6).toUpperCase())
                    .diemTichLuy(0)
                    .build());
        }

        return toNguoiDungResponse(created);
    }

    @Override
    @Transactional
    public NguoiDungResponse updateUser(Long id, Map<String, Object> request) {
        NguoiDung user = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        user.setHoTen(getString(request, "hoTen", user.getHoTen()));
        user.setEmail(getString(request, "email", user.getEmail()));
        user.setSoDienThoai(getString(request, "soDienThoai", user.getSoDienThoai()));
        user.setDiaChi(getString(request, "diaChi", user.getDiaChi()));
        String rawPassword = getString(request, "matKhau", "");
        if (!rawPassword.isBlank()) {
            user.setMatKhau(passwordEncoder.encode(rawPassword));
        }
        if (user instanceof NhanVienBanHang nv) {
            nv.setLuong(getDouble(request, "luong", nv.getLuong() == null ? 0 : nv.getLuong()));
            nguoiDungRepository.save(nv);
            return toNguoiDungResponse(nv);
        }
        if (user instanceof QuanLyKho qlk) {
            qlk.setLuong(getDouble(request, "luong", qlk.getLuong() == null ? 0 : qlk.getLuong()));
            qlk.setKhuVucQuanLy(getString(request, "khuVucQuanLy", qlk.getKhuVucQuanLy()));
            nguoiDungRepository.save(qlk);
            return toNguoiDungResponse(qlk);
        }
        if (user instanceof QuanTriVien qtv) {
            qtv.setQuyenHan(getString(request, "quyenHan", qtv.getQuyenHan()));
            nguoiDungRepository.save(qtv);
            return toNguoiDungResponse(qtv);
        }
        nguoiDungRepository.save(user);
        return toNguoiDungResponse(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        NguoiDung u = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        u.setDeleted(true);
        nguoiDungRepository.save(u);
    }

    @Override
    public List<SanPhamResponse> getAllProducts() {
        return sanPhamRepository.findAll().stream()
                .filter(s -> !s.isDeleted())
                .map(this::toSanPhamResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SanPhamResponse createProduct(Map<String, Object> request) {
        String loai = getString(request, "loaiSanPham", "NUOC_UONG_SAN");
        String ten = getString(request, "ten", "").trim();
        if (ten.isEmpty()) {
            throw new RuntimeException("Tên sản phẩm không được để trống");
        }
        if ("NGUYEN_LIEU".equalsIgnoreCase(loai)) {
            NguyenLieu item = NguyenLieu.builder()
                    .ten(ten)
                    .moTa(getString(request, "moTa", ""))
                    .gia(getDouble(request, "gia", 0))
                    .hinhAnh(getString(request, "hinhAnh", ""))
                    .donViTinh(getString(request, "donViTinh", "kg"))
                    .soLuongTon(getDouble(request, "soLuongTon", 0))
                    .nguongCanhBao(getDouble(request, "nguongCanhBao", 0))
                    .loHang(getString(request, "loHang", ""))
                    .hanSuDung(parseDateOrNull(getString(request, "hanSuDung", "")))
                    .build();
            return toSanPhamResponse(nguyenLieuRepository.save(item));
        }
        NuocUongSan item = NuocUongSan.builder()
                .ten(ten)
                .moTa(getString(request, "moTa", ""))
                .gia(getDouble(request, "gia", 0))
                .hinhAnh(getString(request, "hinhAnh", ""))
                .dungTich(getString(request, "dungTich", "500ml"))
                .loaiNuoc(getString(request, "loaiNuoc", "Khác"))
                .build();
        return toSanPhamResponse(nuocUongSanRepository.save(item));
    }

    @Override
    @Transactional
    public SanPhamResponse updateProduct(Long id, Map<String, Object> request) {
        SanPham product = sanPhamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        product.setTen(getString(request, "ten", product.getTen()));
        product.setMoTa(getString(request, "moTa", product.getMoTa()));
        product.setGia(getDouble(request, "gia", product.getGia()));
        product.setHinhAnh(getString(request, "hinhAnh", product.getHinhAnh()));
        if (product instanceof NguyenLieu nl) {
            nl.setDonViTinh(getString(request, "donViTinh", nl.getDonViTinh()));
            nl.setSoLuongTon(getDouble(request, "soLuongTon", nl.getSoLuongTon()));
            nl.setNguongCanhBao(getDouble(request, "nguongCanhBao", nl.getNguongCanhBao()));
            nl.setLoHang(getString(request, "loHang", nl.getLoHang()));
            String hanSuDung = getString(request, "hanSuDung", "");
            if (!hanSuDung.isBlank()) {
                nl.setHanSuDung(parseDateOrNull(hanSuDung));
            }
            return toSanPhamResponse(nguyenLieuRepository.save(nl));
        }
        if (product instanceof NuocUongSan nus) {
            nus.setDungTich(getString(request, "dungTich", nus.getDungTich()));
            nus.setLoaiNuoc(getString(request, "loaiNuoc", nus.getLoaiNuoc()));
            return toSanPhamResponse(nuocUongSanRepository.save(nus));
        }
        return toSanPhamResponse(sanPhamRepository.save(product));
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        SanPham product = sanPhamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        product.setDeleted(true);
        sanPhamRepository.save(product);
    }

    @Override
    public List<DonHangResponse> getAllOrders() {
        return donHangRepository.findAll().stream()
                .map(this::toDonHangResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DonHangResponse updateOrderStatus(Long id, TrangThaiDonHang status) {
        DonHang order = donHangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        order.setTrangThai(status);
        return toDonHangResponse(donHangRepository.save(order));
    }

    @Override
    public List<Map<String, Object>> getAllCoupons() {
        return maGiamGiaRepository.findAll().stream()
                .filter(c -> !c.isDeleted())
                .map(this::toCouponMap)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Map<String, Object> saveCoupon(Long id, Map<String, Object> request) {
        MaGiamGia coupon = id == null
                ? new MaGiamGia()
                : maGiamGiaRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy mã giảm giá"));
        coupon.setMa(getString(request, "ma", coupon.getMa()));
        coupon.setMoTa(getString(request, "moTa", coupon.getMoTa()));
        coupon.setGiaTriGiam(getDouble(request, "giaTriGiam", coupon.getGiaTriGiam() == null ? 0 : coupon.getGiaTriGiam()));
        coupon.setLoaiGiamGia(LoaiGiamGia.valueOf(getString(request, "loaiGiamGia", coupon.getLoaiGiamGia() == null ? LoaiGiamGia.PHAN_TRAM.name() : coupon.getLoaiGiamGia().name())));
        coupon.setNgayBatDau(parseDateTimeOrDefault(getString(request, "ngayBatDau", ""), coupon.getNgayBatDau(), LocalDateTime.now()));
        coupon.setNgayKetThuc(parseDateTimeOrDefault(getString(request, "ngayKetThuc", ""), coupon.getNgayKetThuc(), LocalDateTime.now().plusDays(30)));
        coupon.setSoLuongSuDungToiDa(getInt(request, "soLuongSuDungToiDa", coupon.getSoLuongSuDungToiDa() == null ? 0 : coupon.getSoLuongSuDungToiDa()));
        coupon.setSoLuongDaSuDung(getInt(request, "soLuongDaSuDung", coupon.getSoLuongDaSuDung() == null ? 0 : coupon.getSoLuongDaSuDung()));
        coupon.setDeleted(false);
        return toCouponMap(maGiamGiaRepository.save(coupon));
    }

    @Override
    @Transactional
    public void deleteCoupon(Long id) {
        MaGiamGia coupon = maGiamGiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mã giảm giá"));
        coupon.setDeleted(true);
        maGiamGiaRepository.save(coupon);
    }

    @Override
    public List<NguyenLieuResponse> getAllIngredients() {
        return nguyenLieuRepository.findAll().stream()
                .filter(n -> !n.isDeleted())
                .map(n -> NguyenLieuResponse.builder()
                        .id(n.getId())
                        .ten(n.getTen())
                        .soLuongTon(n.getSoLuongTon())
                        .donViTinh(n.getDonViTinh())
                        .nguongCanhBao(n.getNguongCanhBao())
                        .hanSuDung(n.getHanSuDung())
                        .loHang(n.getLoHang())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<DoanhThuResponse> getDoanhThuReport() {
        List<DonHang> orders = donHangRepository.findAll().stream()
                .filter(o -> o.getTrangThai() == TrangThaiDonHang.HOAN_THANH)
                .toList();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        return orders.stream()
                .collect(Collectors.groupingBy(
                        o -> o.getNgayTao().format(formatter),
                        Collectors.summingDouble(DonHang::getThanhTien)
                ))
                .entrySet().stream()
                .map(entry -> DoanhThuResponse.builder()
                        .label(entry.getKey())
                        .value(entry.getValue())
                        .build())
                .sorted((a, b) -> a.getLabel().compareTo(b.getLabel()))
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Long> getSystemStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalUsers", nguoiDungRepository.count());
        stats.put("totalProducts", sanPhamRepository.count());
        stats.put("totalOrders", donHangRepository.count());
        return stats;
    }

    private NguoiDungResponse toNguoiDungResponse(NguoiDung u) {
        return NguoiDungResponse.builder()
                .id(u.getId())
                .tenDangNhap(u.getTenDangNhap())
                .hoTen(u.getHoTen())
                .email(u.getEmail())
                .vaiTro(u.getVaiTro().name())
                .isDeleted(u.isDeleted())
                .build();
    }

    private SanPhamResponse toSanPhamResponse(SanPham s) {
        if (s instanceof NuocUongSan n) {
            return NuocUongSanResponse.builder()
                    .id(n.getId())
                    .ten(n.getTen())
                    .moTa(n.getMoTa())
                    .gia(n.getGia())
                    .hinhAnh(n.getHinhAnh())
                    .loaiSanPham("NUOC_UONG_SAN")
                    .dungTich(n.getDungTich())
                    .loaiNuoc(n.getLoaiNuoc())
                    .build();
        }
        if (s instanceof NguyenLieu n) {
            return SanPhamResponse.builder()
                    .id(n.getId())
                    .ten(n.getTen())
                    .moTa(n.getMoTa())
                    .gia(n.getGia())
                    .hinhAnh(n.getHinhAnh())
                    .loaiSanPham("NGUYEN_LIEU")
                    .build();
        }
        return SanPhamResponse.builder()
                .id(s.getId())
                .ten(s.getTen())
                .moTa(s.getMoTa())
                .gia(s.getGia())
                .hinhAnh(s.getHinhAnh())
                .build();
    }

    private DonHangResponse toDonHangResponse(DonHang dh) {
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

    private Map<String, Object> toCouponMap(MaGiamGia coupon) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", coupon.getId());
        result.put("ma", coupon.getMa());
        result.put("moTa", coupon.getMoTa());
        result.put("giaTriGiam", coupon.getGiaTriGiam());
        result.put("loaiGiamGia", coupon.getLoaiGiamGia());
        result.put("ngayBatDau", coupon.getNgayBatDau());
        result.put("ngayKetThuc", coupon.getNgayKetThuc());
        result.put("soLuongSuDungToiDa", coupon.getSoLuongSuDungToiDa());
        result.put("soLuongDaSuDung", coupon.getSoLuongDaSuDung());
        return result;
    }

    private String getString(Map<String, Object> request, String key, String defaultValue) {
        Object value = request.get(key);
        return value == null ? defaultValue : String.valueOf(value);
    }

    private Double getDouble(Map<String, Object> request, String key, Double defaultValue) {
        Object value = request.get(key);
        if (value == null || String.valueOf(value).isBlank()) return defaultValue;
        return Double.parseDouble(String.valueOf(value));
    }

    private Integer getInt(Map<String, Object> request, String key, Integer defaultValue) {
        Object value = request.get(key);
        if (value == null || String.valueOf(value).isBlank()) return defaultValue;
        return Integer.parseInt(String.valueOf(value));
    }

    private LocalDate parseDateOrNull(String value) {
        if (value == null || value.isBlank()) return null;
        return LocalDate.parse(value);
    }

    private LocalDateTime parseDateTimeOrDefault(String value, LocalDateTime current, LocalDateTime fallback) {
        if (value != null && !value.isBlank()) return LocalDateTime.parse(value);
        if (current != null) return current;
        return fallback;
    }
}

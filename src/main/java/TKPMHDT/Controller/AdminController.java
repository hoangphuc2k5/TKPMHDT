package TKPMHDT.Controller;

import TKPMHDT.Entity.donhang.DonHang;
import TKPMHDT.Entity.khuyenmai.MaGiamGia;
import TKPMHDT.Entity.nguoidung.NguoiDung;
import TKPMHDT.Entity.sanpham.NguyenLieu;
import TKPMHDT.Entity.sanpham.NuocUongSan;
import TKPMHDT.Repository.donhang.DonHangRepository;
import TKPMHDT.Service.nguoidung.NguoiDungService;
import TKPMHDT.Service.sanpham.SanPhamService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * AdminController - Quản lý hệ thống
 * UC16: Quản lý sản phẩm & Nguyên liệu
 * UC17: Quản lý kho
 * UC18: Quản lý đơn hàng
 * UC19: Quản lý khách hàng
 * UC20: Quản lý khuyến mại
 * UC21: Xem báo cáo doanh thu
 * UC22: Quản lý nhân viên
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('QUAN_TRI_VIEN')")
public class AdminController {

    private final NguoiDungService nguoiDungService;
    private final SanPhamService sanPhamService;
    private final DonHangRepository donHangRepository;

    public AdminController(
            NguoiDungService nguoiDungService,
            SanPhamService sanPhamService,
            DonHangRepository donHangRepository) {
        this.nguoiDungService = nguoiDungService;
        this.sanPhamService = sanPhamService;
        this.donHangRepository = donHangRepository;
    }

    // ==================== UC16: Quản lý sản phẩm & Nguyên liệu ====================

    /**
     * UC16: Lấy danh sách tất cả sản phẩm
     */
    @GetMapping("/san-pham")
    public ResponseEntity<List<NuocUongSan>> layDanhSachSanPham() {
        return ResponseEntity.ok(sanPhamService.layDanhSachNuocUong());
    }

    /**
     * UC16: Tạo sản phẩm mới
     */
    @PostMapping("/san-pham")
    public ResponseEntity<NuocUongSan> taoSanPham(@RequestBody NuocUongSan sanPham) {
        return ResponseEntity.ok(sanPhamService.luuNuocUong(sanPham));
    }

    /**
     * UC16: Lấy danh sách nguyên liệu
     */
    @GetMapping("/nguyen-lieu")
    public ResponseEntity<List<NguyenLieu>> layDanhSachNguyenLieu() {
        return ResponseEntity.ok(sanPhamService.layDanhSachNguyenLieu());
    }

    /**
     * UC16: Tạo nguyên liệu mới
     */
    @PostMapping("/nguyen-lieu")
    public ResponseEntity<NguyenLieu> taoNguyenLieu(@RequestBody NguyenLieu nguyenLieu) {
        return ResponseEntity.ok(sanPhamService.luuNguyenLieu(nguyenLieu));
    }

    // ==================== UC17: Quản lý kho ====================

    /**
     * UC17: TODO - Quản lý kho (cần Service)
     */
    @GetMapping("/kho")
    public ResponseEntity<Map<String, String>> layThongTinKho() {
        return ResponseEntity.ok(Map.of(
                "message", "Tính năng quản lý kho đang được phát triển"
        ));
    }

    // ==================== UC18: Quản lý đơn hàng ====================

    /**
     * UC18: Lấy danh sách tất cả đơn hàng
     */
    @GetMapping("/don-hang")
    public ResponseEntity<List<DonHang>> layDanhSachDonHang() {
        return ResponseEntity.ok(donHangRepository.findAll());
    }

    /**
     * UC18: Lấy chi tiết đơn hàng
     */
    @GetMapping("/don-hang/{donHangId}")
    public ResponseEntity<DonHang> layChiTietDonHang(@PathVariable UUID donHangId) {
        return donHangRepository.findById(donHangId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * UC18: Lấy danh sách đơn hàng theo trạng thái
     */
    @GetMapping("/don-hang/trang-thai/{trangThai}")
    public ResponseEntity<Map<String, Object>> layDonHangTheoTrangThai(@PathVariable String trangThai) {
        return ResponseEntity.ok(Map.of(
                "message", "Lọc đơn hàng theo trạng thái: " + trangThai,
                "trangThai", trangThai
        ));
    }

    // ==================== UC19: Quản lý khách hàng ====================

    /**
     * UC19: Lấy danh sách khách hàng
     */
    @GetMapping("/khach-hang")
    public ResponseEntity<List<NguoiDung>> layDanhSachKhachHang() {
        return ResponseEntity.ok(nguoiDungService.danhSachKhachHang());
    }

    /**
     * UC19: Lấy chi tiết khách hàng
     */
    @GetMapping("/khach-hang/{khachHangId}")
    public ResponseEntity<NguoiDung> layChiTietKhachHang(@PathVariable UUID khachHangId) {
        return nguoiDungService.timTheoId(khachHangId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * UC19: Khóa/Mở khóa tài khoản khách hàng
     */
    @PatchMapping("/khach-hang/{khachHangId}/trang-thai")
    public ResponseEntity<NguoiDung> khoaMoTaiKhoanKhachHang(
            @PathVariable UUID khachHangId,
            @RequestBody Map<String, Boolean> request) {
        boolean kichHoat = request.getOrDefault("kichHoat", false);
        return ResponseEntity.ok(nguoiDungService.khoaMoTaiKhoan(khachHangId, kichHoat));
    }

    /**
     * UC19: Thống kê khách hàng
     */
    @GetMapping("/khach-hang/thong-ke")
    public ResponseEntity<Map<String, Object>> thongKeKhachHang() {
        List<NguoiDung> khachHangs = nguoiDungService.danhSachKhachHang();
        return ResponseEntity.ok(Map.of(
                "tongSoKhachHang", khachHangs.size(),
                "khachHangs", khachHangs
        ));
    }

    // ==================== UC20: Quản lý khuyến mại ====================

    /**
     * UC20: TODO - Quản lý khuyến mại (cần Service)
     */
    @GetMapping("/khuyen-mai")
    public ResponseEntity<Map<String, String>> layDanhSachKhuyenMai() {
        return ResponseEntity.ok(Map.of(
                "message", "Tính năng quản lý khuyến mại đang được phát triển"
        ));
    }

    @PostMapping("/khuyen-mai")
    public ResponseEntity<Map<String, String>> taoKhuyenMai(@RequestBody KhuyenMaiRequest request) {
        return ResponseEntity.ok(Map.of(
                "message", "Tạo khuyến mại: " + request.maGiamGia(),
                "status", "pending"
        ));
    }

    // ==================== UC21: Xem báo cáo doanh thu ====================

    /**
     * UC21: Báo cáo doanh thu theo khoảng thời gian
     */
    @GetMapping("/bao-cao/doanh-thu")
    public ResponseEntity<Map<String, Object>> baoCaoDoanhThu(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay) {
        try {
            LocalDateTime from = LocalDateTime.of(tuNgay, LocalTime.MIN);
            LocalDateTime to = LocalDateTime.of(denNgay, LocalTime.MAX);
            BigDecimal doanhThu = donHangRepository.tinhDoanhThuTrongKhoang(from, to);
            
            Map<String, Object> response = new HashMap<>();
            response.put("tuNgay", tuNgay);
            response.put("denNgay", denNgay);
            response.put("tongDoanhThu", doanhThu != null ? doanhThu : BigDecimal.ZERO);
            response.put("donHangCount", donHangRepository.count());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Lỗi tính toán doanh thu: " + e.getMessage()
            ));
        }
    }

    /**
     * UC21: Báo cáo chi tiết theo sản phẩm
     */
    @GetMapping("/bao-cao/san-pham")
    public ResponseEntity<Map<String, Object>> baoCaoSanPham() {
        List<NuocUongSan> sanPhams = sanPhamService.layDanhSachNuocUong();
        return ResponseEntity.ok(Map.of(
                "tongLoaiSanPham", sanPhams.size(),
                "sanPhams", sanPhams
        ));
    }

    /**
     * UC21: Báo cáo tháng
     */
    @GetMapping("/bao-cao/hang-thang")
    public ResponseEntity<Map<String, Object>> baoCaoThang(
            @RequestParam int thang,
            @RequestParam int nam) {
        LocalDateTime from = LocalDateTime.of(nam, thang, 1, 0, 0, 0);
        LocalDateTime to = LocalDateTime.of(nam, thang, 28, 23, 59, 59); // Simplified
        BigDecimal doanhThu = donHangRepository.tinhDoanhThuTrongKhoang(from, to);
        
        return ResponseEntity.ok(Map.of(
                "thang", thang,
                "nam", nam,
                "tongDoanhThu", doanhThu != null ? doanhThu : BigDecimal.ZERO
        ));
    }

    // ==================== UC22: Quản lý nhân viên ====================

    /**
     * UC22: Lấy danh sách nhân viên
     */
    @GetMapping("/nhan-vien")
    public ResponseEntity<List<NguoiDung>> layDanhSachNhanVien() {
        return ResponseEntity.ok(nguoiDungService.danhSachNhanVien());
    }

    /**
     * UC22: Tạo nhân viên mới
     */
    @PostMapping("/nhan-vien")
    public ResponseEntity<NguoiDung> taoNhanVien(@RequestBody TaoNhanVienRequest request) {
        try {
            NguoiDung nhanVien = nguoiDungService.taoNhanVien(
                    request.tenDangNhap(),
                    request.email(),
                    request.matKhau()
            );
            return ResponseEntity.ok(nhanVien);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * UC22: Khóa/Mở khóa tài khoản nhân viên
     */
    @PatchMapping("/nhan-vien/{nhanVienId}/trang-thai")
    public ResponseEntity<NguoiDung> khoaMoTaiKhoanNhanVien(
            @PathVariable UUID nhanVienId,
            @RequestBody Map<String, Boolean> request) {
        boolean kichHoat = request.getOrDefault("kichHoat", false);
        return ResponseEntity.ok(nguoiDungService.khoaMoTaiKhoan(nhanVienId, kichHoat));
    }

    /**
     * API chung cho UI admin: khóa/mở khóa tài khoản (khách hàng/nhân viên).
     * UI hiện gọi: PATCH /api/admin/tai-khoan/trang-thai với body { nguoiDungId, active }.
     */
    @PatchMapping("/tai-khoan/trang-thai")
    public ResponseEntity<Map<String, Object>> capNhatTrangThaiTaiKhoan(@RequestBody CapNhatTrangThaiTaiKhoanRequest request) {
        try {
            if (request.nguoiDungId() == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "nguoiDungId là bắt buộc"
                ));
            }
            boolean kichHoat = request.active() != null && request.active();
            NguoiDung updated = nguoiDungService.khoaMoTaiKhoan(request.nguoiDungId(), kichHoat);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Cập nhật trạng thái tài khoản thành công",
                    "user", updated
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * UC22: Lấy chi tiết nhân viên
     */
    @GetMapping("/nhan-vien/{nhanVienId}")
    public ResponseEntity<NguoiDung> layChiTietNhanVien(@PathVariable UUID nhanVienId) {
        return nguoiDungService.timTheoId(nhanVienId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ==================== Request Classes ====================

    public record TaoNhanVienRequest(
            String tenDangNhap,
            String email,
            String matKhau
    ) {}

    public record KhoaMoTaiKhoanRequest(
            UUID userId,
            boolean kichHoat
    ) {}

    public record CapNhatTrangThaiTaiKhoanRequest(
            UUID nguoiDungId,
            Boolean active
    ) {}

    public record KhuyenMaiRequest(
            String maGiamGia,
            BigDecimal phanTramGiam,
            LocalDate ngayBatDau,
            LocalDate ngayKetThuc
    ) {}
}


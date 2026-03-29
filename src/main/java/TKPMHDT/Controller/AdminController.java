package TKPMHDT.Controller;

import TKPMHDT.Entity.nguoidung.NguoiDung;
import TKPMHDT.Service.nguoidung.NguoiDungService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import TKPMHDT.Repository.donhang.DonHangRepository;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('QUAN_TRI_VIEN')")
public class AdminController {

    private final NguoiDungService nguoiDungService;
    private final DonHangRepository donHangRepository;

    public AdminController(NguoiDungService nguoiDungService, DonHangRepository donHangRepository) {
        this.nguoiDungService = nguoiDungService;
        this.donHangRepository = donHangRepository;
    }

    @GetMapping("/khach-hang")
    public List<NguoiDung> danhSachKhachHang() {
        return nguoiDungService.danhSachKhachHang();
    }

    @GetMapping("/nhan-vien")
    public List<NguoiDung> danhSachNhanVien() {
        return nguoiDungService.danhSachNhanVien();
    }

    @PostMapping("/nhan-vien")
    public NguoiDung taoNhanVien(@RequestBody TaoNhanVienRequest request) {
        return nguoiDungService.taoNhanVien(request.tenDangNhap(), request.email(), request.matKhau());
    }

    @PatchMapping("/tai-khoan/trang-thai")
    public NguoiDung khoaMoTaiKhoan(@RequestBody KhoaMoTaiKhoanRequest request) {
        return nguoiDungService.khoaMoTaiKhoan(request.userId(), request.kichHoat());
    }

    @GetMapping("/bao-cao/doanh-thu")
    public Map<String, Object> baoCaoDoanhThu(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay
    ) {
        LocalDateTime from = LocalDateTime.of(tuNgay, LocalTime.MIN);
        LocalDateTime to = LocalDateTime.of(denNgay, LocalTime.MAX);
        var doanhThu = donHangRepository.tinhDoanhThuTrongKhoang(from, to);
        return Map.of(
                "tuNgay", tuNgay,
                "denNgay", denNgay,
                "tongDoanhThu", doanhThu
        );
    }

    public record TaoNhanVienRequest(String tenDangNhap, String email, String matKhau) {
    }

    public record KhoaMoTaiKhoanRequest(UUID userId, boolean kichHoat) {
    }
}


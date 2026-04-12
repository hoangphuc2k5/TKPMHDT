package TKPMHDT.Controller.auth;

import TKPMHDT.Entity.nguoidung.NguoiDung;
import TKPMHDT.Service.nguoidung.DangKyService;
import TKPMHDT.Service.nguoidung.DangNhapService;
import TKPMHDT.Service.nguoidung.DangXuatService;
import TKPMHDT.Service.nguoidung.QuenMatKhauService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AuthController - Quản lý xác thực người dùng
 * UC01: Đăng ký tài khoản
 * UC02: Đăng nhập
 * UC03: Đăng xuất
 * UC04: Quên mật khẩu (OTP)
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final DangKyService dangKyService;
    private final DangNhapService dangNhapService;
    private final DangXuatService dangXuatService;
    private final QuenMatKhauService quenMatKhauService;

    public AuthController(
            DangKyService dangKyService,
            DangNhapService dangNhapService,
            DangXuatService dangXuatService,
            QuenMatKhauService quenMatKhauService) {
        this.dangKyService = dangKyService;
        this.dangNhapService = dangNhapService;
        this.dangXuatService = dangXuatService;
        this.quenMatKhauService = quenMatKhauService;
    }

    /**
     * UC01: Đăng ký tài khoản
     */
    @PostMapping("/dang-ky")
    public ResponseEntity<?> dangKy(@RequestBody DangKyRequest request) {
        try {
            NguoiDung nguoiDung = dangKyService.dangKyKhachHang(
                    request.tenDangNhap(),
                    request.email(),
                    request.matKhau(),
                    request.hoTen()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đăng ký tài khoản thành công");
            response.put("userId", nguoiDung.getId());
            response.put("tenDangNhap", nguoiDung.getTenDangNhap());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * UC02: Đăng nhập
     */
    @PostMapping("/dang-nhap")
    public ResponseEntity<?> dangNhap(@RequestBody DangNhapRequest request) {
        try {
            NguoiDung nguoiDung = dangNhapService.xacThucDangNhapBangDinhDanh(
                    request.tenDangNhapHoacEmail(),
                    request.matKhau()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đăng nhập thành công");
            response.put("userId", nguoiDung.getId());
            response.put("tenDangNhap", nguoiDung.getTenDangNhap());
            response.put("vaiTro", nguoiDung.getVaiTro());
            response.put("email", nguoiDung.getEmail());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * UC03: Đăng xuất
     */
    @PostMapping("/dang-xuat")
    public ResponseEntity<?> dangXuat() {
        dangXuatService.xoaPhoiBanHienTai();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đăng xuất thành công"
        ));
    }

    /**
     * UC04: Quên mật khẩu - Gửi OTP
     */
    @PostMapping("/quen-mat-khau/gui-otp")
    public ResponseEntity<?> guiOtp(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            if (email == null || email.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Email là bắt buộc"
                ));
            }
            quenMatKhauService.taoVaGuiOtp(email);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "OTP đã được gửi đến email của bạn"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * UC04: Quên mật khẩu - Đặt lại mật khẩu
     */
    @PostMapping("/quen-mat-khau/dat-lai")
    public ResponseEntity<?> datLaiMatKhau(@RequestBody DatLaiMatKhauRequest request) {
        try {
            quenMatKhauService.xacThucOtpVaDatLaiMatKhau(
                    request.email(),
                    request.otp(),
                    request.matKhauMoi()
            );
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Mật khẩu đã được đặt lại thành công"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    // ==================== Request/Response Records ====================
    
    public record DangKyRequest(
            String tenDangNhap,
            String email,
            String matKhau,
            String hoTen
    ) {}

    public record DangNhapRequest(
            String tenDangNhapHoacEmail,
            String matKhau
    ) {}

    public record DatLaiMatKhauRequest(
            String email,
            String otp,
            String matKhauMoi
    ) {}
}


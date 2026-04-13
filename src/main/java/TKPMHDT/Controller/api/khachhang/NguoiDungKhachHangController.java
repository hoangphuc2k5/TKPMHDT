package TKPMHDT.Controller.api.khachhang;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import TKPMHDT.Entity.nguoidung.KhachHang;
import TKPMHDT.Entity.nguoidung.NguoiDung;
import TKPMHDT.Service.FileStorageService;
import TKPMHDT.Service.nguoidung.NguoiDungService;

/**
 * API người dùng — tài khoản khách hàng (UC12) và đăng ký khách qua API.
 */
@RestController
@RequestMapping("/api/nguoi-dung")
public class NguoiDungKhachHangController {

    private final NguoiDungService nguoiDungService;
    private final FileStorageService fileStorageService;

    public NguoiDungKhachHangController(NguoiDungService nguoiDungService, FileStorageService fileStorageService) {
        this.nguoiDungService = nguoiDungService;
        this.fileStorageService = fileStorageService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<NguoiDung> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(401).build();
        }
        return nguoiDungService.timTheoTenDangNhap(auth.getName())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(401).build());
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/me")
    public ResponseEntity<Map<String, Object>> updateCurrentUser(@RequestBody UpdateNguoiDungRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(401).build();
            }

            NguoiDung nguoiDung = nguoiDungService.timTheoTenDangNhap(auth.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));

            if (request.email() != null && !request.email().isBlank()) {
                nguoiDung.setEmail(request.email());
            }
            if (request.hoTen() != null && !request.hoTen().isBlank()) {
                if (nguoiDung instanceof KhachHang khachHang) {
                    khachHang.setHoTen(request.hoTen());
                }
            }
            if (request.soDienThoai() != null && !request.soDienThoai().isBlank()) {
                if (nguoiDung instanceof KhachHang khachHang) {
                    khachHang.setSoDienThoai(request.soDienThoai());
                }
            }

            NguoiDung updated = nguoiDungService.luuNguoiDung(nguoiDung);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cập nhật thông tin thành công");
            response.put("user", updated);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/me/avatar")
    public ResponseEntity<Map<String, Object>> uploadAvatar(@RequestParam("avatar") MultipartFile file) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(401).build();
            }

            NguoiDung nguoiDung = nguoiDungService.timTheoTenDangNhap(auth.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));

            String url = fileStorageService.storeFile(file);
            nguoiDung.setAvatar(url);
            NguoiDung updated = nguoiDungService.luuNguoiDung(nguoiDung);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Upload avatar thành công");
            response.put("avatarUrl", url);
            response.put("user", updated);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/me/doi-mat-khau")
    public ResponseEntity<Map<String, Object>> doiMatKhau(@RequestBody DoiMatKhauRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(401).build();
            }

            nguoiDungService.doiMatKhau(auth.getName(), request.matKhauCu(), request.matKhauMoi(), request.xacNhanMatKhau());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đổi mật khẩu thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/me")
    public ResponseEntity<Map<String, Object>> xoaTaiKhoanHienTai() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(401).build();
            }

            NguoiDung nguoiDung = nguoiDungService.timTheoTenDangNhap(auth.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));

            nguoiDungService.xoaNguoiDung(nguoiDung.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Tài khoản đã được xóa thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }

    @PostMapping("/dang-ky-khach-hang")
    public ResponseEntity<KhachHang> dangKyKhachHang(@RequestBody DangKyKhachHangRequest request) {
        KhachHang khachHang = nguoiDungService.dangKyKhachHang(
                request.tenDangNhap(),
                request.email(),
                request.matKhauHash());
        return ResponseEntity.ok(khachHang);
    }

    public record DangKyKhachHangRequest(
            String tenDangNhap,
            String email,
            String matKhauHash) {
    }

    public record UpdateNguoiDungRequest(
            String email,
            String hoTen,
            String soDienThoai) {
    }

    public record DoiMatKhauRequest(
            String matKhauCu,
            String matKhauMoi,
            String xacNhanMatKhau) {
    }
}

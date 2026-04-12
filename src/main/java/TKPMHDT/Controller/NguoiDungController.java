package TKPMHDT.Controller;

import TKPMHDT.Entity.nguoidung.KhachHang;
import TKPMHDT.Entity.nguoidung.NguoiDung;
import TKPMHDT.Service.FileStorageService;
import TKPMHDT.Service.nguoidung.NguoiDungService;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * NguoiDungController - Quản lý người dùng
 * UC12: Quản lý tài khoản cá nhân
 */
@RestController
@RequestMapping("/api/nguoi-dung")
public class NguoiDungController {

    private final NguoiDungService nguoiDungService;
    private final FileStorageService fileStorageService;

    public NguoiDungController(NguoiDungService nguoiDungService, FileStorageService fileStorageService) {
        this.nguoiDungService = nguoiDungService;
        this.fileStorageService = fileStorageService;
    }

    // ==================== UC12: Quản lý tài khoản cá nhân ====================

    /**
     * UC12: Lấy thông tin tài khoản hiện tại (của người dùng đang đăng nhập)
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<NguoiDung> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(401).build();
        }
        return nguoiDungService.timTheoTenDangNhap(auth.getName())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(401).build());
    }

    /**
     * UC12: Cập nhật thông tin tài khoản
     */
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

            // Cập nhật thông tin
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
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * UC12: Upload avatar
     */
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
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * UC12: Thay đổi mật khẩu
     */
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
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * Xóa tài khoản của người dùng hiện tại
     */
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
                    "message", e.getMessage()
            ));
        }
    }

    // ==================== Admin: Quản lý người dùng ====================

    @PostMapping("/dang-ky-khach-hang")
    public ResponseEntity<KhachHang> dangKyKhachHang(@RequestBody DangKyKhachHangRequest request) {
        KhachHang khachHang = nguoiDungService.dangKyKhachHang(
                request.tenDangNhap(),
                request.email(),
                request.matKhauHash()
        );
        return ResponseEntity.ok(khachHang);
    }

    @PostMapping("/{id}/avatar")
    public ResponseEntity<NguoiDung> uploadAvatar(
            @PathVariable UUID id,
            @RequestParam("avatar") MultipartFile file) {
        return nguoiDungService.timTheoId(id).map(nguoiDung -> {
            String url = fileStorageService.storeFile(file);
            nguoiDung.setAvatar(url);
            return ResponseEntity.ok(nguoiDungService.luuNguoiDung(nguoiDung));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NguoiDung> layTheoId(@PathVariable UUID id) {
        return nguoiDungService.timTheoId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/ten-dang-nhap/{tenDangNhap}")
    public ResponseEntity<NguoiDung> layTheoTenDangNhap(@PathVariable String tenDangNhap) {
        return nguoiDungService.timTheoTenDangNhap(tenDangNhap)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/tim-theo-email")
    public ResponseEntity<?> layTheoEmail(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body("Email is required");
        }
        return nguoiDungService.timTheoEmail(email)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ==================== Request Classes ====================

    public record DangKyKhachHangRequest(
            String tenDangNhap,
            String email,
            String matKhauHash
    ) {}

    public record UpdateNguoiDungRequest(
            String email,
            String hoTen,
            String soDienThoai
    ) {}

    public record DoiMatKhauRequest(
            String matKhauCu,
            String matKhauMoi,
            String xacNhanMatKhau
    ) {}
}


package TKPMHDT.Controller.web.chung;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import TKPMHDT.Service.sanpham.SanPhamService;

/**
 * Trang dùng chung: trang chủ (có chuyển hướng theo vai trò), đăng nhập / đăng ký / quên mật khẩu.
 */
@Controller
public class ChungPageController {

    private final SanPhamService sanPhamService;

    public ChungPageController(SanPhamService sanPhamService) {
        this.sanPhamService = sanPhamService;
    }

    @GetMapping("/")
    public String index(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            for (GrantedAuthority a : authentication.getAuthorities()) {
                if ("ROLE_QUAN_TRI_VIEN".equals(a.getAuthority())) {
                    return "redirect:/ui/admin/dashboard";
                }
            }
            for (GrantedAuthority a : authentication.getAuthorities()) {
                if ("ROLE_NHAN_VIEN_BAN_HANG".equals(a.getAuthority())) {
                    return "redirect:/ui/don-hang";
                }
            }
        }
        model.addAttribute("products", sanPhamService.layDanhSachNuocUongChoKhachHang());
        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping("/quen-mat-khau")
    public String forgotPasswordPage() {
        return "forgot-password";
    }
}

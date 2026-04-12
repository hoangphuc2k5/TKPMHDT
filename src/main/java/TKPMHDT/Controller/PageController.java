package TKPMHDT.Controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import TKPMHDT.Service.sanpham.SanPhamService;

@Controller
public class PageController {

    private final SanPhamService sanPhamService;

    public PageController(SanPhamService sanPhamService) {
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

    @GetMapping("/ui/nguoi-dung")
    public String nguoiDungPage() {
        return "nguoidung";
    }

    @GetMapping("/ui/san-pham")
    public String sanPhamPage() {
        return "sanpham";
    }
    @GetMapping("/ui/san-pham/chi-tiet")
    public String chiTietSanPhamPage() {
        return "sanpham-chi-tiet";
    }

    @GetMapping("/ui/gio-hang")
    public String gioHangPage() {
        return "giohang";
    }

    @GetMapping("/ui/thanh-toan")
    public String thanhToanPage() {
        return "thanhtoan";
    }

    @GetMapping("/ui/khuyen-mai")
    public String khuyenMaiPage() {
        return "khuyenmai";
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

    @GetMapping("/ui/admin/dashboard")
    public String adminDashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/ui/tai-khoan")
    public String taiKhoanPage() {
        return "tai-khoan";
    }
        
    @GetMapping("/ui/don-hang")
    public String donHangPage() {
        return "donhang";
    }

    @GetMapping({"/ui/pos/tao-don", "/ui/pos/tao-don/"})
    public String posPage() {
        return "pos/pos-interface";
    }


    @GetMapping({"/ui/pos", "/ui/pos/danh-sach-don-hang"})
    public String posDanhSachDonHang() {
        return "pos/danh-sach-don-hang";
    }
    
}


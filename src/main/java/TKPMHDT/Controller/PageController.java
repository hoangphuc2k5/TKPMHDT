package TKPMHDT.Controller;

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
    public String index(Model model) {
        // Lấy danh sách nước uống từ database để hiển thị trên trang chủ
        model.addAttribute("products", sanPhamService.layDanhSachNuocUong());
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

    @GetMapping("/ui/gio-hang")
    public String gioHangPage() {
        return "giohang";
    }

    @GetMapping("/ui/don-hang")
    public String donHangPage() {
        return "donhang";
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

    @GetMapping("/ui/admin/quan-ly-san-pham")
    public String adminProductManagement() {
        return "admin/manage-products";
    }

    @GetMapping("/ui/admin/quan-ly-nhan-vien")
    public String adminStaffManagement() {
        return "admin/manage-staff";
    }

    @GetMapping("/ui/admin/quan-ly-khach-hang")
    public String adminCustomersManagement() {
        return "admin/manage-customers";
    }

    @GetMapping("/ui/pos")
    public String posPage() {
        return "pos/pos-interface";
    }
}


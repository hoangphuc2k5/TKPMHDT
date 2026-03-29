package TKPMHDT.Controller;

import TKPMHDT.Service.sanpham.SanPhamService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
}


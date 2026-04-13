package TKPMHDT.Controller.web.khachhang;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Giao diện web cho khách hàng (cửa hàng, giỏ, thanh toán, tài khoản).
 */
@Controller
public class KhachHangPageController {

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
        return "redirect:/";
    }

    @GetMapping("/ui/tai-khoan")
    public String taiKhoanPage() {
        return "tai-khoan";
    }
}

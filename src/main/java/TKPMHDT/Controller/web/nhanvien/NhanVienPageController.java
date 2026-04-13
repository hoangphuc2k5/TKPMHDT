package TKPMHDT.Controller.web.nhanvien;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Giao diện web cho nhân viên bán hàng (đơn hàng, POS).
 */
@Controller
public class NhanVienPageController {

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

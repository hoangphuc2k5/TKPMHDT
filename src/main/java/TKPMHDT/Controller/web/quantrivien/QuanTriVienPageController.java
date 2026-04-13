package TKPMHDT.Controller.web.quantrivien;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Giao diện web cho quản trị viên.
 */
@Controller
public class QuanTriVienPageController {

    @GetMapping("/ui/admin/dashboard")
    public String adminDashboard() {
        return "admin/dashboard";
    }
}

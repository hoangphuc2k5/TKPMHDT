package TKPMHDT.Controller;

import TKPMHDT.Service.nguoidung.QuenMatKhauService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final QuenMatKhauService quenMatKhauService;

    public AuthController(QuenMatKhauService quenMatKhauService) {
        this.quenMatKhauService = quenMatKhauService;
    }

    @PostMapping("/quen-mat-khau/gui-otp")
    public ResponseEntity<?> guiOtp(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body("email is required");
        }
        quenMatKhauService.taoVaGuiOtp(email);
        return ResponseEntity.ok("Da gui OTP (dev: xem console)");
    }

    @PostMapping("/quen-mat-khau/dat-lai")
    public ResponseEntity<?> datLaiMatKhau(@RequestBody DatLaiMatKhauRequest request) {
        quenMatKhauService.xacThucOtpVaDatLaiMatKhau(
                request.email(),
                request.otp(),
                request.matKhauMoi()
        );
        return ResponseEntity.ok("Dat lai mat khau thanh cong");
    }

    public record DatLaiMatKhauRequest(String email, String otp, String matKhauMoi) {
    }
}


package TKPMHDT.Controller.api.khachhang;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import TKPMHDT.Entity.thanhtoan.ThanhToan;
import TKPMHDT.Service.thanhtoan.ThanhToanService;
import lombok.RequiredArgsConstructor;

/**
 * Thanh toán — tra cứu theo đơn (khách theo dõi đơn).
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/thanh-toan")
public class ThanhToanKhachHangController {

    private final ThanhToanService thanhToanService;

    @PreAuthorize("hasAnyAuthority('order:track','order:view')")
    @GetMapping("/don-hang/{donHangId}")
    public ResponseEntity<ThanhToan> layTheoDonHang(@PathVariable UUID donHangId) {
        return thanhToanService.layTheoDonHangId(donHangId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

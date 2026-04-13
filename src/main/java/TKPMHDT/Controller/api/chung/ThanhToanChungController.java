package TKPMHDT.Controller.api.chung;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import TKPMHDT.DTO.ApiResponse;
import TKPMHDT.DTO.request.TaoThanhToanRequest;
import TKPMHDT.DTO.response.ThanhToanResponse;
import TKPMHDT.Util.ResponseFactory;
import TKPMHDT.facade.PosFacade;
import lombok.RequiredArgsConstructor;

/**
 * Thanh toán — thao tác dùng chung khách hàng & nhân viên POS (tạo thanh toán).
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/thanh-toan")
public class ThanhToanChungController {

    private final PosFacade posFacade;

    @PreAuthorize("hasAnyAuthority('order:customer-create','pos:create')")
    @PostMapping("/tao-thanh-toan")
    public ResponseEntity<ApiResponse<ThanhToanResponse>> taoThanhToan(@RequestBody TaoThanhToanRequest request) {
        ThanhToanResponse thanhToan = posFacade.taoThanhToan(request);
        return ResponseFactory.success(thanhToan, "Tao thanh toan thanh cong");
    }
}

package TKPMHDT.Controller;

import TKPMHDT.DTO.ApiResponse;
import TKPMHDT.DTO.request.TaoThanhToanRequest;
import TKPMHDT.DTO.response.ThanhToanResponse;
import TKPMHDT.Entity.thanhtoan.ThanhToan;
import TKPMHDT.Entity.thanhtoan.enums.PhuongThucThanhToanEnum;
import TKPMHDT.Entity.thanhtoan.enums.TrangThaiThanhToanEnum;
import TKPMHDT.Service.thanhtoan.ThanhToanService;
import TKPMHDT.Util.ResponseFactory;
import TKPMHDT.facade.PosFacade;
import lombok.RequiredArgsConstructor;

import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/thanh-toan")
public class ThanhToanController {

    private final ThanhToanService thanhToanService;
    private final PosFacade posFacade;

    // Tạo thanh toán cho đơn hàng
    @PreAuthorize("hasAnyAuthority('order:customer-create','pos:create')")
    @PostMapping("/tao-thanh-toan")
    public ResponseEntity<ApiResponse<ThanhToanResponse>> taoThanhToan(@RequestBody TaoThanhToanRequest request) {
        ThanhToanResponse thanhToan = posFacade.taoThanhToan(request);
        return ResponseFactory.success(thanhToan, "Tao thanh toan thanh cong");
    }




    @PreAuthorize("hasAuthority('order:update')")
    @PatchMapping("/{thanhToanId}/trang-thai")
    public ResponseEntity<ThanhToan> capNhatTrangThai(
            @PathVariable UUID thanhToanId,
            @RequestBody CapNhatTrangThaiThanhToanRequest request
    ) {
        ThanhToan thanhToan = thanhToanService.capNhatTrangThai(thanhToanId, request.trangThaiMoi());
        return ResponseEntity.ok(thanhToan);
    }

    @PreAuthorize("hasAnyAuthority('order:track','order:view')")
    @GetMapping("/don-hang/{donHangId}")
    public ResponseEntity<ThanhToan> layTheoDonHang(@PathVariable UUID donHangId) {
        return thanhToanService.layTheoDonHangId(donHangId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    

    public record CapNhatTrangThaiThanhToanRequest(TrangThaiThanhToanEnum trangThaiMoi) {
    }

    // Xác nhận thanh toán thành công, in hóa đơn
    @PreAuthorize("hasAuthority('print:invoice')")
    @PatchMapping("/{thanhToanId}/xac-nhan-thanh-toan")
    public ResponseEntity<ApiResponse<String>> xacNhanThanhToan(@PathVariable UUID thanhToanId) {
        posFacade.xacNhanThanhToan(thanhToanId);
        return ResponseFactory.success(null,"Xác nhận thanh toán thành công và in hóa đơn");
    }
}


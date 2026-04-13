package TKPMHDT.Controller.api.nhanvien;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import TKPMHDT.DTO.ApiResponse;
import TKPMHDT.Entity.thanhtoan.ThanhToan;
import TKPMHDT.Entity.thanhtoan.enums.TrangThaiThanhToanEnum;
import TKPMHDT.Service.thanhtoan.ThanhToanService;
import TKPMHDT.Util.ResponseFactory;
import TKPMHDT.facade.PosFacade;
import lombok.RequiredArgsConstructor;

/**
 * Thanh toán — cập nhật trạng thái, xác nhận in hóa đơn (nhân viên / quyền hệ thống).
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/thanh-toan")
public class ThanhToanNhanVienController {

    private final ThanhToanService thanhToanService;
    private final PosFacade posFacade;

    @PreAuthorize("hasAuthority('order:update')")
    @PatchMapping("/{thanhToanId}/trang-thai")
    public ResponseEntity<ThanhToan> capNhatTrangThai(
            @PathVariable UUID thanhToanId,
            @RequestBody CapNhatTrangThaiThanhToanRequest request) {
        ThanhToan thanhToan = thanhToanService.capNhatTrangThai(thanhToanId, request.trangThaiMoi());
        return ResponseEntity.ok(thanhToan);
    }

    @PreAuthorize("hasAuthority('print:invoice')")
    @PatchMapping("/{thanhToanId}/xac-nhan-thanh-toan")
    public ResponseEntity<ApiResponse<String>> xacNhanThanhToan(@PathVariable UUID thanhToanId) {
        posFacade.xacNhanThanhToan(thanhToanId);
        return ResponseFactory.success(null, "Xác nhận thanh toán thành công và in hóa đơn");
    }

    public record CapNhatTrangThaiThanhToanRequest(TrangThaiThanhToanEnum trangThaiMoi) {
    }
}

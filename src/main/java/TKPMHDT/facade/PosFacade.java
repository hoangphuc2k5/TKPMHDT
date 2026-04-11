package TKPMHDT.facade;

import java.util.UUID;

import org.springframework.stereotype.Service;

import TKPMHDT.DTO.request.SanPhamOrderRequest;
import TKPMHDT.DTO.request.TaoThanhToanRequest;
import TKPMHDT.DTO.response.DonHangResponse;
import TKPMHDT.DTO.response.ThanhToanResponse;
import TKPMHDT.Entity.donhang.HoaDon;
import TKPMHDT.Entity.thanhtoan.ThanhToan;
import TKPMHDT.Service.donhang.DonHangService;
import TKPMHDT.Service.donhang.HoaDonService;
import TKPMHDT.Service.thanhtoan.ThanhToanService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PosFacade {

    private final DonHangService donHangService;
    private final ThanhToanService thanhToanService;
    private final HoaDonService hoaDonService;

    public UUID taoDonHangTaiQuay() {
        return donHangService.taoDonHangTaiQuay();
    }
    
    public DonHangResponse themSanPham(UUID donHangId, SanPhamOrderRequest request) {
        return donHangService.themChiTietVaoDonHangTaiQuay(donHangId, request);
    }

    public void xacNhanDonHang(UUID donHangId) {
        donHangService.xacNhanDonHang(donHangId);
        
    }

    public ThanhToanResponse taoThanhToan(TaoThanhToanRequest request) {
        return thanhToanService.taoThanhToan(request);
    }

    public void xacNhanThanhToan(UUID thanhToanId) {

        // 1. confirm thanh toán
        ThanhToan thanhToan = thanhToanService.xacNhanThanhToanThanhCong(thanhToanId);

        // 2. cập nhật trạng thái đơn hàng
        donHangService.giaoDonHang(thanhToan.getDonHang().getId());
        // 3. in hóa đơn
        hoaDonService.taoHoaDonChoDonHangKhiThanhToanThanhCong(thanhToan);
    }

    public void hoanThanhDonHang(UUID donHangId) {
        donHangService.hoanThanhDonHang(donHangId);
    }

    public void huyDonHang(UUID donHangId) {
        donHangService.huyDonHang(donHangId);
    }
    
    public void tangSoLuong(UUID chiTietDonHangId) {
        donHangService.tangSoLuong(chiTietDonHangId);
    }

    public void giamSoLuong(UUID chiTietDonHangId) {
        donHangService.giamSoLuong(chiTietDonHangId);
    }

    public void xoaChiTietDonHang(UUID chiTietDonHangId) {
        donHangService.xoaChiTietDonHang(chiTietDonHangId);
    }
}

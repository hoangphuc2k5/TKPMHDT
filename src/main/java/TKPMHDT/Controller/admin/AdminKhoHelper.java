package TKPMHDT.Controller.admin;

import TKPMHDT.Entity.sanpham.LichSuKho;
import TKPMHDT.Entity.sanpham.NguyenLieu;
import TKPMHDT.Repository.sanpham.LichSuKhoRepository;
import TKPMHDT.Service.sanpham.SanPhamService;
import java.math.BigDecimal;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class AdminKhoHelper {

    private final SanPhamService sanPhamService;
    private final LichSuKhoRepository lichSuKhoRepository;
    private final AdminAuditHelper audit;

    public AdminKhoHelper(
            SanPhamService sanPhamService,
            LichSuKhoRepository lichSuKhoRepository,
            AdminAuditHelper audit) {
        this.sanPhamService = sanPhamService;
        this.lichSuKhoRepository = lichSuKhoRepository;
        this.audit = audit;
    }

    public ResponseEntity<NguyenLieu> capNhatKho(AdminApiDtos.CapNhatKhoRequest request, String loai) {
        return sanPhamService.layNguyenLieuTheoId(request.nguyenLieuId()).map(nguyenLieu -> {
            BigDecimal soLuong = request.soLuong() == null ? BigDecimal.ZERO : request.soLuong();
            BigDecimal tonTruoc = nguyenLieu.getSoLuongTon() == null ? BigDecimal.ZERO : nguyenLieu.getSoLuongTon();
            BigDecimal tonSau = "NHAP".equalsIgnoreCase(loai) ? tonTruoc.add(soLuong) : tonTruoc.subtract(soLuong);
            if (tonSau.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Số lượng tồn không đủ để xuất");
            }
            nguyenLieu.setSoLuongTon(tonSau);
            NguyenLieu saved = sanPhamService.luuNguyenLieu(nguyenLieu);
            LichSuKho lichSuKho = LichSuKho.builder()
                    .nguyenLieu(saved)
                    .loai(loai)
                    .soLuong(soLuong)
                    .tonTruoc(tonTruoc)
                    .tonSau(tonSau)
                    .ghiChu(request.ghiChu())
                    .nguoiThucHien(audit.tenNguoiDangNhap())
                    .build();
            lichSuKhoRepository.save(lichSuKho);
            audit.ghiLog("KHO", loai, saved.getTen() + ":" + soLuong);
            return ResponseEntity.ok(saved);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}

package TKPMHDT.Controller.admin;

import TKPMHDT.Entity.sanpham.LichSuKho;
import TKPMHDT.Entity.sanpham.NguyenLieu;
import TKPMHDT.Repository.sanpham.LichSuKhoRepository;
import TKPMHDT.Service.sanpham.SanPhamService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize(
        "hasAnyAuthority('product:manage','order:manage-all','inventory:manage','customer:manage','promotion:manage','report:view','staff:manage')")
public class AdminKhoController {

    private final SanPhamService sanPhamService;
    private final LichSuKhoRepository lichSuKhoRepository;
    private final AdminKhoHelper khoHelper;

    public AdminKhoController(
            SanPhamService sanPhamService, LichSuKhoRepository lichSuKhoRepository, AdminKhoHelper khoHelper) {
        this.sanPhamService = sanPhamService;
        this.lichSuKhoRepository = lichSuKhoRepository;
        this.khoHelper = khoHelper;
    }

    @PostMapping("/kho/nhap")
    public ResponseEntity<NguyenLieu> nhapKho(@RequestBody AdminApiDtos.CapNhatKhoRequest request) {
        return khoHelper.capNhatKho(request, "NHAP");
    }

    @PostMapping("/kho/xuat")
    public ResponseEntity<NguyenLieu> xuatKho(@RequestBody AdminApiDtos.CapNhatKhoRequest request) {
        return khoHelper.capNhatKho(request, "XUAT");
    }

    @GetMapping("/kho/canh-bao")
    public ResponseEntity<List<NguyenLieu>> layCanhBaoTonKho() {
        return ResponseEntity.ok(sanPhamService.layNguyenLieuCanhBao());
    }

    @GetMapping("/kho/lich-su")
    public ResponseEntity<List<LichSuKho>> lichSuKho() {
        return ResponseEntity.ok(lichSuKhoRepository.findTop100ByOrderByThoiGianDesc());
    }
}

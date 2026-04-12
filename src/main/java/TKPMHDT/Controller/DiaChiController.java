package TKPMHDT.Controller;

import TKPMHDT.Entity.nguoidung.DiaChi;
import TKPMHDT.Service.nguoidung.DiaChiService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dia-chi")
public class DiaChiController {

    private final DiaChiService diaChiService;

    public DiaChiController(DiaChiService diaChiService) {
        this.diaChiService = diaChiService;
    }

    @PreAuthorize("hasRole('KHACH_HANG')")
    @PostMapping("/{khachHangId}")
    public ResponseEntity<DiaChi> themDiaChi(@PathVariable UUID khachHangId, @RequestBody DiaChi diaChi) {
        return ResponseEntity.ok(diaChiService.themDiaChi(khachHangId, diaChi));
    }

    @PreAuthorize("hasRole('KHACH_HANG')")
    @GetMapping("/{khachHangId}")
    public ResponseEntity<List<DiaChi>> layDiaChi(@PathVariable UUID khachHangId) {
        return ResponseEntity.ok(diaChiService.layDiaChiCuaKhachHang(khachHangId));
    }

    @PreAuthorize("hasRole('KHACH_HANG')")
    @GetMapping("/{khachHangId}/mac-dinh")
    public ResponseEntity<DiaChi> layDiaChiMacDinh(@PathVariable UUID khachHangId) {
        return diaChiService.layDiaChiMacDinh(khachHangId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('KHACH_HANG')")
    @PatchMapping("/{khachHangId}/{diaChiId}/mac-dinh")
    public ResponseEntity<DiaChi> datDiaChiMacDinh(
            @PathVariable UUID khachHangId,
            @PathVariable UUID diaChiId
    ) {
        return ResponseEntity.ok(diaChiService.datDiaChiMacDinh(khachHangId, diaChiId));
    }

    @PreAuthorize("hasRole('KHACH_HANG')")
    @DeleteMapping("/{diaChiId}")
    public ResponseEntity<Void> xoaDiaChi(@PathVariable UUID diaChiId) {
        diaChiService.xoaDiaChi(diaChiId);
        return ResponseEntity.noContent().build();
    }
}

package TKPMHDT.Controller.admin;

import TKPMHDT.Entity.donhang.DonHang;
import TKPMHDT.Entity.donhang.PhieuGiao;
import TKPMHDT.Repository.donhang.DonHangRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize(
        "hasAnyAuthority('product:manage','order:manage-all','inventory:manage','customer:manage','promotion:manage','report:view','staff:manage')")
public class AdminPhieuGiaoController {

    private final DonHangRepository donHangRepository;
    private final AdminPayloadHelper payloads;

    public AdminPhieuGiaoController(DonHangRepository donHangRepository, AdminPayloadHelper payloads) {
        this.donHangRepository = donHangRepository;
        this.payloads = payloads;
    }

    @GetMapping("/phieu-giao")
    public ResponseEntity<List<Map<String, Object>>> layDanhSachPhieuGiao(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay) {
        List<PhieuGiao> phieuGiaos = donHangRepository.findAll().stream()
                .map(DonHang::getPhieuGiao)
                .filter(pg -> pg != null)
                .toList();

        List<PhieuGiao> filtered = phieuGiaos.stream()
                .filter(pg -> {
                    if (tuNgay == null || denNgay == null || pg.getNgayTao() == null) {
                        return true;
                    }
                    LocalDate d = pg.getNgayTao().toLocalDate();
                    return !d.isBefore(tuNgay) && !d.isAfter(denNgay);
                })
                .filter(pg -> trangThai == null
                        || trangThai.isBlank()
                        || pg.getTrangThaiGiao().equalsIgnoreCase(trangThai))
                .filter(pg -> {
                    if (q == null || q.isBlank()) {
                        return true;
                    }
                    String k = q.toLowerCase(Locale.ROOT);
                    String soPhieu = pg.getSoPhieuGiao() != null ? pg.getSoPhieuGiao().toLowerCase(Locale.ROOT) : "";
                    String donHangId = pg.getDonHang() != null && pg.getDonHang().getId() != null
                            ? pg.getDonHang().getId().toString().toLowerCase(Locale.ROOT)
                            : "";
                    return soPhieu.contains(k) || donHangId.contains(k);
                })
                .sorted(Comparator.comparing(PhieuGiao::getNgayTao, Comparator.nullsLast(LocalDateTime::compareTo))
                        .reversed())
                .toList();

        return ResponseEntity.ok(filtered.stream().map(payloads::toPhieuGiaoPayload).toList());
    }

    @GetMapping("/phieu-giao/{phieuGiaoId}")
    public ResponseEntity<Map<String, Object>> layChiTietPhieuGiao(@PathVariable UUID phieuGiaoId) {
        List<PhieuGiao> allPG = donHangRepository.findAll().stream()
                .map(DonHang::getPhieuGiao)
                .filter(pg -> pg != null && pg.getId().equals(phieuGiaoId))
                .toList();
        if (allPG.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        PhieuGiao phieuGiao = allPG.get(0);
        return ResponseEntity.ok(payloads.toPhieuGiaoDetailPayload(phieuGiao));
    }
}

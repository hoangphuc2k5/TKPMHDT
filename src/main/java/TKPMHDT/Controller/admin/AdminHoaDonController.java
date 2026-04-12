package TKPMHDT.Controller.admin;

import TKPMHDT.Entity.donhang.DonHang;
import TKPMHDT.Entity.donhang.HoaDon;
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
public class AdminHoaDonController {

    private final DonHangRepository donHangRepository;
    private final AdminPayloadHelper payloads;

    public AdminHoaDonController(DonHangRepository donHangRepository, AdminPayloadHelper payloads) {
        this.donHangRepository = donHangRepository;
        this.payloads = payloads;
    }

    @GetMapping("/hoa-don")
    @SuppressWarnings("unchecked")
    public ResponseEntity<List<Map<String, Object>>> layDanhSachHoaDon(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay) {
        List<HoaDon> hoaDons = (List<HoaDon>) (Object) donHangRepository.findAll().stream()
                .map(DonHang::getHoaDon)
                .filter(hd -> hd != null)
                .toList();

        List<HoaDon> filtered = hoaDons.stream()
                .filter(hd -> {
                    if (tuNgay == null || denNgay == null || hd.getNgayLap() == null) {
                        return true;
                    }
                    LocalDate d = hd.getNgayLap().toLocalDate();
                    return !d.isBefore(tuNgay) && !d.isAfter(denNgay);
                })
                .filter(hd -> trangThai == null
                        || trangThai.isBlank()
                        || hd.getTrangThaiHoaDon().equalsIgnoreCase(trangThai))
                .filter(hd -> {
                    if (q == null || q.isBlank()) {
                        return true;
                    }
                    String k = q.toLowerCase(Locale.ROOT);
                    String soHoaDon = hd.getSoHoaDon() != null ? hd.getSoHoaDon().toLowerCase(Locale.ROOT) : "";
                    String donHangId = hd.getDonHang() != null && hd.getDonHang().getId() != null
                            ? hd.getDonHang().getId().toString().toLowerCase(Locale.ROOT)
                            : "";
                    return soHoaDon.contains(k) || donHangId.contains(k);
                })
                .sorted(Comparator.comparing(HoaDon::getNgayLap, Comparator.nullsLast(LocalDateTime::compareTo))
                        .reversed())
                .toList();

        return ResponseEntity.ok(filtered.stream().map(payloads::toHoaDonPayload).toList());
    }

    @GetMapping("/hoa-don/{hoaDonId}")
    public ResponseEntity<Map<String, Object>> layChiTietHoaDon(@PathVariable UUID hoaDonId) {
        List<HoaDon> allHD = donHangRepository.findAll().stream()
                .map(DonHang::getHoaDon)
                .filter(hd -> hd != null && hd.getId().equals(hoaDonId))
                .toList();
        if (allHD.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        HoaDon hoaDon = allHD.get(0);
        return ResponseEntity.ok(payloads.toHoaDonDetailPayload(hoaDon));
    }
}

package TKPMHDT.Controller.api.quantrivien;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import TKPMHDT.Entity.donhang.ChiTietDonHang;
import TKPMHDT.Entity.donhang.DonHang;
import TKPMHDT.Repository.donhang.DonHangRepository;
import TKPMHDT.Service.nguoidung.NguoiDungService;
import TKPMHDT.Service.sanpham.SanPhamService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAnyAuthority('product:manage','order:manage-all','inventory:manage','customer:manage','promotion:manage','report:view','staff:manage')")
@RequiredArgsConstructor
public class AdminBaoCaoController {

    private final DonHangRepository donHangRepository;
    private final NguoiDungService nguoiDungService;
    private final SanPhamService sanPhamService;

    @GetMapping("/bao-cao/tong-quan")
    public ResponseEntity<Map<String, Object>> baoCaoTongQuan(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay) {
        LocalDate fromDate = tuNgay != null ? tuNgay : LocalDate.now().minusDays(30);
        LocalDate toDate = denNgay != null ? denNgay : LocalDate.now();
        LocalDateTime from = LocalDateTime.of(fromDate, LocalTime.MIN);
        LocalDateTime to = LocalDateTime.of(toDate, LocalTime.MAX);
        List<DonHang> donHangs = donHangRepository.findByNgayDatBetween(from, to);
        BigDecimal doanhThu = donHangs.stream()
                .map(DonHang::getTongTien)
                .filter(v -> v != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<String, Integer> sanPhamBanChay = new HashMap<>();
        for (DonHang donHang : donHangs) {
            if (donHang.getChiTietDonHangs() == null) {
                continue;
            }
            for (ChiTietDonHang chiTiet : donHang.getChiTietDonHangs()) {
                String key = chiTiet.getNuocUong() != null ? chiTiet.getNuocUong().getTen() : "Khac";
                int old = sanPhamBanChay.getOrDefault(key, 0);
                int qty = chiTiet.getSoLuong() != null ? chiTiet.getSoLuong() : 0;
                sanPhamBanChay.put(key, old + qty);
            }
        }
        List<Map<String, Object>> topProducts = sanPhamBanChay.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .map(e -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("ten", e.getKey());
                    item.put("soLuong", e.getValue());
                    return item;
                })
                .toList();
        Map<String, Object> payload = new HashMap<>();
        payload.put("tuNgay", fromDate);
        payload.put("denNgay", toDate);
        payload.put("tongDoanhThu", doanhThu);
        payload.put("soLuongDonHang", donHangs.size());
        payload.put("topSanPham", topProducts);
        payload.put("soLuongKhachHang", nguoiDungService.danhSachKhachHang().size());
        payload.put("soLuongSanPham", sanPhamService.layDanhSachNuocUong().size());
        payload.put("canhBaoTonKho", sanPhamService.layNguyenLieuCanhBao().size());
        return ResponseEntity.ok(payload);
    }

    @GetMapping("/bao-cao/doanh-thu")
    public ResponseEntity<List<Map<String, Object>>> baoCaoDoanhThu(
            @RequestParam(defaultValue = "ngay") String kieu,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay) {
        LocalDate fromDate = tuNgay != null ? tuNgay : LocalDate.now().minusDays(30);
        LocalDate toDate = denNgay != null ? denNgay : LocalDate.now();
        List<DonHang> donHangs = donHangRepository.findByNgayDatBetween(
                LocalDateTime.of(fromDate, LocalTime.MIN),
                LocalDateTime.of(toDate, LocalTime.MAX));
        Map<String, BigDecimal> grouped = new java.util.LinkedHashMap<>();
        for (DonHang donHang : donHangs) {
            if (donHang.getNgayDat() == null) {
                continue;
            }
            String key;
            if ("nam".equalsIgnoreCase(kieu)) {
                key = String.valueOf(donHang.getNgayDat().getYear());
            } else if ("thang".equalsIgnoreCase(kieu)) {
                YearMonth ym = YearMonth.from(donHang.getNgayDat());
                key = ym.toString();
            } else {
                key = donHang.getNgayDat().toLocalDate().toString();
            }
            BigDecimal old = grouped.getOrDefault(key, BigDecimal.ZERO);
            BigDecimal amount = donHang.getTongTien() == null ? BigDecimal.ZERO : donHang.getTongTien();
            grouped.put(key, old.add(amount));
        }
        List<Map<String, Object>> chart = grouped.entrySet().stream()
                .map(e -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("nhan", e.getKey());
                    item.put("giaTri", e.getValue().setScale(2, RoundingMode.HALF_UP));
                    return item;
                })
                .toList();
        return ResponseEntity.ok(chart);
    }
}

package TKPMHDT.Controller.api.quantrivien;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import TKPMHDT.Entity.donhang.DonHang;
import TKPMHDT.Entity.nguoidung.DiaChi;
import TKPMHDT.Entity.nguoidung.KhachHang;
import TKPMHDT.Entity.nguoidung.NguoiDung;
import TKPMHDT.Entity.nguoidung.enums.VaiTro;
import TKPMHDT.Repository.donhang.DonHangRepository;
import TKPMHDT.Service.nguoidung.NguoiDungService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAnyAuthority('product:manage','order:manage-all','inventory:manage','customer:manage','promotion:manage','report:view','staff:manage')")
@RequiredArgsConstructor
public class AdminKhachHangController {

    private final NguoiDungService nguoiDungService;
    private final DonHangRepository donHangRepository;
    private final AdminAuditSupport audit;
    private final AdminPayloadMapper mapper;

    @GetMapping("/khach-hang")
    public ResponseEntity<List<Map<String, Object>>> layDanhSachKhachHang(@RequestParam(required = false) String q) {
        List<Map<String, Object>> data = nguoiDungService.danhSachKhachHang().stream()
                .filter(u -> q == null || q.isBlank() || mapper.checkUserMatch(u, q))
                .map(mapper::toUserPayload)
                .toList();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/khach-hang/{khachHangId}/lich-su")
    public ResponseEntity<List<DonHang>> lichSuMuaHang(@PathVariable UUID khachHangId) {
        return ResponseEntity.ok(donHangRepository.findByKhachHangId(khachHangId));
    }

    @PostMapping("/khach-hang")
    public ResponseEntity<Map<String, Object>> taoKhachHang(@RequestBody TaoKhachHangRequest request) {
        if (request.tenDangNhap() == null || request.tenDangNhap().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "tenDangNhap là bắt buộc"));
        }
        if (request.email() == null || request.email().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "email là bắt buộc"));
        }
        try {
            String tempPassword = "TempPass@" + System.currentTimeMillis();
            KhachHang khachHang = KhachHang.builder()
                    .tenDangNhap(request.tenDangNhap())
                    .email(request.email())
                    .matKhauHash(tempPassword)
                    .hoTen(request.hoTen() != null ? request.hoTen() : request.tenDangNhap())
                    .soDienThoai(request.soDienThoai())
                    .vaiTro(VaiTro.KHACH_HANG)
                    .trangThaiHoatDong(true)
                    .build();
            NguoiDung saved = nguoiDungService.luuNguoiDung(khachHang);
            audit.ghiLog("KHACH_HANG", "TAO", saved.getId().toString());
            return ResponseEntity.ok(mapper.toUserPayload(saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PutMapping("/khach-hang/{khachHangId}")
    public ResponseEntity<Map<String, Object>> capNhatKhachHang(
            @PathVariable UUID khachHangId,
            @RequestBody CapNhatNguoiDungRequest request) {
        return nguoiDungService.timTheoId(khachHangId).map(user -> {
            if (request.email() != null && !request.email().isBlank()) {
                user.setEmail(request.email().trim());
            }
            if (request.kichHoat() != null) {
                user.setTrangThaiHoatDong(request.kichHoat());
            }
            if (user instanceof KhachHang khachHang) {
                if (request.hoTen() != null) {
                    khachHang.setHoTen(request.hoTen());
                }
                if (request.soDienThoai() != null) {
                    khachHang.setSoDienThoai(request.soDienThoai());
                }
                user = khachHang;
            }
            NguoiDung saved = nguoiDungService.luuNguoiDung(user);
            audit.ghiLog("KHACH_HANG", "CAP_NHAT", saved.getId().toString());
            return ResponseEntity.ok(mapper.toUserPayload(saved));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/khach-hang/{khachHangId}/dia-chi")
    public ResponseEntity<List<Map<String, Object>>> layDiaChiKhachHang(@PathVariable UUID khachHangId) {
        return nguoiDungService.timTheoId(khachHangId).map(user -> {
            if (!(user instanceof KhachHang khachHang)) {
                return ResponseEntity.badRequest().<List<Map<String, Object>>>build();
            }
            List<Map<String, Object>> addresses = khachHang.getDanhSachDiaChi() == null ? List.of()
                    : khachHang.getDanhSachDiaChi().stream().map(mapper::toDiaChiPayload).toList();
            return ResponseEntity.ok(addresses);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/khach-hang/{khachHangId}/dia-chi")
    public ResponseEntity<Map<String, Object>> taoDiaChiKhachHang(
            @PathVariable UUID khachHangId,
            @RequestBody TaoDiaChiRequest request) {
        return nguoiDungService.timTheoId(khachHangId).map(user -> {
            if (!(user instanceof KhachHang khachHang)) {
                return ResponseEntity.badRequest().<Map<String, Object>>build();
            }
            DiaChi diaChi = DiaChi.builder()
                    .khachHang(khachHang)
                    .tenNguoiNhan(request.tenNguoiNhan() != null ? request.tenNguoiNhan() : khachHang.getHoTen())
                    .soDienThoai(request.soDienThoai() != null ? request.soDienThoai() : khachHang.getSoDienThoai())
                    .diaChiCuThe(request.diaChiCuThe())
                    .phuongXa(request.phuongXa())
                    .quanHuyen(request.quanHuyen())
                    .tinhThanhPho(request.tinhThanhPho())
                    .laMacDinh(request.laMacDinh() != null && request.laMacDinh())
                    .build();
            if (khachHang.getDanhSachDiaChi() == null) {
                khachHang.setDanhSachDiaChi(new ArrayList<>());
            }
            khachHang.getDanhSachDiaChi().add(diaChi);
            nguoiDungService.luuNguoiDung(khachHang);
            audit.ghiLog("DIA_CHI", "TAO", khachHangId + ":" + request.tenNguoiNhan());
            return ResponseEntity.ok(mapper.toDiaChiPayload(diaChi));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/khach-hang/{khachHangId}/dia-chi/{diaChiId}")
    public ResponseEntity<Map<String, Object>> capNhatDiaChiKhachHang(
            @PathVariable UUID khachHangId,
            @PathVariable UUID diaChiId,
            @RequestBody CapNhatDiaChiRequest request) {
        return nguoiDungService.timTheoId(khachHangId).map(user -> {
            if (!(user instanceof KhachHang khachHang)) {
                return ResponseEntity.badRequest().<Map<String, Object>>build();
            }
            DiaChi diaChi = khachHang.getDanhSachDiaChi() == null ? null
                    : khachHang.getDanhSachDiaChi().stream()
                    .filter(d -> d.getId().equals(diaChiId))
                    .findFirst()
                    .orElse(null);
            if (diaChi == null) {
                return ResponseEntity.notFound().<Map<String, Object>>build();
            }
            if (request.tenNguoiNhan() != null) {
                diaChi.setTenNguoiNhan(request.tenNguoiNhan());
            }
            if (request.soDienThoai() != null) {
                diaChi.setSoDienThoai(request.soDienThoai());
            }
            if (request.diaChiCuThe() != null) {
                diaChi.setDiaChiCuThe(request.diaChiCuThe());
            }
            if (request.phuongXa() != null) {
                diaChi.setPhuongXa(request.phuongXa());
            }
            if (request.quanHuyen() != null) {
                diaChi.setQuanHuyen(request.quanHuyen());
            }
            if (request.tinhThanhPho() != null) {
                diaChi.setTinhThanhPho(request.tinhThanhPho());
            }
            if (request.laMacDinh() != null) {
                diaChi.setLaMacDinh(request.laMacDinh());
            }
            nguoiDungService.luuNguoiDung(khachHang);
            audit.ghiLog("DIA_CHI", "CAP_NHAT", khachHangId + ":" + diaChiId);
            return ResponseEntity.ok(mapper.toDiaChiPayload(diaChi));
        }).orElseGet(() -> ResponseEntity.notFound().<Map<String, Object>>build());
    }

    @DeleteMapping("/khach-hang/{khachHangId}/dia-chi/{diaChiId}")
    public ResponseEntity<Map<String, Object>> xoaDiaChiKhachHang(
            @PathVariable UUID khachHangId,
            @PathVariable UUID diaChiId) {
        return nguoiDungService.timTheoId(khachHangId).map(user -> {
            if (!(user instanceof KhachHang khachHang)) {
                return ResponseEntity.badRequest().<Map<String, Object>>build();
            }
            if (khachHang.getDanhSachDiaChi() != null) {
                khachHang.getDanhSachDiaChi().removeIf(d -> d.getId().equals(diaChiId));
                nguoiDungService.luuNguoiDung(khachHang);
                audit.ghiLog("DIA_CHI", "XOA", khachHangId + ":" + diaChiId);
            }
            return ResponseEntity.ok(Map.<String, Object>of("success", true));
        }).orElseGet(() -> ResponseEntity.notFound().<Map<String, Object>>build());
    }
}

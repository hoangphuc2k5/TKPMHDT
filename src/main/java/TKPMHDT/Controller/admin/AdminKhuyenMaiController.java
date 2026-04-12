package TKPMHDT.Controller.admin;

import TKPMHDT.Entity.khuyenmai.MaGiamGia;
import TKPMHDT.Entity.khuyenmai.enums.LoaiGiamGiaEnum;
import TKPMHDT.Repository.khuyenmai.MaGiamGiaRepository;
import TKPMHDT.Repository.sanpham.NuocUongSanRepository;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize(
        "hasAnyAuthority('product:manage','order:manage-all','inventory:manage','customer:manage','promotion:manage','report:view','staff:manage')")
public class AdminKhuyenMaiController {

    private final MaGiamGiaRepository maGiamGiaRepository;
    private final NuocUongSanRepository nuocUongSanRepository;
    private final AdminPayloadHelper payloads;
    private final AdminAuditHelper audit;

    public AdminKhuyenMaiController(
            MaGiamGiaRepository maGiamGiaRepository,
            NuocUongSanRepository nuocUongSanRepository,
            AdminPayloadHelper payloads,
            AdminAuditHelper audit) {
        this.maGiamGiaRepository = maGiamGiaRepository;
        this.nuocUongSanRepository = nuocUongSanRepository;
        this.payloads = payloads;
        this.audit = audit;
    }

    @GetMapping("/khuyen-mai")
    public ResponseEntity<List<Map<String, Object>>> layDanhSachKhuyenMai() {
        return ResponseEntity.ok(
                maGiamGiaRepository.findAll().stream().map(payloads::toKhuyenMaiPayload).toList());
    }

    @PostMapping("/khuyen-mai")
    public ResponseEntity<Map<String, Object>> taoKhuyenMai(@RequestBody AdminApiDtos.KhuyenMaiRequest request) {
        MaGiamGia entity = MaGiamGia.builder()
                .ma(request.maGiamGia())
                .loaiGiam(request.loaiGiam() == null ? LoaiGiamGiaEnum.PHAN_TRAM : request.loaiGiam())
                .giaTri(request.giaTri() == null ? BigDecimal.ZERO : request.giaTri())
                .ngayBatDau(request.ngayBatDau())
                .ngayKetThuc(request.ngayKetThuc())
                .kichHoat(request.kichHoat() == null || request.kichHoat())
                .apDungToanHeThong(request.apDungToanHeThong() != null && request.apDungToanHeThong())
                .build();
        if (request.sanPhamIds() != null) {
            entity.setSanPhamApDung(new LinkedHashSet<>(nuocUongSanRepository.findAllById(request.sanPhamIds())));
        }
        MaGiamGia saved = maGiamGiaRepository.save(entity);
        audit.ghiLog("KHUYEN_MAI", "TAO", saved.getMa());
        return ResponseEntity.ok(payloads.toKhuyenMaiPayload(saved));
    }

    @PutMapping("/khuyen-mai/{khuyenMaiId}")
    public ResponseEntity<Map<String, Object>> capNhatKhuyenMai(
            @PathVariable UUID khuyenMaiId, @RequestBody AdminApiDtos.KhuyenMaiRequest request) {
        return maGiamGiaRepository
                .findById(khuyenMaiId)
                .map(entity -> {
                    if (request.maGiamGia() != null) {
                        entity.setMa(request.maGiamGia());
                    }
                    if (request.loaiGiam() != null) {
                        entity.setLoaiGiam(request.loaiGiam());
                    }
                    if (request.giaTri() != null) {
                        entity.setGiaTri(request.giaTri());
                    }
                    if (request.ngayBatDau() != null) {
                        entity.setNgayBatDau(request.ngayBatDau());
                    }
                    if (request.ngayKetThuc() != null) {
                        entity.setNgayKetThuc(request.ngayKetThuc());
                    }
                    if (request.kichHoat() != null) {
                        entity.setKichHoat(request.kichHoat());
                    }
                    if (request.apDungToanHeThong() != null) {
                        entity.setApDungToanHeThong(request.apDungToanHeThong());
                    }
                    if (request.sanPhamIds() != null) {
                        entity.setSanPhamApDung(
                                new LinkedHashSet<>(nuocUongSanRepository.findAllById(request.sanPhamIds())));
                    }
                    MaGiamGia updated = maGiamGiaRepository.save(entity);
                    audit.ghiLog("KHUYEN_MAI", "CAP_NHAT", updated.getMa());
                    return ResponseEntity.ok(payloads.toKhuyenMaiPayload(updated));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/khuyen-mai/{khuyenMaiId}")
    public ResponseEntity<Map<String, Object>> xoaKhuyenMai(@PathVariable UUID khuyenMaiId) {
        maGiamGiaRepository.deleteById(khuyenMaiId);
        audit.ghiLog("KHUYEN_MAI", "XOA", khuyenMaiId.toString());
        return ResponseEntity.ok(Map.of("success", true));
    }
}

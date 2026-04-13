package TKPMHDT.Controller.api.quantrivien;

import java.math.BigDecimal;
import java.util.ArrayList;
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

import TKPMHDT.Entity.khuyenmai.KhuyenMaiGiaSanPham;
import TKPMHDT.Entity.khuyenmai.MaGiamGia;
import TKPMHDT.Entity.khuyenmai.enums.LoaiGiamGiaEnum;
import TKPMHDT.Repository.khuyenmai.MaGiamGiaRepository;
import TKPMHDT.Repository.sanpham.NuocUongSanRepository;
import TKPMHDT.Service.khuyenmai.KhuyenMaiGiaSanPhamService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAnyAuthority('product:manage','order:manage-all','inventory:manage','customer:manage','promotion:manage','report:view','staff:manage')")
@RequiredArgsConstructor
public class AdminKhuyenMaiController {

    private final MaGiamGiaRepository maGiamGiaRepository;
    private final NuocUongSanRepository nuocUongSanRepository;
    private final KhuyenMaiGiaSanPhamService khuyenMaiGiaSanPhamService;
    private final AdminAuditSupport audit;
    private final AdminPayloadMapper mapper;

    @GetMapping("/khuyen-mai")
    public ResponseEntity<List<Map<String, Object>>> layDanhSachKhuyenMai() {
        return ResponseEntity.ok(maGiamGiaRepository.findAll().stream().map(mapper::toKhuyenMaiPayload).toList());
    }

    @PostMapping("/khuyen-mai")
    public ResponseEntity<Map<String, Object>> taoKhuyenMai(@RequestBody KhuyenMaiRequest request) {
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
        if (request.cacNgayApDung() != null) {
            entity.setCacNgayApDung(new ArrayList<>(request.cacNgayApDung()));
        }
        if (request.danhMucApDung() != null) {
            entity.setDanhMucApDung(AdminPayloadMapper.chuanHoaDanhMuc(request.danhMucApDung()));
        }
        MaGiamGia saved = maGiamGiaRepository.save(entity);
        audit.ghiLog("KHUYEN_MAI", "TAO", saved.getMa());
        return ResponseEntity.ok(mapper.toKhuyenMaiPayload(saved));
    }

    @PutMapping("/khuyen-mai/{khuyenMaiId}")
    public ResponseEntity<Map<String, Object>> capNhatKhuyenMai(
            @PathVariable UUID khuyenMaiId,
            @RequestBody KhuyenMaiRequest request) {
        return maGiamGiaRepository.findById(khuyenMaiId).map(entity -> {
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
                entity.setSanPhamApDung(new LinkedHashSet<>(nuocUongSanRepository.findAllById(request.sanPhamIds())));
            }
            if (request.cacNgayApDung() != null) {
                entity.setCacNgayApDung(new ArrayList<>(request.cacNgayApDung()));
            }
            if (request.danhMucApDung() != null) {
                entity.setDanhMucApDung(AdminPayloadMapper.chuanHoaDanhMuc(request.danhMucApDung()));
            }
            MaGiamGia updated = maGiamGiaRepository.save(entity);
            audit.ghiLog("KHUYEN_MAI", "CAP_NHAT", updated.getMa());
            return ResponseEntity.ok(mapper.toKhuyenMaiPayload(updated));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/khuyen-mai/{khuyenMaiId}")
    public ResponseEntity<Map<String, Object>> xoaKhuyenMai(@PathVariable UUID khuyenMaiId) {
        maGiamGiaRepository.deleteById(khuyenMaiId);
        audit.ghiLog("KHUYEN_MAI", "XOA", khuyenMaiId.toString());
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/khuyen-mai-gia-san-pham")
    public ResponseEntity<List<Map<String, Object>>> layDanhSachKhuyenMaiGiaSanPham() {
        return ResponseEntity.ok(
                khuyenMaiGiaSanPhamService.tatCaDayDu().stream().map(mapper::toKhuyenMaiGiaPayload).toList());
    }

    @PostMapping("/khuyen-mai-gia-san-pham")
    public ResponseEntity<Map<String, Object>> taoKhuyenMaiGiaSanPham(@RequestBody KhuyenMaiGiaSanPhamRequest request) {
        KhuyenMaiGiaSanPham entity = KhuyenMaiGiaSanPham.builder()
                .ten(request.ten())
                .phamVi(request.phamVi())
                .loaiGiam(request.loaiGiam() == null ? LoaiGiamGiaEnum.PHAN_TRAM : request.loaiGiam())
                .giaTri(request.giaTri())
                .thoiGianBatDau(request.thoiGianBatDau())
                .thoiGianKetThuc(request.thoiGianKetThuc())
                .kichHoat(request.kichHoat() == null || request.kichHoat())
                .build();
        mapper.ganPhamViKhuyenMaiGia(entity, request);
        KhuyenMaiGiaSanPham saved = khuyenMaiGiaSanPhamService.luu(entity);
        audit.ghiLog("KHUYEN_MAI_GIA", "TAO", saved.getId().toString());
        return ResponseEntity.ok(mapper.toKhuyenMaiGiaPayload(saved));
    }

    @PutMapping("/khuyen-mai-gia-san-pham/{id}")
    public ResponseEntity<Map<String, Object>> capNhatKhuyenMaiGiaSanPham(
            @PathVariable UUID id,
            @RequestBody KhuyenMaiGiaSanPhamRequest request) {
        return khuyenMaiGiaSanPhamService
                .timTheoIdDayDu(id)
                .map(entity -> {
                    if (request.ten() != null) {
                        entity.setTen(request.ten());
                    }
                    if (request.phamVi() != null) {
                        entity.setPhamVi(request.phamVi());
                    }
                    if (request.loaiGiam() != null) {
                        entity.setLoaiGiam(request.loaiGiam());
                    }
                    if (request.giaTri() != null) {
                        entity.setGiaTri(request.giaTri());
                    }
                    if (request.thoiGianBatDau() != null) {
                        entity.setThoiGianBatDau(request.thoiGianBatDau());
                    }
                    if (request.thoiGianKetThuc() != null) {
                        entity.setThoiGianKetThuc(request.thoiGianKetThuc());
                    }
                    if (request.kichHoat() != null) {
                        entity.setKichHoat(request.kichHoat());
                    }
                    mapper.ganPhamViKhuyenMaiGia(entity, request);
                    KhuyenMaiGiaSanPham updated = khuyenMaiGiaSanPhamService.luu(entity);
                    audit.ghiLog("KHUYEN_MAI_GIA", "CAP_NHAT", updated.getId().toString());
                    return ResponseEntity.ok(mapper.toKhuyenMaiGiaPayload(updated));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/khuyen-mai-gia-san-pham/{id}")
    public ResponseEntity<Map<String, Object>> xoaKhuyenMaiGiaSanPham(@PathVariable UUID id) {
        khuyenMaiGiaSanPhamService.xoa(id);
        audit.ghiLog("KHUYEN_MAI_GIA", "XOA", id.toString());
        return ResponseEntity.ok(Map.of("success", true));
    }
}

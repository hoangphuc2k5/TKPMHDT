package TKPMHDT.Controller.admin;

import TKPMHDT.Entity.sanpham.CongThuc;
import TKPMHDT.Entity.sanpham.LuongNguyenLieu;
import TKPMHDT.Entity.sanpham.NguyenLieu;
import TKPMHDT.Entity.sanpham.NuocUongSan;
import TKPMHDT.Entity.sanpham.enums.LoaiNguyenLieu;
import TKPMHDT.Repository.sanpham.LuongNguyenLieuRepository;
import TKPMHDT.Repository.sanpham.TuyChonTuyChinhRepository;
import TKPMHDT.Service.FileStorageService;
import TKPMHDT.Service.sanpham.SanPhamService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize(
        "hasAnyAuthority('product:manage','order:manage-all','inventory:manage','customer:manage','promotion:manage','report:view','staff:manage')")
public class AdminSanPhamController {

    private final SanPhamService sanPhamService;
    private final TuyChonTuyChinhRepository tuyChonTuyChinhRepository;
    private final LuongNguyenLieuRepository luongNguyenLieuRepository;
    private final FileStorageService fileStorageService;
    private final AdminAuditHelper audit;
    private final AdminSanPhamCongThucHelper congThucHelper;

    public AdminSanPhamController(
            SanPhamService sanPhamService,
            TuyChonTuyChinhRepository tuyChonTuyChinhRepository,
            LuongNguyenLieuRepository luongNguyenLieuRepository,
            FileStorageService fileStorageService,
            AdminAuditHelper audit,
            AdminSanPhamCongThucHelper congThucHelper) {
        this.sanPhamService = sanPhamService;
        this.tuyChonTuyChinhRepository = tuyChonTuyChinhRepository;
        this.luongNguyenLieuRepository = luongNguyenLieuRepository;
        this.fileStorageService = fileStorageService;
        this.audit = audit;
        this.congThucHelper = congThucHelper;
    }

    @GetMapping("/san-pham")
    public ResponseEntity<List<NuocUongSan>> layDanhSachSanPham(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String danhMuc,
            @RequestParam(required = false) Boolean dangBan) {
        List<NuocUongSan> sanPhams = sanPhamService.layDanhSachNuocUong();
        List<NuocUongSan> filtered = sanPhams.stream()
                .filter(s -> q == null
                        || q.isBlank()
                        || (s.getTen() != null
                                && s.getTen().toLowerCase(Locale.ROOT).contains(q.toLowerCase(Locale.ROOT))))
                .filter(s -> danhMuc == null
                        || danhMuc.isBlank()
                        || (s.getDanhMuc() != null && s.getDanhMuc().equalsIgnoreCase(danhMuc)))
                .filter(s -> dangBan == null || s.isDangKinhDoanh() == dangBan)
                .sorted(Comparator.comparing(NuocUongSan::getTen, Comparator.nullsLast(String::compareToIgnoreCase)))
                .toList();
        return ResponseEntity.ok(filtered);
    }

    @GetMapping("/san-pham/danh-muc")
    public ResponseEntity<List<String>> layDanhMucSanPham() {
        Set<String> categories = sanPhamService.layDanhSachNuocUong().stream()
                .map(NuocUongSan::getDanhMuc)
                .filter(c -> c != null && !c.isBlank())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        return ResponseEntity.ok(new ArrayList<>(categories));
    }

    @PostMapping("/san-pham")
    public ResponseEntity<NuocUongSan> taoSanPham(@RequestBody AdminApiDtos.SanPhamRequest request) {
        NuocUongSan sanPham = new NuocUongSan();
        sanPham.setTen(request.ten());
        sanPham.setGia(request.gia() != null ? request.gia() : BigDecimal.ZERO);
        sanPham.setMoTa(request.moTa());
        sanPham.setDanhMuc(request.danhMuc());
        sanPham.setDangKinhDoanh(request.dangBan() == null || request.dangBan());
        sanPham.setCongThucCoBan(
                congThucHelper.taoCongThucRiengChoSanPham(request.congThucId(), request.ten(), sanPham.getGia()));
        congThucHelper.apDungCauHinhTuyChinhMacDinh(sanPham);
        if (request.hinhAnh() != null) {
            sanPham.setHinhAnh(new ArrayList<>(request.hinhAnh()));
        }
        NuocUongSan saved = sanPhamService.luuNuocUong(sanPham);
        audit.ghiLog("SAN_PHAM", "TAO", saved.getTen());
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/san-pham/{sanPhamId}")
    public ResponseEntity<NuocUongSan> capNhatSanPham(
            @PathVariable UUID sanPhamId, @RequestBody AdminApiDtos.SanPhamRequest request) {
        return sanPhamService.layNuocUongTheoId(sanPhamId)
                .map(sp -> {
                    if (request.ten() != null) {
                        sp.setTen(request.ten());
                    }
                    if (request.gia() != null) {
                        sp.setGia(request.gia());
                    }
                    if (request.moTa() != null) {
                        sp.setMoTa(request.moTa());
                    }
                    if (request.danhMuc() != null) {
                        sp.setDanhMuc(request.danhMuc());
                    }
                    if (request.dangBan() != null) {
                        sp.setDangKinhDoanh(request.dangBan());
                    }
                    if (request.hinhAnh() != null) {
                        sp.setHinhAnh(new ArrayList<>(request.hinhAnh()));
                    }
                    if (request.congThucId() != null) {
                        sp.setCongThucCoBan(congThucHelper.taoCongThucRiengChoSanPham(
                                request.congThucId(), sp.getTen(), sp.getGia()));
                    } else if (sp.getCongThucCoBan() == null) {
                        sp.setCongThucCoBan(
                                congThucHelper.taoCongThucRiengChoSanPham(null, sp.getTen(), sp.getGia()));
                    }
                    congThucHelper.damBaoCongThucRieng(sp, false);
                    NuocUongSan updated = sanPhamService.luuNuocUong(sp);
                    audit.ghiLog("SAN_PHAM", "CAP_NHAT", updated.getTen());
                    return ResponseEntity.ok(updated);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/san-pham/{sanPhamId}/cong-thuc")
    public ResponseEntity<Map<String, Object>> layCongThucSanPham(@PathVariable UUID sanPhamId) {
        return sanPhamService.layNuocUongTheoId(sanPhamId)
                .map(sp -> {
                    CongThuc congThuc = sp.getCongThucCoBan();
                    List<Map<String, Object>> nguyenLieuCongThuc =
                            congThuc != null && congThuc.getLuongNguyenLieus() != null
                                    ? congThuc.getLuongNguyenLieus().stream()
                                            .map(congThucHelper::toCongThucIngredientPayload)
                                            .toList()
                                    : List.of();
                    Map<String, Object> payload = new HashMap<>();
                    payload.put("sanPhamId", sp.getId());
                    payload.put("congThucId", congThuc != null ? congThuc.getId() : null);
                    payload.put("congThucTen", congThuc != null ? congThuc.getTen() : "Công thức mặc định");
                    payload.put("nguyenLieu", nguyenLieuCongThuc);
                    payload.put("danhSachNguyenLieuCoSan", sanPhamService.layDanhSachNguyenLieu());
                    payload.put(
                            "toppingCoSan",
                            tuyChonTuyChinhRepository.findAll().stream()
                                    .filter(opt -> "TOPPING".equalsIgnoreCase(opt.getNhom()))
                                    .toList());
                    payload.put(
                            "mucDuongTuyChon",
                            congThucHelper.parseCsv(
                                    sp.getMucDuongTuyChon(),
                                    List.of("Không đường", "Ít đường", "Bình thường", "Nhiều đường")));
                    payload.put(
                            "mucDuongMacDinh",
                            sp.getMucDuongMacDinh() != null ? sp.getMucDuongMacDinh() : "Bình thường");
                    payload.put(
                            "mucDaTuyChon",
                            congThucHelper.parseCsv(sp.getMucDaTuyChon(), List.of("Không đá", "Ít đá", "Bình thường", "Nhiều đá")));
                    payload.put("mucDaMacDinh", sp.getMucDaMacDinh() != null ? sp.getMucDaMacDinh() : "Bình thường");
                    payload.put(
                            "kichCoTuyChon",
                            congThucHelper.parseCsv(sp.getKichCoTuyChon(), List.of("Nhỏ", "Vừa", "Lớn")));
                    payload.put("kichCoMacDinh", sp.getKichCoMacDinh() != null ? sp.getKichCoMacDinh() : "Vừa");
                    payload.put("coApDungSize", Boolean.TRUE.equals(sp.getCoApDungSize()));
                    payload.put("toppingChoPhep", congThucHelper.parseUuidCsv(sp.getToppingChoPhep()));
                    return ResponseEntity.ok(payload);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/san-pham/{sanPhamId}/cong-thuc/nguyen-lieu")
    public ResponseEntity<Map<String, Object>> themNguyenLieuCongThuc(
            @PathVariable UUID sanPhamId, @RequestBody AdminApiDtos.CongThucNguyenLieuRequest request) {
        return sanPhamService.layNuocUongTheoId(sanPhamId)
                .map(sp -> {
                    CongThuc congThuc = congThucHelper.damBaoCongThucRieng(sp, true);
                    if (congThuc == null) {
                        throw new IllegalArgumentException("Sản phẩm chưa có công thức mặc định");
                    }
                    NguyenLieu nguyenLieu = sanPhamService
                            .layNguyenLieuTheoId(request.nguyenLieuId())
                            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nguyên liệu"));
                    LuongNguyenLieu entity = LuongNguyenLieu.builder()
                            .congThuc(congThuc)
                            .nguyenLieu(nguyenLieu)
                            .soLuong(congThucHelper.soLuongMacDinhChoCongThuc(nguyenLieu, request.soLuong()))
                            .donVi(congThucHelper.donViMacDinhChoCongThuc(nguyenLieu, request.donVi()))
                            .build();
                    LuongNguyenLieu saved = luongNguyenLieuRepository.save(entity);
                    audit.ghiLog("CONG_THUC", "THEM_NGUYEN_LIEU", sp.getTen() + ":" + nguyenLieu.getTen());
                    return ResponseEntity.ok(congThucHelper.toCongThucIngredientPayload(saved));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/san-pham/{sanPhamId}/cong-thuc/nguyen-lieu/{luongId}")
    public ResponseEntity<Map<String, Object>> capNhatNguyenLieuCongThuc(
            @PathVariable UUID sanPhamId,
            @PathVariable UUID luongId,
            @RequestBody AdminApiDtos.CongThucNguyenLieuRequest request) {
        return sanPhamService.layNuocUongTheoId(sanPhamId)
                .map(sp -> {
                    CongThuc congThuc = congThucHelper.damBaoCongThucRieng(sp, true);
                    return luongNguyenLieuRepository
                            .findById(luongId)
                            .map(entity -> {
                                if (congThuc == null
                                        || entity.getCongThuc() == null
                                        || !congThuc.getId().equals(entity.getCongThuc().getId())) {
                                    throw new IllegalArgumentException("Nguyên liệu không thuộc công thức của sản phẩm");
                                }
                                if (request.nguyenLieuId() != null) {
                                    NguyenLieu nl = sanPhamService
                                            .layNguyenLieuTheoId(request.nguyenLieuId())
                                            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nguyên liệu"));
                                    entity.setNguyenLieu(nl);
                                }
                                NguyenLieu nl = entity.getNguyenLieu();
                                if (nl != null && nl.getLoaiNguyenLieu() == LoaiNguyenLieu.TOPPING) {
                                    entity.setSoLuong(BigDecimal.ONE);
                                    entity.setDonVi(congThucHelper.donViMacDinhChoCongThuc(nl, null));
                                } else {
                                    if (request.soLuong() != null) {
                                        entity.setSoLuong(request.soLuong());
                                    }
                                    if (request.donVi() != null && !request.donVi().isBlank()) {
                                        entity.setDonVi(request.donVi());
                                    }
                                }
                                LuongNguyenLieu updated = luongNguyenLieuRepository.save(entity);
                                audit.ghiLog("CONG_THUC", "CAP_NHAT_NGUYEN_LIEU", sp.getTen() + ":" + updated.getId());
                                return ResponseEntity.ok(congThucHelper.toCongThucIngredientPayload(updated));
                            })
                            .orElseGet(() -> ResponseEntity.notFound().build());
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/san-pham/{sanPhamId}/cong-thuc/nguyen-lieu/{luongId}")
    public ResponseEntity<Map<String, Object>> xoaNguyenLieuCongThuc(
            @PathVariable UUID sanPhamId, @PathVariable UUID luongId) {
        return sanPhamService.layNuocUongTheoId(sanPhamId)
                .map(sp -> {
                    CongThuc congThuc = congThucHelper.damBaoCongThucRieng(sp, true);
                    return luongNguyenLieuRepository
                            .findById(luongId)
                            .map(entity -> {
                                if (congThuc == null
                                        || entity.getCongThuc() == null
                                        || !congThuc.getId().equals(entity.getCongThuc().getId())) {
                                    throw new IllegalArgumentException("Nguyên liệu không thuộc công thức của sản phẩm");
                                }
                                luongNguyenLieuRepository.delete(entity);
                                audit.ghiLog("CONG_THUC", "XOA_NGUYEN_LIEU", sp.getTen() + ":" + luongId);
                                return ResponseEntity.ok(Map.<String, Object>of("success", true));
                            })
                            .orElseGet(() -> ResponseEntity.notFound().build());
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/san-pham/{sanPhamId}/cong-thuc/tuy-chinh")
    public ResponseEntity<Map<String, Object>> capNhatTuyChinhCongThuc(
            @PathVariable UUID sanPhamId, @RequestBody AdminApiDtos.TuyChinhCongThucRequest request) {
        return sanPhamService.layNuocUongTheoId(sanPhamId)
                .map(sp -> {
                    if (request.mucDuongTuyChon() != null) {
                        sp.setMucDuongTuyChon(congThucHelper.joinCsv(request.mucDuongTuyChon()));
                    }
                    if (request.mucDuongMacDinh() != null) {
                        sp.setMucDuongMacDinh(request.mucDuongMacDinh());
                    }
                    if (request.mucDaTuyChon() != null) {
                        sp.setMucDaTuyChon(congThucHelper.joinCsv(request.mucDaTuyChon()));
                    }
                    if (request.mucDaMacDinh() != null) {
                        sp.setMucDaMacDinh(request.mucDaMacDinh());
                    }
                    if (request.kichCoTuyChon() != null) {
                        sp.setKichCoTuyChon(congThucHelper.joinCsv(request.kichCoTuyChon()));
                    }
                    if (request.kichCoMacDinh() != null) {
                        sp.setKichCoMacDinh(request.kichCoMacDinh());
                    }
                    if (request.coApDungSize() != null) {
                        sp.setCoApDungSize(request.coApDungSize());
                    }
                    if (request.toppingChoPhep() != null) {
                        sp.setToppingChoPhep(congThucHelper.joinUuidCsv(request.toppingChoPhep()));
                    }
                    NuocUongSan updated = sanPhamService.luuNuocUong(sp);
                    audit.ghiLog("CONG_THUC", "CAP_NHAT_TUY_CHINH", updated.getTen());
                    Map<String, Object> payload = new HashMap<>();
                    payload.put("success", true);
                    payload.put("sanPhamId", updated.getId());
                    payload.put("mucDuongTuyChon", congThucHelper.parseCsv(updated.getMucDuongTuyChon(), List.of()));
                    payload.put("mucDuongMacDinh", updated.getMucDuongMacDinh());
                    payload.put("mucDaTuyChon", congThucHelper.parseCsv(updated.getMucDaTuyChon(), List.of()));
                    payload.put("mucDaMacDinh", updated.getMucDaMacDinh());
                    payload.put("kichCoTuyChon", congThucHelper.parseCsv(updated.getKichCoTuyChon(), List.of()));
                    payload.put("kichCoMacDinh", updated.getKichCoMacDinh());
                    payload.put("coApDungSize", Boolean.TRUE.equals(updated.getCoApDungSize()));
                    payload.put("toppingChoPhep", congThucHelper.parseUuidCsv(updated.getToppingChoPhep()));
                    return ResponseEntity.ok(payload);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/san-pham/{sanPhamId}")
    public ResponseEntity<Map<String, Object>> xoaSanPham(@PathVariable UUID sanPhamId) {
        sanPhamService.xoaNuocUong(sanPhamId);
        audit.ghiLog("SAN_PHAM", "XOA", sanPhamId.toString());
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PatchMapping("/san-pham/{sanPhamId}/trang-thai-ban")
    public ResponseEntity<NuocUongSan> batTatTrangThaiBan(
            @PathVariable UUID sanPhamId, @RequestBody AdminApiDtos.TrangThaiBanRequest request) {
        return sanPhamService.layNuocUongTheoId(sanPhamId)
                .map(sp -> {
                    sp.setDangKinhDoanh(request.dangBan());
                    NuocUongSan updated = sanPhamService.luuNuocUong(sp);
                    audit.ghiLog("SAN_PHAM", "TRANG_THAI_BAN", sp.getTen() + ":" + request.dangBan());
                    return ResponseEntity.ok(updated);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/san-pham/{sanPhamId}/hinh-anh", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NuocUongSan> capNhatHinhAnhSanPham(
            @PathVariable UUID sanPhamId, @RequestParam("hinhAnh") MultipartFile[] files) {
        return sanPhamService.layNuocUongTheoId(sanPhamId)
                .map(sp -> {
                    List<String> current =
                            sp.getHinhAnh() != null ? new ArrayList<>(sp.getHinhAnh()) : new ArrayList<>();
                    for (MultipartFile file : files) {
                        if (file != null && !file.isEmpty()) {
                            String imageUrl = fileStorageService.storeFile(file);
                            current.add(0, imageUrl);
                        }
                    }
                    sp.setHinhAnh(current);
                    NuocUongSan updated = sanPhamService.luuNuocUong(sp);
                    audit.ghiLog("SAN_PHAM", "CAP_NHAT_ANH", sp.getTen());
                    return ResponseEntity.ok(updated);
                })
                .orElseGet(() -> ResponseEntity.status(404).build());
    }

    @DeleteMapping("/san-pham/{sanPhamId}/hinh-anh")
    public ResponseEntity<NuocUongSan> xoaHinhAnhSanPham(
            @PathVariable UUID sanPhamId, @RequestParam int index) {

        Optional<NuocUongSan> optional = sanPhamService.layNuocUongTheoId(sanPhamId);

        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        NuocUongSan sp = optional.get();

        List<String> current =
                sp.getHinhAnh() != null ? new ArrayList<>(sp.getHinhAnh()) : new ArrayList<>();

        if (index < 0 || index >= current.size()) {
            return ResponseEntity.badRequest().build();
        }

        current.remove(index);
        sp.setHinhAnh(current);

        NuocUongSan updated = sanPhamService.luuNuocUong(sp);
        audit.ghiLog("SAN_PHAM", "XOA_ANH", sp.getTen());

        return ResponseEntity.ok(updated);
    }
}

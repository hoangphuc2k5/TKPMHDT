package TKPMHDT.Controller;

import TKPMHDT.Entity.donhang.ChiTietDonHang;
import TKPMHDT.Entity.donhang.DonHang;
import TKPMHDT.Entity.donhang.trangthai.TrangThaiChoXacNhan;
import TKPMHDT.Entity.donhang.trangthai.TrangThaiDaGiao;
import TKPMHDT.Entity.donhang.trangthai.TrangThaiDaHuy;
import TKPMHDT.Entity.donhang.trangthai.TrangThaiDaXacNhan;
import TKPMHDT.Entity.hethong.CauHinhHeThong;
import TKPMHDT.Entity.hethong.NhatKyHeThong;
import TKPMHDT.Entity.hethong.VaiTroQuyen;
import TKPMHDT.Entity.khuyenmai.MaGiamGia;
import TKPMHDT.Entity.khuyenmai.enums.LoaiGiamGiaEnum;
import TKPMHDT.Entity.nguoidung.KhachHang;
import TKPMHDT.Entity.nguoidung.NguoiDung;
import TKPMHDT.Entity.nguoidung.enums.VaiTro;
import TKPMHDT.Entity.sanpham.CongThuc;
import TKPMHDT.Entity.sanpham.LichSuKho;
import TKPMHDT.Entity.sanpham.LuongNguyenLieu;
import TKPMHDT.Entity.sanpham.NguyenLieu;
import TKPMHDT.Entity.sanpham.NuocUongSan;
import TKPMHDT.Entity.sanpham.TuyChonTuyChinh;
import TKPMHDT.Repository.donhang.DonHangRepository;
import TKPMHDT.Repository.hethong.CauHinhHeThongRepository;
import TKPMHDT.Repository.hethong.NhatKyHeThongRepository;
import TKPMHDT.Repository.hethong.VaiTroQuyenRepository;
import TKPMHDT.Repository.khuyenmai.MaGiamGiaRepository;
import TKPMHDT.Repository.sanpham.CongThucRepository;
import TKPMHDT.Repository.sanpham.LichSuKhoRepository;
import TKPMHDT.Repository.sanpham.LuongNguyenLieuRepository;
import TKPMHDT.Repository.sanpham.NuocUongSanRepository;
import TKPMHDT.Repository.sanpham.TuyChonTuyChinhRepository;
import TKPMHDT.Service.FileStorageService;
import TKPMHDT.Service.nguoidung.NguoiDungService;
import TKPMHDT.Service.sanpham.SanPhamService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
@PreAuthorize("hasAnyAuthority('product:manage','order:manage-all','inventory:manage','customer:manage','promotion:manage','report:view','staff:manage')")
public class AdminController {

    private final NguoiDungService nguoiDungService;
    private final SanPhamService sanPhamService;
    private final DonHangRepository donHangRepository;
    private final MaGiamGiaRepository maGiamGiaRepository;
    private final TuyChonTuyChinhRepository tuyChonTuyChinhRepository;
    private final LichSuKhoRepository lichSuKhoRepository;
    private final NhatKyHeThongRepository nhatKyHeThongRepository;
    private final CauHinhHeThongRepository cauHinhHeThongRepository;
    private final VaiTroQuyenRepository vaiTroQuyenRepository;
    private final CongThucRepository congThucRepository;
    private final LuongNguyenLieuRepository luongNguyenLieuRepository;
    private final NuocUongSanRepository nuocUongSanRepository;
    private final FileStorageService fileStorageService;

    public AdminController(
            NguoiDungService nguoiDungService,
            SanPhamService sanPhamService,
            DonHangRepository donHangRepository,
            MaGiamGiaRepository maGiamGiaRepository,
            TuyChonTuyChinhRepository tuyChonTuyChinhRepository,
            LichSuKhoRepository lichSuKhoRepository,
            NhatKyHeThongRepository nhatKyHeThongRepository,
            CauHinhHeThongRepository cauHinhHeThongRepository,
            VaiTroQuyenRepository vaiTroQuyenRepository,
            CongThucRepository congThucRepository,
            LuongNguyenLieuRepository luongNguyenLieuRepository,
            NuocUongSanRepository nuocUongSanRepository,
            FileStorageService fileStorageService) {
        this.nguoiDungService = nguoiDungService;
        this.sanPhamService = sanPhamService;
        this.donHangRepository = donHangRepository;
        this.maGiamGiaRepository = maGiamGiaRepository;
        this.tuyChonTuyChinhRepository = tuyChonTuyChinhRepository;
        this.lichSuKhoRepository = lichSuKhoRepository;
        this.nhatKyHeThongRepository = nhatKyHeThongRepository;
        this.cauHinhHeThongRepository = cauHinhHeThongRepository;
        this.vaiTroQuyenRepository = vaiTroQuyenRepository;
        this.congThucRepository = congThucRepository;
        this.luongNguyenLieuRepository = luongNguyenLieuRepository;
        this.nuocUongSanRepository = nuocUongSanRepository;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/san-pham")
    public ResponseEntity<List<NuocUongSan>> layDanhSachSanPham(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String danhMuc,
            @RequestParam(required = false) Boolean dangBan) {
        List<NuocUongSan> sanPhams = sanPhamService.layDanhSachNuocUong();
        List<NuocUongSan> filtered = sanPhams.stream()
                .filter(s -> q == null || q.isBlank() || (s.getTen() != null && s.getTen().toLowerCase(Locale.ROOT).contains(q.toLowerCase(Locale.ROOT))))
                .filter(s -> danhMuc == null || danhMuc.isBlank() || (s.getDanhMuc() != null && s.getDanhMuc().equalsIgnoreCase(danhMuc)))
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
    public ResponseEntity<NuocUongSan> taoSanPham(@RequestBody SanPhamRequest request) {
        NuocUongSan sanPham = new NuocUongSan();
        sanPham.setTen(request.ten());
        sanPham.setGia(request.gia() != null ? request.gia() : BigDecimal.ZERO);
        sanPham.setMoTa(request.moTa());
        sanPham.setDanhMuc(request.danhMuc());
        sanPham.setDangKinhDoanh(request.dangBan() == null || request.dangBan());
        sanPham.setCongThucCoBan(taoCongThucRiengChoSanPham(request.congThucId(), request.ten(), sanPham.getGia()));
        apDungCauHinhTuyChinhMacDinh(sanPham);
        if (request.hinhAnh() != null) {
            sanPham.setHinhAnh(new ArrayList<>(request.hinhAnh()));
        }
        NuocUongSan saved = sanPhamService.luuNuocUong(sanPham);
        ghiLog("SAN_PHAM", "TAO", saved.getTen());
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/san-pham/{sanPhamId}")
    public ResponseEntity<NuocUongSan> capNhatSanPham(@PathVariable UUID sanPhamId, @RequestBody SanPhamRequest request) {
        return sanPhamService.layNuocUongTheoId(sanPhamId).map(sp -> {
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
                sp.setCongThucCoBan(taoCongThucRiengChoSanPham(request.congThucId(), sp.getTen(), sp.getGia()));
            } else if (sp.getCongThucCoBan() == null) {
                sp.setCongThucCoBan(taoCongThucRiengChoSanPham(null, sp.getTen(), sp.getGia()));
            }
            damBaoCongThucRieng(sp, false);
            NuocUongSan updated = sanPhamService.luuNuocUong(sp);
            ghiLog("SAN_PHAM", "CAP_NHAT", updated.getTen());
            return ResponseEntity.ok(updated);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/san-pham/{sanPhamId}/cong-thuc")
    public ResponseEntity<Map<String, Object>> layCongThucSanPham(@PathVariable UUID sanPhamId) {
        return sanPhamService.layNuocUongTheoId(sanPhamId).map(sp -> {
            CongThuc congThuc = sp.getCongThucCoBan();
            List<Map<String, Object>> nguyenLieuCongThuc = congThuc != null && congThuc.getLuongNguyenLieus() != null
                    ? congThuc.getLuongNguyenLieus().stream().map(this::toCongThucIngredientPayload).toList()
                    : List.of();
            Map<String, Object> payload = new HashMap<>();
            payload.put("sanPhamId", sp.getId());
            payload.put("congThucId", congThuc != null ? congThuc.getId() : null);
            payload.put("congThucTen", congThuc != null ? congThuc.getTen() : "Công thức mặc định");
            payload.put("nguyenLieu", nguyenLieuCongThuc);
            payload.put("danhSachNguyenLieuCoSan", sanPhamService.layDanhSachNguyenLieu());
            payload.put("toppingCoSan", tuyChonTuyChinhRepository.findAll().stream()
                    .filter(opt -> "TOPPING".equalsIgnoreCase(opt.getNhom()))
                    .toList());
            payload.put("mucDuongTuyChon", parseCsv(sp.getMucDuongTuyChon(), List.of("Không đường", "Ít đường", "Bình thường", "Nhiều đường")));
            payload.put("mucDuongMacDinh", sp.getMucDuongMacDinh() != null ? sp.getMucDuongMacDinh() : "Bình thường");
            payload.put("mucDaTuyChon", parseCsv(sp.getMucDaTuyChon(), List.of("Không đá", "Ít đá", "Bình thường", "Nhiều đá")));
            payload.put("mucDaMacDinh", sp.getMucDaMacDinh() != null ? sp.getMucDaMacDinh() : "Bình thường");
            payload.put("kichCoTuyChon", parseCsv(sp.getKichCoTuyChon(), List.of("Nhỏ", "Vừa", "Lớn")));
            payload.put("kichCoMacDinh", sp.getKichCoMacDinh() != null ? sp.getKichCoMacDinh() : "Vừa");
            payload.put("coApDungSize", Boolean.TRUE.equals(sp.getCoApDungSize()));
            payload.put("toppingChoPhep", parseUuidCsv(sp.getToppingChoPhep()));
            return ResponseEntity.ok(payload);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/san-pham/{sanPhamId}/cong-thuc/nguyen-lieu")
    public ResponseEntity<Map<String, Object>> themNguyenLieuCongThuc(
            @PathVariable UUID sanPhamId,
            @RequestBody CongThucNguyenLieuRequest request) {
        return sanPhamService.layNuocUongTheoId(sanPhamId).map(sp -> {
            CongThuc congThuc = damBaoCongThucRieng(sp, true);
            if (congThuc == null) {
                throw new IllegalArgumentException("Sản phẩm chưa có công thức mặc định");
            }
            NguyenLieu nguyenLieu = sanPhamService.layNguyenLieuTheoId(request.nguyenLieuId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nguyên liệu"));
            LuongNguyenLieu entity = LuongNguyenLieu.builder()
                    .congThuc(congThuc)
                    .nguyenLieu(nguyenLieu)
                    .soLuong(request.soLuong() != null ? request.soLuong() : BigDecimal.ZERO)
                    .donVi(request.donVi() != null && !request.donVi().isBlank() ? request.donVi() : nguyenLieu.getDonVi())
                    .build();
            LuongNguyenLieu saved = luongNguyenLieuRepository.save(entity);
            ghiLog("CONG_THUC", "THEM_NGUYEN_LIEU", sp.getTen() + ":" + nguyenLieu.getTen());
            return ResponseEntity.ok(toCongThucIngredientPayload(saved));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/san-pham/{sanPhamId}/cong-thuc/nguyen-lieu/{luongId}")
    public ResponseEntity<Map<String, Object>> capNhatNguyenLieuCongThuc(
            @PathVariable UUID sanPhamId,
            @PathVariable UUID luongId,
            @RequestBody CongThucNguyenLieuRequest request) {
        return sanPhamService.layNuocUongTheoId(sanPhamId).map(sp -> {
            CongThuc congThuc = damBaoCongThucRieng(sp, true);
            return
                luongNguyenLieuRepository.findById(luongId).map(entity -> {
                    if (congThuc == null || entity.getCongThuc() == null
                            || !congThuc.getId().equals(entity.getCongThuc().getId())) {
                        throw new IllegalArgumentException("Nguyên liệu không thuộc công thức của sản phẩm");
                    }
                    if (request.nguyenLieuId() != null) {
                        NguyenLieu nguyenLieu = sanPhamService.layNguyenLieuTheoId(request.nguyenLieuId())
                                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nguyên liệu"));
                        entity.setNguyenLieu(nguyenLieu);
                    }
                    if (request.soLuong() != null) {
                        entity.setSoLuong(request.soLuong());
                    }
                    if (request.donVi() != null && !request.donVi().isBlank()) {
                        entity.setDonVi(request.donVi());
                    }
                    LuongNguyenLieu updated = luongNguyenLieuRepository.save(entity);
                    ghiLog("CONG_THUC", "CAP_NHAT_NGUYEN_LIEU", sp.getTen() + ":" + updated.getId());
                    return ResponseEntity.ok(toCongThucIngredientPayload(updated));
                }).orElseGet(() -> ResponseEntity.notFound().build());
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/san-pham/{sanPhamId}/cong-thuc/nguyen-lieu/{luongId}")
    public ResponseEntity<Map<String, Object>> xoaNguyenLieuCongThuc(
            @PathVariable UUID sanPhamId,
            @PathVariable UUID luongId) {
        return sanPhamService.layNuocUongTheoId(sanPhamId).map(sp -> {
            CongThuc congThuc = damBaoCongThucRieng(sp, true);
            return
                luongNguyenLieuRepository.findById(luongId).map(entity -> {
                    if (congThuc == null || entity.getCongThuc() == null
                            || !congThuc.getId().equals(entity.getCongThuc().getId())) {
                        throw new IllegalArgumentException("Nguyên liệu không thuộc công thức của sản phẩm");
                    }
                    luongNguyenLieuRepository.delete(entity);
                    ghiLog("CONG_THUC", "XOA_NGUYEN_LIEU", sp.getTen() + ":" + luongId);
                    return ResponseEntity.ok(Map.<String, Object>of("success", true));
                }).orElseGet(() -> ResponseEntity.notFound().build());
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/san-pham/{sanPhamId}/cong-thuc/tuy-chinh")
    public ResponseEntity<Map<String, Object>> capNhatTuyChinhCongThuc(
            @PathVariable UUID sanPhamId,
            @RequestBody TuyChinhCongThucRequest request) {
        return sanPhamService.layNuocUongTheoId(sanPhamId).map(sp -> {
            if (request.mucDuongTuyChon() != null) {
                sp.setMucDuongTuyChon(joinCsv(request.mucDuongTuyChon()));
            }
            if (request.mucDuongMacDinh() != null) {
                sp.setMucDuongMacDinh(request.mucDuongMacDinh());
            }
            if (request.mucDaTuyChon() != null) {
                sp.setMucDaTuyChon(joinCsv(request.mucDaTuyChon()));
            }
            if (request.mucDaMacDinh() != null) {
                sp.setMucDaMacDinh(request.mucDaMacDinh());
            }
            if (request.kichCoTuyChon() != null) {
                sp.setKichCoTuyChon(joinCsv(request.kichCoTuyChon()));
            }
            if (request.kichCoMacDinh() != null) {
                sp.setKichCoMacDinh(request.kichCoMacDinh());
            }
            if (request.coApDungSize() != null) {
                sp.setCoApDungSize(request.coApDungSize());
            }
            if (request.toppingChoPhep() != null) {
                sp.setToppingChoPhep(joinUuidCsv(request.toppingChoPhep()));
            }
            NuocUongSan updated = sanPhamService.luuNuocUong(sp);
            ghiLog("CONG_THUC", "CAP_NHAT_TUY_CHINH", updated.getTen());
            Map<String, Object> payload = new HashMap<>();
            payload.put("success", true);
            payload.put("sanPhamId", updated.getId());
            payload.put("mucDuongTuyChon", parseCsv(updated.getMucDuongTuyChon(), List.of()));
            payload.put("mucDuongMacDinh", updated.getMucDuongMacDinh());
            payload.put("mucDaTuyChon", parseCsv(updated.getMucDaTuyChon(), List.of()));
            payload.put("mucDaMacDinh", updated.getMucDaMacDinh());
            payload.put("kichCoTuyChon", parseCsv(updated.getKichCoTuyChon(), List.of()));
            payload.put("kichCoMacDinh", updated.getKichCoMacDinh());
            payload.put("coApDungSize", Boolean.TRUE.equals(updated.getCoApDungSize()));
            payload.put("toppingChoPhep", parseUuidCsv(updated.getToppingChoPhep()));
            return ResponseEntity.ok(payload);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/san-pham/{sanPhamId}")
    public ResponseEntity<Map<String, Object>> xoaSanPham(@PathVariable UUID sanPhamId) {
        sanPhamService.xoaNuocUong(sanPhamId);
        ghiLog("SAN_PHAM", "XOA", sanPhamId.toString());
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PatchMapping("/san-pham/{sanPhamId}/trang-thai-ban")
    public ResponseEntity<NuocUongSan> batTatTrangThaiBan(
            @PathVariable UUID sanPhamId,
            @RequestBody TrangThaiBanRequest request) {
        return sanPhamService.layNuocUongTheoId(sanPhamId).map(sp -> {
            sp.setDangKinhDoanh(request.dangBan());
            NuocUongSan updated = sanPhamService.luuNuocUong(sp);
            ghiLog("SAN_PHAM", "TRANG_THAI_BAN", sp.getTen() + ":" + request.dangBan());
            return ResponseEntity.ok(updated);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/san-pham/{sanPhamId}/hinh-anh", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NuocUongSan> capNhatHinhAnhSanPham(
            @PathVariable UUID sanPhamId,
            @RequestParam("hinhAnh") MultipartFile[] files) {
        return sanPhamService.layNuocUongTheoId(sanPhamId).map(sp -> {
            List<String> current = sp.getHinhAnh() != null ? new ArrayList<>(sp.getHinhAnh()) : new ArrayList<>();
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    String imageUrl = fileStorageService.storeFile(file);
                    current.add(0, imageUrl);
                }
            }
            sp.setHinhAnh(current);
            NuocUongSan updated = sanPhamService.luuNuocUong(sp);
            ghiLog("SAN_PHAM", "CAP_NHAT_ANH", sp.getTen());
            return ResponseEntity.ok(updated);
        }).orElseGet(() -> ResponseEntity.status(404).build());
    }

    @DeleteMapping("/san-pham/{sanPhamId}/hinh-anh")
    public ResponseEntity<NuocUongSan> xoaHinhAnhSanPham(
            @PathVariable UUID sanPhamId,
            @RequestParam int index) {

        Optional<NuocUongSan> optional = sanPhamService.layNuocUongTheoId(sanPhamId);

        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        NuocUongSan sp = optional.get();

        List<String> current = sp.getHinhAnh() != null
                ? new ArrayList<>(sp.getHinhAnh())
                : new ArrayList<>();

        if (index < 0 || index >= current.size()) {
            return ResponseEntity.badRequest().build(); // tự infer đúng kiểu
        }

        current.remove(index);
        sp.setHinhAnh(current);

        NuocUongSan updated = sanPhamService.luuNuocUong(sp);
        ghiLog("SAN_PHAM", "XOA_ANH", sp.getTen());

        return ResponseEntity.ok(updated);
    }

    @GetMapping("/tuy-chon")
    public ResponseEntity<List<TuyChonTuyChinh>> layTuyChon() {
        return ResponseEntity.ok(tuyChonTuyChinhRepository.findAll());
    }

    @PostMapping("/tuy-chon")
    public ResponseEntity<TuyChonTuyChinh> taoTuyChon(@RequestBody TuyChonRequest request) {
        TuyChonTuyChinh entity = TuyChonTuyChinh.builder()
                .ten(request.ten())
                .nhom(request.nhom())
                .giaThem(request.giaThem() != null ? request.giaThem() : BigDecimal.ZERO)
                .kichHoat(request.kichHoat() == null || request.kichHoat())
                .build();
        TuyChonTuyChinh saved = tuyChonTuyChinhRepository.save(entity);
        ghiLog("TUY_CHON", "TAO", saved.getTen());
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/tuy-chon/{id}")
    public ResponseEntity<TuyChonTuyChinh> capNhatTuyChon(@PathVariable UUID id, @RequestBody TuyChonRequest request) {
        return tuyChonTuyChinhRepository.findById(id).map(entity -> {
            if (request.ten() != null) {
                entity.setTen(request.ten());
            }
            if (request.nhom() != null) {
                entity.setNhom(request.nhom());
            }
            if (request.giaThem() != null) {
                entity.setGiaThem(request.giaThem());
            }
            if (request.kichHoat() != null) {
                entity.setKichHoat(request.kichHoat());
            }
            TuyChonTuyChinh updated = tuyChonTuyChinhRepository.save(entity);
            ghiLog("TUY_CHON", "CAP_NHAT", updated.getTen());
            return ResponseEntity.ok(updated);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/tuy-chon/{id}")
    public ResponseEntity<Map<String, Object>> xoaTuyChon(@PathVariable UUID id) {
        tuyChonTuyChinhRepository.deleteById(id);
        ghiLog("TUY_CHON", "XOA", id.toString());
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/nguyen-lieu")
    public ResponseEntity<List<NguyenLieu>> layDanhSachNguyenLieu() {
        return ResponseEntity.ok(sanPhamService.layDanhSachNguyenLieu());
    }

    @PostMapping("/nguyen-lieu")
    public ResponseEntity<NguyenLieu> taoNguyenLieu(@RequestBody NguyenLieuRequest request) {
        NguyenLieu nguyenLieu = NguyenLieu.builder()
                .ten(request.ten())
                .donVi(request.donVi() != null ? request.donVi() : "kg")
                .soLuongTon(request.soLuongTon() != null ? request.soLuongTon() : BigDecimal.ZERO)
                .giaDonVi(request.giaDonVi() != null ? request.giaDonVi() : BigDecimal.ZERO)
                .nguongCanhBao(request.nguongCanhBao() != null ? request.nguongCanhBao() : BigDecimal.ZERO)
                .build();
        NguyenLieu saved = sanPhamService.luuNguyenLieu(nguyenLieu);
        ghiLog("NGUYEN_LIEU", "TAO", saved.getTen());
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/nguyen-lieu/{id}")
    public ResponseEntity<NguyenLieu> capNhatNguyenLieu(@PathVariable UUID id, @RequestBody NguyenLieuRequest request) {
        return sanPhamService.layNguyenLieuTheoId(id).map(entity -> {
            if (request.ten() != null) {
                entity.setTen(request.ten());
            }
            if (request.donVi() != null) {
                entity.setDonVi(request.donVi());
            }
            if (request.soLuongTon() != null) {
                entity.setSoLuongTon(request.soLuongTon());
            }
            if (request.giaDonVi() != null) {
                entity.setGiaDonVi(request.giaDonVi());
            }
            if (request.nguongCanhBao() != null) {
                entity.setNguongCanhBao(request.nguongCanhBao());
            }
            NguyenLieu updated = sanPhamService.luuNguyenLieu(entity);
            ghiLog("NGUYEN_LIEU", "CAP_NHAT", updated.getTen());
            return ResponseEntity.ok(updated);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/nguyen-lieu/{id}")
    public ResponseEntity<Map<String, Object>> xoaNguyenLieu(@PathVariable UUID id) {
        sanPhamService.xoaNguyenLieu(id);
        ghiLog("NGUYEN_LIEU", "XOA", id.toString());
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/kho/nhap")
    public ResponseEntity<NguyenLieu> nhapKho(@RequestBody CapNhatKhoRequest request) {
        return capNhatKho(request, "NHAP");
    }

    @PostMapping("/kho/xuat")
    public ResponseEntity<NguyenLieu> xuatKho(@RequestBody CapNhatKhoRequest request) {
        return capNhatKho(request, "XUAT");
    }

    @GetMapping("/kho/canh-bao")
    public ResponseEntity<List<NguyenLieu>> layCanhBaoTonKho() {
        return ResponseEntity.ok(sanPhamService.layNguyenLieuCanhBao());
    }

    @GetMapping("/kho/lich-su")
    public ResponseEntity<List<LichSuKho>> lichSuKho() {
        return ResponseEntity.ok(lichSuKhoRepository.findTop100ByOrderByThoiGianDesc());
    }

    @GetMapping("/don-hang")
    public ResponseEntity<List<DonHang>> layDanhSachDonHang(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay) {
        List<DonHang> all = donHangRepository.findAll();
        List<DonHang> filtered = all.stream()
                .filter(o -> {
                    if (tuNgay == null || denNgay == null || o.getNgayDat() == null) {
                        return true;
                    }
                    LocalDate d = o.getNgayDat().toLocalDate();
                    return !d.isBefore(tuNgay) && !d.isAfter(denNgay);
                })
                .filter(o -> trangThai == null || trangThai.isBlank() || mapTrangThaiVeDb(trangThai).equalsIgnoreCase(o.getTrangThaiDb()))
                .filter(o -> {
                    if (q == null || q.isBlank()) {
                        return true;
                    }
                    String k = q.toLowerCase(Locale.ROOT);
                    String idText = o.getId() != null ? o.getId().toString().toLowerCase(Locale.ROOT) : "";
                    String ten = o.getKhachHang() != null && o.getKhachHang().getTenDangNhap() != null
                            ? o.getKhachHang().getTenDangNhap().toLowerCase(Locale.ROOT) : "";
                    return idText.contains(k) || ten.contains(k);
                })
                .sorted(Comparator.comparing(DonHang::getNgayDat, Comparator.nullsLast(LocalDateTime::compareTo)).reversed())
                .toList();
        return ResponseEntity.ok(filtered);
    }

    @GetMapping("/don-hang/{donHangId}")
    public ResponseEntity<Map<String, Object>> layChiTietDonHang(@PathVariable UUID donHangId) {
        return donHangRepository.findById(donHangId)
                .map(donHang -> ResponseEntity.ok(toDonHangDetailPayload(donHang)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/don-hang/{donHangId}/trang-thai")
    public ResponseEntity<DonHang> capNhatTrangThaiDonHang(
            @PathVariable UUID donHangId,
            @RequestBody CapNhatTrangThaiDonHangRequest request) {
        return donHangRepository.findById(donHangId).map(donHang -> {
            String dbStatus = mapTrangThaiVeDb(request.trangThai());
            if ("CHO_XAC_NHAN".equals(dbStatus)) {
                donHang.setTrangThai(new TrangThaiChoXacNhan());
            } else if ("DA_XAC_NHAN".equals(dbStatus)) {
                donHang.setTrangThai(new TrangThaiDaXacNhan());
            } else if ("DA_GIAO".equals(dbStatus)) {
                donHang.setTrangThai(new TrangThaiDaGiao());
            } else if ("DA_HUY".equals(dbStatus)) {
                donHang.setTrangThai(new TrangThaiDaHuy());
            }
            donHang.setTrangThaiDb(dbStatus);
            DonHang updated = donHangRepository.save(donHang);
            ghiLog("DON_HANG", "CAP_NHAT_TRANG_THAI", updated.getId() + ":" + dbStatus);
            return ResponseEntity.ok(updated);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/khach-hang")
    public ResponseEntity<List<Map<String, Object>>> layDanhSachKhachHang(@RequestParam(required = false) String q) {
        List<Map<String, Object>> data = nguoiDungService.danhSachKhachHang().stream()
                .filter(u -> q == null || q.isBlank() || checkUserMatch(u, q))
                .map(this::toUserPayload)
                .toList();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/khach-hang/{khachHangId}/lich-su")
    public ResponseEntity<List<DonHang>> lichSuMuaHang(@PathVariable UUID khachHangId) {
        return ResponseEntity.ok(donHangRepository.findByKhachHangId(khachHangId));
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
            ghiLog("KHACH_HANG", "CAP_NHAT", saved.getId().toString());
            return ResponseEntity.ok(toUserPayload(saved));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/nhan-vien")
    @PreAuthorize("hasAuthority('staff:manage')")
    public ResponseEntity<List<Map<String, Object>>> layDanhSachNhanVien() {
        return ResponseEntity.ok(nguoiDungService.danhSachNhanVien().stream()
                .map(this::toUserPayload)
                .toList());
    }

    @PostMapping("/nhan-vien")
    @PreAuthorize("hasAnyAuthority('staff:manage','role:assign')")
    public ResponseEntity<Map<String, Object>> taoNhanVien(@RequestBody TaoNhanVienRequest request) {
        VaiTro role = request.vaiTro() == null ? VaiTro.NHAN_VIEN_BAN_HANG : request.vaiTro();
        NguoiDung nv = nguoiDungService.taoNhanVien(request.tenDangNhap(), request.email(), request.matKhau(), role);
        ghiLog("NHAN_VIEN", "TAO", nv.getTenDangNhap());
        return ResponseEntity.ok(toUserPayload(nv));
    }

    @PutMapping("/nhan-vien/{nhanVienId}")
    @PreAuthorize("hasAnyAuthority('staff:manage','role:assign')")
    public ResponseEntity<Map<String, Object>> capNhatNhanVien(
            @PathVariable UUID nhanVienId,
            @RequestBody CapNhatNhanVienRequest request) {
        NguoiDung updated = nguoiDungService.capNhatNhanVien(
                nhanVienId,
                request.email(),
                request.vaiTro(),
                request.kichHoat()
        );
        ghiLog("NHAN_VIEN", "CAP_NHAT", updated.getId().toString());
        return ResponseEntity.ok(toUserPayload(updated));
    }

    @DeleteMapping("/nhan-vien/{nhanVienId}")
    @PreAuthorize("hasAuthority('staff:manage')")
    public ResponseEntity<Map<String, Object>> xoaNhanVien(@PathVariable UUID nhanVienId) {
        nguoiDungService.timTheoId(nhanVienId).ifPresent(user -> {
            if (tenNguoiDangNhap().equalsIgnoreCase(user.getTenDangNhap()) && user.getVaiTro() == VaiTro.QUAN_TRI_VIEN) {
                throw new IllegalArgumentException("Không thể tự xóa tài khoản ADMIN của chính mình");
            }
        });
        nguoiDungService.xoaNguoiDung(nhanVienId);
        ghiLog("NHAN_VIEN", "XOA", nhanVienId.toString());
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/nhan-vien/hoat-dong")
    @PreAuthorize("hasAuthority('staff:manage')")
    public ResponseEntity<List<NhatKyHeThong>> theoDoiHoatDongNhanVien() {
        List<NhatKyHeThong> logs = nhatKyHeThongRepository.findTop200ByOrderByThoiGianDesc().stream()
                .filter(l -> "NHAN_VIEN".equalsIgnoreCase(l.getMoDun()) || "DON_HANG".equalsIgnoreCase(l.getMoDun()))
                .toList();
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/khuyen-mai")
    public ResponseEntity<List<Map<String, Object>>> layDanhSachKhuyenMai() {
        return ResponseEntity.ok(maGiamGiaRepository.findAll().stream().map(this::toKhuyenMaiPayload).toList());
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
        MaGiamGia saved = maGiamGiaRepository.save(entity);
        ghiLog("KHUYEN_MAI", "TAO", saved.getMa());
        return ResponseEntity.ok(toKhuyenMaiPayload(saved));
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
            MaGiamGia updated = maGiamGiaRepository.save(entity);
            ghiLog("KHUYEN_MAI", "CAP_NHAT", updated.getMa());
            return ResponseEntity.ok(toKhuyenMaiPayload(updated));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/khuyen-mai/{khuyenMaiId}")
    public ResponseEntity<Map<String, Object>> xoaKhuyenMai(@PathVariable UUID khuyenMaiId) {
        maGiamGiaRepository.deleteById(khuyenMaiId);
        ghiLog("KHUYEN_MAI", "XOA", khuyenMaiId.toString());
        return ResponseEntity.ok(Map.of("success", true));
    }

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
        Map<String, BigDecimal> grouped = new LinkedHashMap<>();
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

    @GetMapping("/he-thong/cau-hinh")
    public ResponseEntity<List<CauHinhHeThong>> layCauHinhHeThong() {
        return ResponseEntity.ok(cauHinhHeThongRepository.findAll());
    }

    @PutMapping("/he-thong/cau-hinh")
    public ResponseEntity<CauHinhHeThong> luuCauHinhHeThong(@RequestBody CauHinhRequest request) {
        CauHinhHeThong entity = cauHinhHeThongRepository.findByConfigKey(request.configKey())
                .orElseGet(CauHinhHeThong::new);
        entity.setConfigKey(request.configKey());
        entity.setConfigValue(request.configValue());
        entity.setMoTa(request.moTa());
        CauHinhHeThong saved = cauHinhHeThongRepository.save(entity);
        ghiLog("HE_THONG", "CAU_HINH", saved.getConfigKey());
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/he-thong/rbac")
    @PreAuthorize("hasAuthority('role:assign')")
    public ResponseEntity<List<VaiTroQuyen>> layRbac() {
        damBaoRbacMacDinh();
        return ResponseEntity.ok(vaiTroQuyenRepository.findAll());
    }

    @PutMapping("/he-thong/rbac/{vaiTro}")
    @PreAuthorize("hasAuthority('role:assign')")
    public ResponseEntity<VaiTroQuyen> capNhatRbac(
            @PathVariable String vaiTro,
            @RequestBody RbacRequest request) {
        VaiTroQuyen entity = vaiTroQuyenRepository.findByVaiTro(vaiTro)
                .orElseGet(VaiTroQuyen::new);
        entity.setVaiTro(vaiTro);
        entity.setQuyenCsv(String.join(",", request.quyens()));
        VaiTroQuyen saved = vaiTroQuyenRepository.save(entity);
        ghiLog("RBAC", "CAP_NHAT", vaiTro);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/he-thong/log")
    public ResponseEntity<List<NhatKyHeThong>> layLogHeThong() {
        return ResponseEntity.ok(nhatKyHeThongRepository.findTop200ByOrderByThoiGianDesc());
    }

    @PatchMapping("/tai-khoan/trang-thai")
    public ResponseEntity<Map<String, Object>> capNhatTrangThaiTaiKhoan(@RequestBody CapNhatTrangThaiTaiKhoanRequest request) {
        if (request.nguoiDungId() == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "nguoiDungId là bắt buộc"));
        }
        NguoiDung updated = nguoiDungService.khoaMoTaiKhoan(request.nguoiDungId(), request.active() != null && request.active());
        ghiLog("TAI_KHOAN", "TRANG_THAI", updated.getId() + ":" + updated.isTrangThaiHoatDong());
        return ResponseEntity.ok(Map.of("success", true, "user", toUserPayload(updated)));
    }

    private ResponseEntity<NguyenLieu> capNhatKho(CapNhatKhoRequest request, String loai) {
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
                    .nguoiThucHien(tenNguoiDangNhap())
                    .build();
            lichSuKhoRepository.save(lichSuKho);
            ghiLog("KHO", loai, saved.getTen() + ":" + soLuong);
            return ResponseEntity.ok(saved);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    private Map<String, Object> toUserPayload(NguoiDung user) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", user.getId());
        payload.put("tenDangNhap", user.getTenDangNhap());
        payload.put("email", user.getEmail());
        payload.put("vaiTro", user.getVaiTro());
        payload.put("active", user.isTrangThaiHoatDong());
        payload.put("trangThaiHoatDong", user.isTrangThaiHoatDong());
        if (user instanceof KhachHang kh) {
            payload.put("hoTen", kh.getHoTen());
            payload.put("soDienThoai", kh.getSoDienThoai());
        }
        return payload;
    }

    private boolean checkUserMatch(NguoiDung u, String keyword) {
        String k = keyword.toLowerCase(Locale.ROOT);
        String username = u.getTenDangNhap() != null ? u.getTenDangNhap().toLowerCase(Locale.ROOT) : "";
        String email = u.getEmail() != null ? u.getEmail().toLowerCase(Locale.ROOT) : "";
        String hoten = "";
        if (u instanceof KhachHang kh && kh.getHoTen() != null) {
            hoten = kh.getHoTen().toLowerCase(Locale.ROOT);
        }
        return username.contains(k) || email.contains(k) || hoten.contains(k);
    }

    private String mapTrangThaiVeDb(String input) {
        if (input == null) {
            return "CHO_XAC_NHAN";
        }
        String normalized = input.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "CHO_XU_LY", "CHO_XAC_NHAN" -> "CHO_XAC_NHAN";
            case "DANG_LAM", "DA_XAC_NHAN", "DANG_CHUAN_BI" -> "DA_XAC_NHAN";
            case "HOAN_THANH", "DA_GIAO" -> "DA_GIAO";
            case "HUY", "DA_HUY" -> "DA_HUY";
            default -> normalized;
        };
    }

    private void damBaoRbacMacDinh() {
        taoRbacNeuChuaCo("QUAN_TRI_VIEN", List.of("ALL"));
        taoRbacNeuChuaCo("NHAN_VIEN_BAN_HANG", List.of("DON_HANG_XEM", "DON_HANG_CAP_NHAT"));
        taoRbacNeuChuaCo("QUAN_LY_KHO", List.of("KHO_XEM", "KHO_NHAP_XUAT", "SAN_PHAM_XEM"));
        taoRbacNeuChuaCo("KHACH_HANG", List.of("DON_HANG_CUA_TOI", "GIO_HANG"));
    }

    private void taoRbacNeuChuaCo(String role, List<String> permissions) {
        vaiTroQuyenRepository.findByVaiTro(role).orElseGet(() -> {
            VaiTroQuyen entity = new VaiTroQuyen();
            entity.setVaiTro(role);
            entity.setQuyenCsv(String.join(",", permissions));
            return vaiTroQuyenRepository.save(entity);
        });
    }

    private void ghiLog(String module, String action, String detail) {
        NhatKyHeThong log = NhatKyHeThong.builder()
                .moDun(module)
                .hanhDong(action)
                .chiTiet(detail)
                .nguoiThucHien(tenNguoiDangNhap())
                .build();
        nhatKyHeThongRepository.save(log);
    }

    private String tenNguoiDangNhap() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "SYSTEM";
    }

    private void apDungCauHinhTuyChinhMacDinh(NuocUongSan sanPham) {
        sanPham.setMucDuongTuyChon("Không đường,Ít đường,Bình thường,Nhiều đường");
        sanPham.setMucDuongMacDinh("Bình thường");
        sanPham.setMucDaTuyChon("Không đá,Ít đá,Bình thường,Nhiều đá");
        sanPham.setMucDaMacDinh("Bình thường");
        sanPham.setKichCoTuyChon("Nhỏ,Vừa,Lớn");
        sanPham.setKichCoMacDinh("Vừa");
        sanPham.setCoApDungSize(true);
        sanPham.setToppingChoPhep("");
    }

    private CongThuc taoCongThucRiengChoSanPham(UUID congThucMauId, String tenSanPham, BigDecimal giaCoBan) {
        CongThuc congThucMau = null;
        if (congThucMauId != null) {
            congThucMau = congThucRepository.findById(congThucMauId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy công thức"));
        }
        CongThuc congThucMoi = CongThuc.builder()
                .ten("CT - " + (tenSanPham != null && !tenSanPham.isBlank() ? tenSanPham : "Sản phẩm"))
                .moTa("Công thức riêng cho sản phẩm")
                .giaCoBan(giaCoBan != null ? giaCoBan : BigDecimal.ZERO)
                .build();
        if (congThucMau != null && congThucMau.getLuongNguyenLieus() != null) {
            List<LuongNguyenLieu> danhSachSaoChep = congThucMau.getLuongNguyenLieus().stream()
                    .map(luong -> LuongNguyenLieu.builder()
                            .congThuc(congThucMoi)
                            .nguyenLieu(luong.getNguyenLieu())
                            .soLuong(luong.getSoLuong())
                            .donVi(luong.getDonVi())
                            .build())
                    .toList();
            congThucMoi.setLuongNguyenLieus(new ArrayList<>(danhSachSaoChep));
        }
        return congThucRepository.save(congThucMoi);
    }

    private CongThuc damBaoCongThucRieng(NuocUongSan sanPham, boolean saveProductIfChanged) {
        if (sanPham.getCongThucCoBan() == null) {
            CongThuc created = taoCongThucRiengChoSanPham(null, sanPham.getTen(), sanPham.getGia());
            sanPham.setCongThucCoBan(created);
            if (saveProductIfChanged) {
                sanPhamService.luuNuocUong(sanPham);
            }
            return created;
        }
        UUID congThucId = sanPham.getCongThucCoBan().getId();
        if (congThucId == null) {
            return sanPham.getCongThucCoBan();
        }
        long soSanPhamDungChung = nuocUongSanRepository.countByCongThucCoBanId(congThucId);
        if (soSanPhamDungChung <= 1) {
            return sanPham.getCongThucCoBan();
        }
        CongThuc cloned = taoCongThucRiengChoSanPham(congThucId, sanPham.getTen(), sanPham.getGia());
        sanPham.setCongThucCoBan(cloned);
        if (saveProductIfChanged) {
            sanPhamService.luuNuocUong(sanPham);
        }
        return cloned;
    }

    private Map<String, Object> toCongThucIngredientPayload(LuongNguyenLieu entity) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", entity.getId());
        payload.put("nguyenLieuId", entity.getNguyenLieu() != null ? entity.getNguyenLieu().getId() : null);
        payload.put("tenNguyenLieu", entity.getNguyenLieu() != null ? entity.getNguyenLieu().getTen() : "");
        payload.put("soLuong", entity.getSoLuong());
        payload.put("donVi", entity.getDonVi());
        return payload;
    }

    private Map<String, Object> toDonHangDetailPayload(DonHang donHang) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", donHang.getId());
        payload.put("ngayDat", donHang.getNgayDat());
        payload.put("tongTien", donHang.getTongTien());
        payload.put("trangThaiDb", donHang.getTrangThaiDb());
        payload.put("khachHang", donHang.getKhachHang() != null ? donHang.getKhachHang().getTenDangNhap() : "Khách lẻ");
        List<Map<String, Object>> items = donHang.getChiTietDonHangs() == null ? List.of() : donHang.getChiTietDonHangs().stream().map(item -> {
            Map<String, Object> row = new HashMap<>();
            row.put("sanPham", item.getNuocUong() != null ? item.getNuocUong().getTen() : "Sản phẩm");
            row.put("soLuong", item.getSoLuong());
            row.put("thanhTien", item.getThanhTien());
            row.put("congThuc", item.getNuocUong() != null && item.getNuocUong().getCongThucCoBan() != null
                    ? item.getNuocUong().getCongThucCoBan().getTen() : "Mặc định");
            Map<String, Object> tuyChinh = new HashMap<>();
            tuyChinh.put("mucDa", item.getTuyChinh() != null ? item.getTuyChinh().getMucDa() : null);
            tuyChinh.put("ghiChu", item.getTuyChinh() != null ? item.getTuyChinh().getGhiChu() : "");
            row.put("tuyChinh", tuyChinh);
            return row;
        }).toList();
        payload.put("chiTiet", items);
        return payload;
    }

    private Map<String, Object> toKhuyenMaiPayload(MaGiamGia entity) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", entity.getId());
        payload.put("ma", entity.getMa());
        payload.put("loaiGiam", entity.getLoaiGiam());
        payload.put("giaTri", entity.getGiaTri());
        payload.put("ngayBatDau", entity.getNgayBatDau());
        payload.put("ngayKetThuc", entity.getNgayKetThuc());
        payload.put("kichHoat", entity.isKichHoat());
        payload.put("apDungToanHeThong", entity.isApDungToanHeThong());
        payload.put("sanPhamIds", entity.getSanPhamApDung() == null ? List.of()
                : entity.getSanPhamApDung().stream().map(s -> s.getId()).toList());
        return payload;
    }

    private List<String> parseCsv(String csv, List<String> defaults) {
        if (csv == null || csv.isBlank()) {
            return defaults;
        }
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
    }

    private String joinCsv(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "";
        }
        return values.stream().map(String::trim).filter(v -> !v.isBlank()).collect(Collectors.joining(","));
    }

    private List<UUID> parseUuidCsv(String csv) {
        if (csv == null || csv.isBlank()) {
            return List.of();
        }
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(v -> !v.isBlank())
                .map(UUID::fromString)
                .toList();
    }

    private String joinUuidCsv(List<UUID> values) {
        if (values == null || values.isEmpty()) {
            return "";
        }
        return values.stream().map(UUID::toString).collect(Collectors.joining(","));
    }

    public record SanPhamRequest(
            String ten,
            BigDecimal gia,
            String moTa,
            String danhMuc,
            Boolean dangBan,
            List<String> hinhAnh,
            UUID congThucId
    ) {}

    public record TrangThaiBanRequest(boolean dangBan) {}

    public record TuyChonRequest(
            String ten,
            String nhom,
            BigDecimal giaThem,
            Boolean kichHoat
    ) {}

    public record NguyenLieuRequest(
            String ten,
            String donVi,
            BigDecimal soLuongTon,
            BigDecimal giaDonVi,
            BigDecimal nguongCanhBao
    ) {}

    public record CapNhatKhoRequest(
            UUID nguyenLieuId,
            BigDecimal soLuong,
            String ghiChu
    ) {}

    public record CongThucNguyenLieuRequest(
            UUID nguyenLieuId,
            BigDecimal soLuong,
            String donVi
    ) {}

    public record TuyChinhCongThucRequest(
            List<String> mucDuongTuyChon,
            String mucDuongMacDinh,
            List<String> mucDaTuyChon,
            String mucDaMacDinh,
            List<String> kichCoTuyChon,
            String kichCoMacDinh,
            Boolean coApDungSize,
            List<UUID> toppingChoPhep
    ) {}

    public record CapNhatTrangThaiDonHangRequest(String trangThai) {}

    public record CapNhatNguoiDungRequest(
            String email,
            String hoTen,
            String soDienThoai,
            Boolean kichHoat
    ) {}

    public record TaoNhanVienRequest(
            String tenDangNhap,
            String email,
            String matKhau,
            VaiTro vaiTro
    ) {}

    public record CapNhatNhanVienRequest(
            String email,
            VaiTro vaiTro,
            Boolean kichHoat
    ) {}

    public record CapNhatTrangThaiTaiKhoanRequest(
            UUID nguoiDungId,
            Boolean active
    ) {}

    public record KhuyenMaiRequest(
            String maGiamGia,
            LoaiGiamGiaEnum loaiGiam,
            BigDecimal giaTri,
            LocalDate ngayBatDau,
            LocalDate ngayKetThuc,
            Boolean kichHoat,
            Boolean apDungToanHeThong,
            List<UUID> sanPhamIds
    ) {}

    public record CauHinhRequest(
            String configKey,
            String configValue,
            String moTa
    ) {}

    public record RbacRequest(List<String> quyens) {}
}


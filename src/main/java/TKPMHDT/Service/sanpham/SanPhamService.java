package TKPMHDT.Service.sanpham;

import TKPMHDT.Entity.sanpham.NguyenLieu;
import TKPMHDT.Entity.sanpham.NuocUongSan;
import TKPMHDT.Entity.sanpham.enums.LoaiNguyenLieu;
import TKPMHDT.Repository.sanpham.NguyenLieuRepository;
import TKPMHDT.Repository.sanpham.NuocUongSanRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SanPhamService {

    private final NuocUongSanRepository nuocUongSanRepository;
    private final NguyenLieuRepository nguyenLieuRepository;

    public SanPhamService(
            NuocUongSanRepository nuocUongSanRepository,
            NguyenLieuRepository nguyenLieuRepository
    ) {
        this.nuocUongSanRepository = nuocUongSanRepository;
        this.nguyenLieuRepository = nguyenLieuRepository;
    }

    @Transactional(readOnly = true)
    public List<NuocUongSan> layDanhSachNuocUong() {
        return nuocUongSanRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<NuocUongSan> timNuocUongTheoTen(String ten) {
        return nuocUongSanRepository.findByTenContainingIgnoreCase(ten);
    }

    @Transactional(readOnly = true)
    public Optional<NuocUongSan> layNuocUongTheoId(UUID id) {
        return nuocUongSanRepository.findById(id);
    }

    @Transactional
    public NuocUongSan luuNuocUong(NuocUongSan nuocUongSan) {
        return nuocUongSanRepository.save(nuocUongSan);
    }

    @Transactional
    public void xoaNuocUong(UUID id) {
        nuocUongSanRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<NguyenLieu> layDanhSachNguyenLieu() {
        return nguyenLieuRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<NguyenLieu> timNguyenLieuTheoTen(String ten) {
        return nguyenLieuRepository.findByTenContainingIgnoreCase(ten);
    }

    @Transactional
    public NguyenLieu luuNguyenLieu(NguyenLieu nguyenLieu) {
        return nguyenLieuRepository.save(nguyenLieu);
    }

    @Transactional(readOnly = true)
    public Optional<NguyenLieu> layNguyenLieuTheoId(UUID id) {
        return nguyenLieuRepository.findById(id);
    }

    @Transactional
    public void xoaNguyenLieu(UUID id) {
        nguyenLieuRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<NguyenLieu> layNguyenLieuCanhBao() {
        return nguyenLieuRepository.findNguyenLieuCanhBao();
    }

    /** Tất cả nguyên liệu loại TOPPING (cho admin / công thức). */
    @Transactional(readOnly = true)
    public List<NguyenLieu> layDanhSachToppingNguyenLieu() {
        return nguyenLieuRepository.findAllNguyenLieuByLoaNguyenLieu(LoaiNguyenLieu.TOPPING);
    }

    private List<NguyenLieu> loadToppingOptions(NuocUongSan sp) {
        List<NguyenLieu> candidates = layDanhSachToppingNguyenLieu();
        List<UUID> allowedIds = parseUuidCsv(sp.getToppingChoPhep());
        if (allowedIds.isEmpty()) {
            return candidates;
        }
        Set<UUID> allow = new HashSet<>(allowedIds);
        return candidates.stream().filter(n -> allow.contains(n.getId())).toList();
    }

    private List<UUID> parseUuidCsv(String csv) {
        if (csv == null || csv.isBlank()) {
            return List.of();
        }

        String normalized = csv.trim();
        if (normalized.startsWith("[") && normalized.endsWith("]")) {
            normalized = normalized.substring(1, normalized.length() - 1);
        }
        normalized = normalized.replace("\"", "").replace("'", "");

        return Arrays.stream(normalized.split("[,;]"))
                .map(String::trim)
                .filter(v -> !v.isBlank())
                .map(this::safeParseUuid)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    private Optional<UUID> safeParseUuid(String value) {
        try {
            return Optional.of(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    @Transactional(readOnly = true)
    public Map<String, Object> layChiTietDayDu(UUID id) {

        NuocUongSan sp = nuocUongSanRepository.findByIdWithCongThucVaLuongNguyenLieu(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));

        Map<String, Object> data = new HashMap<>();

        data.put("sanPham", sp);
        data.put("size", List.of());
        data.put("da", List.of());
        data.put("topping", loadToppingOptions(sp));
        data.put("coTheTuyChinh", sp.isCoTheTuyChinh());
        data.put("congThuc", sp.getCongThucCoBan());

        List<Map<String, Object>> ingredientList = new ArrayList<>();
        if (sp.getCongThucCoBan() != null && sp.getCongThucCoBan().getLuongNguyenLieus() != null) {
            sp.getCongThucCoBan().getLuongNguyenLieus().forEach(l -> {
                Map<String, Object> row = new HashMap<>();
                row.put("id", l.getId());
                String tenNl = l.getNguyenLieu() != null ? l.getNguyenLieu().getTen() : "";
                row.put("ten", tenNl);
                row.put("tenNguyenLieu", tenNl);
                row.put("soLuong", l.getSoLuong());
                row.put("donVi", l.getDonVi());
                row.put("donGia", l.getNguyenLieu() != null ? l.getNguyenLieu().getGiaDonVi() : null);
                if (l.getNguyenLieu() != null && l.getNguyenLieu().getLoaiNguyenLieu() != null) {
                    row.put("loaiNguyenLieu", l.getNguyenLieu().getLoaiNguyenLieu().name());
                }
                ingredientList.add(row);
            });
        }
        data.put("nguyenLieu", ingredientList);

        return data;
    }
}

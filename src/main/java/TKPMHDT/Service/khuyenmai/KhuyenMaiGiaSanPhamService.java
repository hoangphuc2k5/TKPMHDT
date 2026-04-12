package TKPMHDT.Service.khuyenmai;

import TKPMHDT.DTO.response.NuocUongHienThiKhachHang;
import TKPMHDT.Entity.khuyenmai.KhuyenMaiGiaSanPham;
import TKPMHDT.Entity.khuyenmai.enums.LoaiGiamGiaEnum;
import TKPMHDT.Entity.khuyenmai.enums.PhamViKhuyenMaiGia;
import TKPMHDT.Entity.sanpham.NuocUongSan;
import TKPMHDT.Repository.khuyenmai.KhuyenMaiGiaSanPhamRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KhuyenMaiGiaSanPhamService {

    private final KhuyenMaiGiaSanPhamRepository repository;

    public KhuyenMaiGiaSanPhamService(KhuyenMaiGiaSanPhamRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<KhuyenMaiGiaSanPham> tatCa() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<KhuyenMaiGiaSanPham> tatCaDayDu() {
        List<KhuyenMaiGiaSanPham> list = repository.findAllWithRelations();
        list.sort(Comparator.comparing(
                        KhuyenMaiGiaSanPham::getThoiGianBatDau,
                        Comparator.nullsLast(Comparator.naturalOrder()))
                .reversed());
        return list;
    }

    @Transactional(readOnly = true)
    public Optional<KhuyenMaiGiaSanPham> timTheoIdDayDu(UUID id) {
        return repository.findByIdWithRelations(id);
    }

    @Transactional(readOnly = true)
    public List<KhuyenMaiGiaSanPham> dangHieuLucTai(LocalDateTime luc) {
        return repository.findHieuLucTai(luc);
    }

    /** Đơn giá đồ uống (chưa topping) sau KM tốt nhất cho khách tại thời điểm hiện tại. */
    @Transactional(readOnly = true)
    public BigDecimal donGiaCoSoSauKhuyenMai(NuocUongSan nuoc) {
        return donGiaCoSoSauKhuyenMai(nuoc, LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public BigDecimal donGiaCoSoSauKhuyenMai(NuocUongSan nuoc, LocalDateTime luc) {
        if (nuoc == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal goc = nuoc.getGia() != null ? nuoc.getGia() : BigDecimal.ZERO;
        return giaTotNhatSauKhuyenMai(nuoc, goc, luc);
    }

    @Transactional(readOnly = true)
    public NuocUongHienThiKhachHang toHienThi(NuocUongSan nuoc, LocalDateTime luc) {
        if (nuoc == null) {
            return null;
        }
        BigDecimal goc = nuoc.getGia() != null ? nuoc.getGia() : BigDecimal.ZERO;
        Win win = timUuDaiTotNhat(nuoc, goc, luc);
        BigDecimal sau = win.giaSau();
        boolean km = sau.compareTo(goc) < 0;
        return NuocUongHienThiKhachHang.builder()
                .id(nuoc.getId())
                .ten(nuoc.getTen())
                .gia(goc)
                .giaSauKhuyenMai(sau)
                .moTa(nuoc.getMoTa())
                .danhMuc(nuoc.getDanhMuc())
                .hinhAnh(nuoc.getHinhAnh() != null ? new java.util.ArrayList<>(nuoc.getHinhAnh()) : new java.util.ArrayList<>())
                .dangKinhDoanh(nuoc.getDangKinhDoanh())
                .dangKhuyenMai(km)
                .nhanKhuyenMai(km ? win.nhan() : null)
                .build();
    }

    private BigDecimal giaTotNhatSauKhuyenMai(NuocUongSan nuoc, BigDecimal goc, LocalDateTime luc) {
        return timUuDaiTotNhat(nuoc, goc, luc).giaSau();
    }

    private Win timUuDaiTotNhat(NuocUongSan nuoc, BigDecimal goc, LocalDateTime luc) {
        if (goc.compareTo(BigDecimal.ZERO) <= 0) {
            return new Win(goc, null, null);
        }
        List<KhuyenMaiGiaSanPham> ds = repository.findHieuLucTai(luc);
        BigDecimal best = goc;
        KhuyenMaiGiaSanPham bestKm = null;
        for (KhuyenMaiGiaSanPham km : ds) {
            if (!apDungChoSanPham(km, nuoc)) {
                continue;
            }
            BigDecimal discounted = tinhGiaSauGiam(goc, km);
            if (discounted.compareTo(best) < 0) {
                best = discounted;
                bestKm = km;
            }
        }
        if (bestKm == null) {
            return new Win(goc, null, null);
        }
        return new Win(best, bestKm, goc);
    }

    private record Win(BigDecimal giaSau, KhuyenMaiGiaSanPham km, BigDecimal goc) {
        String nhan() {
            if (km == null || goc == null) {
                return "Khuyến mãi";
            }
            if (km.getLoaiGiam() == LoaiGiamGiaEnum.PHAN_TRAM && km.getGiaTri() != null) {
                return "-" + km.getGiaTri().stripTrailingZeros().toPlainString() + "%";
            }
            if (km.getLoaiGiam() == LoaiGiamGiaEnum.SO_TIEN_CO_DINH && km.getGiaTri() != null) {
                return "Giảm " + formatVnd(km.getGiaTri());
            }
            return "Khuyến mãi";
        }
    }

    private static String formatVnd(BigDecimal v) {
        DecimalFormatSymbols sym = DecimalFormatSymbols.getInstance(new Locale("vi", "VN"));
        DecimalFormat df = new DecimalFormat("#,###", sym);
        return df.format(v.setScale(0, RoundingMode.HALF_UP)) + "₫";
    }

    public boolean apDungChoSanPham(KhuyenMaiGiaSanPham km, NuocUongSan nuoc) {
        if (km == null || nuoc == null || km.getPhamVi() == null) {
            return false;
        }
        return switch (km.getPhamVi()) {
            case MOT_SAN_PHAM -> km.getSanPhamDon() != null && km.getSanPhamDon().getId().equals(nuoc.getId());
            case NHIEU_SAN_PHAM -> km.getSanPhams() != null
                    && km.getSanPhams().stream().anyMatch(s -> s.getId().equals(nuoc.getId()));
            case DANH_MUC -> {
                String dmNuoc = nuoc.getDanhMuc();
                String dmKm = km.getDanhMuc();
                yield dmNuoc != null
                        && dmKm != null
                        && !dmNuoc.isBlank()
                        && dmNuoc.trim().equalsIgnoreCase(dmKm.trim());
            }
        };
    }

    public static BigDecimal tinhGiaSauGiam(BigDecimal goc, KhuyenMaiGiaSanPham km) {
        if (goc == null || km == null || km.getLoaiGiam() == null || km.getGiaTri() == null) {
            return goc != null ? goc : BigDecimal.ZERO;
        }
        if (km.getLoaiGiam() == LoaiGiamGiaEnum.PHAN_TRAM) {
            BigDecimal pt = km.getGiaTri().max(BigDecimal.ZERO).min(new BigDecimal("100"));
            BigDecimal sau = goc.multiply(BigDecimal.ONE.subtract(pt.divide(new BigDecimal("100"), 8, RoundingMode.HALF_UP)))
                    .setScale(2, RoundingMode.HALF_UP);
            return sau.max(BigDecimal.ZERO).min(goc);
        }
        if (km.getLoaiGiam() == LoaiGiamGiaEnum.SO_TIEN_CO_DINH) {
            return goc.subtract(km.getGiaTri()).max(BigDecimal.ZERO);
        }
        return goc;
    }

    @Transactional
    public KhuyenMaiGiaSanPham luu(KhuyenMaiGiaSanPham entity) {
        validate(entity);
        return repository.save(entity);
    }

    @Transactional
    public void xoa(UUID id) {
        repository.deleteById(id);
    }

    public void validate(KhuyenMaiGiaSanPham k) {
        Objects.requireNonNull(k.getPhamVi(), "phamVi");
        Objects.requireNonNull(k.getLoaiGiam(), "loaiGiam");
        if (k.getGiaTri() == null || k.getGiaTri().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Giá trị giảm không hợp lệ");
        }
        if (k.getThoiGianBatDau() == null || k.getThoiGianKetThuc() == null) {
            throw new IllegalArgumentException("Cần thời gian bắt đầu và kết thúc");
        }
        if (k.getThoiGianKetThuc().isBefore(k.getThoiGianBatDau())) {
            throw new IllegalArgumentException("Thời gian kết thúc phải sau hoặc bằng bắt đầu");
        }
        if (k.getLoaiGiam() == LoaiGiamGiaEnum.MUA_X_TANG_Y) {
            throw new IllegalArgumentException("Loại MUA_X_TANG_Y không áp dụng cho khuyến mãi giá");
        }
        if (k.getLoaiGiam() == LoaiGiamGiaEnum.PHAN_TRAM && k.getGiaTri().compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Phần trăm giảm không được vượt 100%");
        }
        switch (k.getPhamVi()) {
            case MOT_SAN_PHAM -> {
                if (k.getSanPhamDon() == null) {
                    throw new IllegalArgumentException("Chọn một sản phẩm");
                }
                k.setSanPhams(new java.util.HashSet<>());
                k.setDanhMuc(null);
            }
            case NHIEU_SAN_PHAM -> {
                if (k.getSanPhams() == null || k.getSanPhams().isEmpty()) {
                    throw new IllegalArgumentException("Chọn ít nhất một sản phẩm");
                }
                k.setSanPhamDon(null);
                k.setDanhMuc(null);
            }
            case DANH_MUC -> {
                if (k.getDanhMuc() == null || k.getDanhMuc().isBlank()) {
                    throw new IllegalArgumentException("Nhập danh mục áp dụng");
                }
                k.setSanPhamDon(null);
                k.setSanPhams(new java.util.HashSet<>());
            }
            default -> throw new IllegalArgumentException("Phạm vi không hợp lệ");
        }
    }
}

package TKPMHDT.Service.khuyenmai;

import TKPMHDT.DTO.request.TinhTienGiamGioHangRequest;
import TKPMHDT.Entity.giohang.ChiTietGioHang;
import TKPMHDT.Entity.khuyenmai.MaGiamGia;
import TKPMHDT.Entity.khuyenmai.enums.LoaiGiamGiaEnum;
import TKPMHDT.Entity.sanpham.NuocUongSan;
import TKPMHDT.Repository.khuyenmai.MaGiamGiaRepository;
import TKPMHDT.Repository.sanpham.NuocUongSanRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KhuyenMaiService {

    private final MaGiamGiaRepository maGiamGiaRepository;
    private final NuocUongSanRepository nuocUongSanRepository;

    public KhuyenMaiService(MaGiamGiaRepository maGiamGiaRepository, NuocUongSanRepository nuocUongSanRepository) {
        this.maGiamGiaRepository = maGiamGiaRepository;
        this.nuocUongSanRepository = nuocUongSanRepository;
    }

    @Transactional(readOnly = true)
    public Optional<MaGiamGia> timTheoMa(String ma) {
        if (ma == null || ma.isBlank()) {
            return Optional.empty();
        }
        return maGiamGiaRepository.findByMaIgnoreCase(ma.trim());
    }

    /**
     * Mã tồn tại, đang kích hoạt và hợp lệ theo ngày (khoảng + danh sách ngày nếu có).
     */
    @Transactional(readOnly = true)
    public Optional<MaGiamGia> timMaHopLe(String ma, LocalDate ngay) {
        return timTheoMa(ma).filter(mg -> maCoHieuLucTai(mg, ngay));
    }

    @Transactional(readOnly = true)
    public boolean maCoHieuLucTai(MaGiamGia mg, LocalDate ngay) {
        if (mg == null || !mg.isKichHoat()) {
            return false;
        }
        if (mg.getNgayBatDau() != null && ngay.isBefore(mg.getNgayBatDau())) {
            return false;
        }
        if (mg.getNgayKetThuc() != null && ngay.isAfter(mg.getNgayKetThuc())) {
            return false;
        }
        if (mg.getCacNgayApDung() != null && !mg.getCacNgayApDung().isEmpty() && !mg.getCacNgayApDung().contains(ngay)) {
            return false;
        }
        return true;
    }

    /**
     * Áp dụng cho dòng này hay không (theo toàn hệ thống / SP cụ thể / danh mục).
     */
    @Transactional(readOnly = true)
    public boolean nuocUongDuocApDung(MaGiamGia mg, NuocUongSan sp) {
        if (mg == null || sp == null) {
            return false;
        }
        if (mg.isApDungToanHeThong()) {
            return true;
        }
        boolean coSanPham = mg.getSanPhamApDung() != null && !mg.getSanPhamApDung().isEmpty();
        boolean coDanhMuc = mg.getDanhMucApDung() != null && !mg.getDanhMucApDung().isEmpty();
        if (!coSanPham && !coDanhMuc) {
            return false;
        }
        if (coSanPham && sanPhamTrongDanhSach(mg, sp)) {
            return true;
        }
        return coDanhMuc && danhMucKhop(mg, sp);
    }

    private boolean sanPhamTrongDanhSach(MaGiamGia mg, NuocUongSan sp) {
        UUID id = sp.getId();
        return mg.getSanPhamApDung().stream().anyMatch(s -> s.getId().equals(id));
    }

    private boolean danhMucKhop(MaGiamGia mg, NuocUongSan sp) {
        String dm = sp.getDanhMuc();
        if (dm == null || dm.isBlank()) {
            return false;
        }
        String normalized = dm.trim();
        return mg.getDanhMucApDung().stream()
                .filter(Objects::nonNull)
                .anyMatch(d -> d.trim().equalsIgnoreCase(normalized));
    }

    @Transactional(readOnly = true)
    public BigDecimal tongTienHangDuocApDung(MaGiamGia mg, List<ChiTietGioHang> items) {
        if (mg == null || items == null) {
            return BigDecimal.ZERO;
        }
        return items.stream()
                .filter(i -> i.getNuocUong() != null && nuocUongDuocApDung(mg, i.getNuocUong()))
                .map(ChiTietGioHang::getThanhTien)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional(readOnly = true)
    public BigDecimal tongTienDuocApDungTuCacDong(MaGiamGia mg, List<TinhTienGiamGioHangRequest.DongGioTinhGiam> dong) {
        if (mg == null || dong == null || dong.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal sum = BigDecimal.ZERO;
        for (TinhTienGiamGioHangRequest.DongGioTinhGiam d : dong) {
            if (d == null || d.nuocUongId() == null || d.thanhTien() == null) {
                continue;
            }
            NuocUongSan sp = nuocUongSanRepository.findById(d.nuocUongId()).orElse(null);
            if (sp != null && nuocUongDuocApDung(mg, sp)) {
                sum = sum.add(d.thanhTien());
            }
        }
        return sum;
    }

    @Transactional(readOnly = true)
    public List<MaGiamGia> danhSachMaGiamGia() {
        return maGiamGiaRepository.findAll();
    }

    @Transactional
    public MaGiamGia luuMaGiamGia(MaGiamGia maGiamGia) {
        return maGiamGiaRepository.save(maGiamGia);
    }

    @Transactional(readOnly = true)
    public BigDecimal tinhTienGiam(MaGiamGia maGiamGia, BigDecimal tongTien) {
        if (maGiamGia == null || tongTien == null || tongTien.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        if (maGiamGia.getLoaiGiam() == LoaiGiamGiaEnum.PHAN_TRAM) {
            BigDecimal phanTram = maGiamGia.getGiaTri();
            BigDecimal tienGiam = tongTien
                    .multiply(phanTram)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            return tienGiam.min(tongTien);
        }

        if (maGiamGia.getLoaiGiam() == LoaiGiamGiaEnum.SO_TIEN_CO_DINH) {
            return maGiamGia.getGiaTri().min(tongTien);
        }

        return BigDecimal.ZERO;
    }
}

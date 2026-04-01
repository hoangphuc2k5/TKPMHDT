package TKPMHDT.Service.donhang;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import TKPMHDT.Entity.donhang.ChiTietDonHang;
import TKPMHDT.Entity.donhang.DonHang;
import TKPMHDT.Entity.donhang.trangthai.TrangThaiChoXacNhan;
import TKPMHDT.Entity.giohang.ChiTietGioHang;
import TKPMHDT.Entity.giohang.GioHang;
import TKPMHDT.Entity.khuyenmai.MaGiamGia;
import TKPMHDT.Entity.nguoidung.DiaChi;
import TKPMHDT.Entity.nguoidung.NguoiDung;
import TKPMHDT.Entity.sanpham.NuocUongSan;
import TKPMHDT.Repository.donhang.DonHangRepository;
import TKPMHDT.Repository.giohang.GioHangRepository;
import TKPMHDT.Repository.nguoidung.DiaChiRepository;
import TKPMHDT.Repository.nguoidung.NguoiDungRepository;
import TKPMHDT.Repository.sanpham.NuocUongSanRepository;
import TKPMHDT.Service.khuyenmai.KhuyenMaiService;

@Service
public class DonHangService {

    private final DonHangRepository donHangRepository;
    private final GioHangRepository gioHangRepository;
    private final KhuyenMaiService khuyenMaiService;
    private final NguoiDungRepository nguoiDungRepository;
    private final DiaChiRepository diaChiRepository;
    private final NuocUongSanRepository nuocUongSanRepository;

    public DonHangService(
            DonHangRepository donHangRepository,
            GioHangRepository gioHangRepository,
            KhuyenMaiService khuyenMaiService,
            NguoiDungRepository nguoiDungRepository,
            DiaChiRepository diaChiRepository,
            NuocUongSanRepository nuocUongSanRepository
    ) {
        this.donHangRepository = donHangRepository;
        this.gioHangRepository = gioHangRepository;
        this.khuyenMaiService = khuyenMaiService;
        this.nguoiDungRepository = nguoiDungRepository;
        this.diaChiRepository = diaChiRepository;
        this.nuocUongSanRepository = nuocUongSanRepository;
    }

    @Transactional
    public DonHang taoDonHangTuGioHang(UUID khachHangId, UUID diaChiId, String maGiamGiaCode) {
        GioHang gioHang = gioHangRepository.findByKhachHangId(khachHangId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy giỏ hàng"));

        if (gioHang.getCacMatHang().isEmpty()) {
            throw new IllegalStateException("Giỏ hàng đang rỗng");
        }

        DiaChi diaChi = diaChiRepository.findById(diaChiId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy địa chỉ"));

        if (!diaChi.getKhachHang().getId().equals(khachHangId)) {
            throw new SecurityException("Địa chỉ không thuộc về khách hàng này");
        }

        Optional<MaGiamGia> maGiamGiaOpt = maGiamGiaCode == null || maGiamGiaCode.isBlank()
                ? Optional.empty()
                : khuyenMaiService.timTheoMa(maGiamGiaCode);

        BigDecimal tongTienGoc = gioHang.getTongTien();
        BigDecimal tienGiam = khuyenMaiService.tinhTienGiam(maGiamGiaOpt.orElse(null), tongTienGoc);
        BigDecimal tongThanhToan = tongTienGoc.subtract(tienGiam).max(BigDecimal.ZERO);

        DonHang donHang = DonHang.builder()
                .khachHang(gioHang.getKhachHang())
                .ngayDat(LocalDateTime.now())
                .trangThai(new TrangThaiChoXacNhan()) // State Pattern
                .tongTien(tongThanhToan)
                .maGiamGia(maGiamGiaOpt.orElse(null))
                .diaChiGiaoHang(diaChi)
                .chiTietDonHangs(new ArrayList<>())
                .build();

        List<ChiTietDonHang> chiTietDonHangs = new ArrayList<>();
        for (ChiTietGioHang item : gioHang.getCacMatHang()) {
            ChiTietDonHang chiTiet = ChiTietDonHang.builder()
                    .donHang(donHang)
                    .soLuong(item.getSoLuong())
                    .nuocUong(item.getNuocUong())
                    .tuyChinh(item.getTuyChinh())
                    .thanhTien(item.getThanhTien())
                    .build();
            chiTietDonHangs.add(chiTiet);
        }
        donHang.setChiTietDonHangs(chiTietDonHangs);

        DonHang saved = donHangRepository.save(donHang);

        gioHang.getCacMatHang().clear();
        gioHang.setTongTien(BigDecimal.ZERO);
        gioHangRepository.save(gioHang);

        return saved;
    }

    @Transactional
    public DonHang xacNhanDonHang(UUID donHangId) {
        DonHang donHang = donHangRepository.findById(donHangId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));
        donHang.xacNhan();
        return donHangRepository.save(donHang);
    }

    @Transactional
    public DonHang giaoDonHang(UUID donHangId) {
        DonHang donHang = donHangRepository.findById(donHangId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));
        donHang.giaoHang();
        return donHangRepository.save(donHang);
    }

    @Transactional
    public DonHang hoanThanhDonHang(UUID donHangId) {
        DonHang donHang = donHangRepository.findById(donHangId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));
        donHang.hoanThanh();
        return donHangRepository.save(donHang);
    }

    @Transactional
    public DonHang huyDonHang(UUID donHangId) {
        DonHang donHang = donHangRepository.findById(donHangId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));
        donHang.huyDon();
        return donHangRepository.save(donHang);
    }

    @Transactional(readOnly = true)
    public List<DonHang> layDonHangTheoKhachHang(UUID khachHangId) {
        return donHangRepository.findByKhachHangId(khachHangId);
    }

    @Transactional(readOnly = true)
    public Optional<DonHang> layTheoId(UUID donHangId) {
        return donHangRepository.findById(donHangId);
    }

    @Transactional(readOnly = true)
    public List<DonHang> layDonHangCuaToi(String tenDangNhap) {
        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(tenDangNhap)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay tai khoan"));
        return donHangRepository.findByKhachHangId(nguoiDung.getId());
    }

    @Transactional(readOnly = true)
    public List<DonHang> layTatCaDonHang() {
        return donHangRepository.findAll();
    }

    @Transactional
    public DonHang taoDonHangTaiQuay(String tenKhachHang, String soDienThoai, java.util.List<Map<String, Object>> chiTietItems) {
        if (chiTietItems == null || chiTietItems.isEmpty()) {
            throw new IllegalArgumentException("Đơn hàng phải có ít nhất một sản phẩm");
        }

        DonHang donHang = DonHang.builder()
                .khachHang(null) // POS orders may not have a customer
                .ngayDat(LocalDateTime.now())
                .trangThai(new TrangThaiChoXacNhan())
                .tongTien(BigDecimal.ZERO)
                .chiTietDonHangs(new ArrayList<>())
                .build();

        BigDecimal tongTien = BigDecimal.ZERO;
        List<ChiTietDonHang> chiTiets = new ArrayList<>();

        for (Map<String, Object> item : chiTietItems) {
            String nuocUongIdStr = item.get("nuocUongId").toString();
            UUID nuocUongId = UUID.fromString(nuocUongIdStr);
            Integer soLuong = Integer.parseInt(item.get("soLuong").toString());
            BigDecimal thanhTien = new BigDecimal(item.get("thanhTien").toString());
            
            // Fetch the product to ensure it exists and get its details
            NuocUongSan nuocUong = nuocUongSanRepository.findById(nuocUongId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm dengan ID: " + nuocUongId));
            
            ChiTietDonHang chiTiet = ChiTietDonHang.builder()
                    .donHang(donHang)
                    .soLuong(soLuong)
                    .nuocUong(nuocUong)  // ✅ Set the product reference
                    .thanhTien(thanhTien)
                    .build();
            chiTiets.add(chiTiet);
            tongTien = tongTien.add(thanhTien);
        }

        donHang.setChiTietDonHangs(chiTiets);
        donHang.setTongTien(tongTien);

        return donHangRepository.save(donHang);
    }

    // Các phương thức khác giữ nguyên...
}


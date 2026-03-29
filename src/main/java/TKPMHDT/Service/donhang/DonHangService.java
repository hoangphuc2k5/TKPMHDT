package TKPMHDT.Service.donhang;

import TKPMHDT.Entity.donhang.ChiTietDonHang;
import TKPMHDT.Entity.donhang.DonHang;
import TKPMHDT.Entity.donhang.enums.TrangThaiDonHangEnum;
import TKPMHDT.Entity.giohang.ChiTietGioHang;
import TKPMHDT.Entity.giohang.GioHang;
import TKPMHDT.Entity.khuyenmai.MaGiamGia;
import TKPMHDT.Entity.nguoidung.KhachHang;
import TKPMHDT.Entity.nguoidung.NguoiDung;
import TKPMHDT.Repository.donhang.DonHangRepository;
import TKPMHDT.Repository.giohang.GioHangRepository;
import TKPMHDT.Repository.nguoidung.KhachHangRepository;
import TKPMHDT.Repository.nguoidung.NguoiDungRepository;
import TKPMHDT.Repository.sanpham.NuocUongSanRepository;
import TKPMHDT.Service.khuyenmai.KhuyenMaiService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DonHangService {

    private final DonHangRepository donHangRepository;
    private final GioHangRepository gioHangRepository;
    private final KhuyenMaiService khuyenMaiService;
    private final NguoiDungRepository nguoiDungRepository;
    private final KhachHangRepository khachHangRepository;
    private final NuocUongSanRepository nuocUongSanRepository;

    public DonHangService(
            DonHangRepository donHangRepository,
            GioHangRepository gioHangRepository,
            KhuyenMaiService khuyenMaiService,
            NguoiDungRepository nguoiDungRepository,
            KhachHangRepository khachHangRepository,
            NuocUongSanRepository nuocUongSanRepository
    ) {
        this.donHangRepository = donHangRepository;
        this.gioHangRepository = gioHangRepository;
        this.khuyenMaiService = khuyenMaiService;
        this.nguoiDungRepository = nguoiDungRepository;
        this.khachHangRepository = khachHangRepository;
        this.nuocUongSanRepository = nuocUongSanRepository;
    }

    @Transactional
    public DonHang taoDonHangTuGioHang(UUID khachHangId, String maGiamGiaCode) {
        GioHang gioHang = gioHangRepository.findByKhachHangId(khachHangId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay gio hang"));

        if (gioHang.getCacMatHang().isEmpty()) {
            throw new IllegalStateException("Gio hang dang rong");
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
                .trangThai(TrangThaiDonHangEnum.CHO_XAC_NHAN)
                .tongTien(tongThanhToan)
                .maGiamGia(maGiamGiaOpt.orElse(null))
                .chiTietDonHangs(new ArrayList<>())
                .build();

        List<ChiTietDonHang> chiTietDonHangs = new ArrayList<>();
        for (ChiTietGioHang item : gioHang.getCacMatHang()) {
            ChiTietDonHang chiTiet = ChiTietDonHang.builder()
                    .donHang(donHang)
                    .soLuong(item.getSoLuong())
                    .nuocUong(item.getNuocUong())
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
    public DonHang capNhatTrangThai(UUID donHangId, TrangThaiDonHangEnum trangThaiMoi) {
        DonHang donHang = donHangRepository.findById(donHangId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay don hang"));
        donHang.setTrangThai(trangThaiMoi);
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

    @Transactional
    public DonHang xuLyDonTaiQuay(UUID khachHangId, UUID nuocUongId, int soLuong) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException("So luong phai lon hon 0");
        }

        KhachHang khachHang = khachHangRepository.findById(khachHangId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay khach hang"));

        var nuocUong = nuocUongSanRepository.findById(nuocUongId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay san pham"));

        BigDecimal thanhTien = nuocUong.getGia().multiply(BigDecimal.valueOf(soLuong));

        DonHang donHang = DonHang.builder()
                .khachHang(khachHang)
                .ngayDat(LocalDateTime.now())
                .trangThai(TrangThaiDonHangEnum.DA_XAC_NHAN)
                .tongTien(thanhTien)
                .build();

        ChiTietDonHang chiTiet = ChiTietDonHang.builder()
                .donHang(donHang)
                .soLuong(soLuong)
                .nuocUong(nuocUong)
                .thanhTien(thanhTien)
                .build();
        donHang.setChiTietDonHangs(List.of(chiTiet));
        return donHangRepository.save(donHang);
    }

    @Transactional(readOnly = true)
    public String inHoaDon(UUID donHangId) {
        DonHang donHang = donHangRepository.findById(donHangId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay don hang"));
        return "HOA DON\nMa don: " + donHang.getId()
                + "\nKhach: " + donHang.getKhachHang().getTenDangNhap()
                + "\nTong tien: " + donHang.getTongTien()
                + "\nTrang thai: " + donHang.getTrangThai();
    }

    @Transactional(readOnly = true)
    public String inPhieuGiao(UUID donHangId) {
        DonHang donHang = donHangRepository.findById(donHangId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay don hang"));
        return "PHIEU GIAO HANG\nMa don: " + donHang.getId()
                + "\nKhach nhan: " + donHang.getKhachHang().getTenDangNhap()
                + "\nTrang thai hien tai: " + donHang.getTrangThai();
    }
}


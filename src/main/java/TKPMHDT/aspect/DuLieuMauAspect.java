package TKPMHDT.aspect;

import TKPMHDT.Entity.khuyenmai.MaGiamGia;
import TKPMHDT.Entity.khuyenmai.enums.LoaiGiamGiaEnum;
import TKPMHDT.Entity.nguoidung.KhachHang;
import TKPMHDT.Entity.nguoidung.QuanTriVien;
import TKPMHDT.Entity.nguoidung.enums.VaiTro;
import TKPMHDT.Entity.sanpham.CongThuc;
import TKPMHDT.Entity.sanpham.NguyenLieu;
import TKPMHDT.Entity.sanpham.NuocUongSan;
import TKPMHDT.Repository.khuyenmai.MaGiamGiaRepository;
import TKPMHDT.Repository.nguoidung.KhachHangRepository;
import TKPMHDT.Repository.nguoidung.NguoiDungRepository;
import TKPMHDT.Repository.nguoidung.QuanTriVienRepository;
import TKPMHDT.Repository.sanpham.CongThucRepository;
import TKPMHDT.Repository.sanpham.NguyenLieuRepository;
import TKPMHDT.Repository.sanpham.NuocUongSanRepository;
import java.math.BigDecimal;
import java.util.HashSet;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DuLieuMauAspect implements CommandLineRunner {

    private final NguoiDungRepository nguoiDungRepository;
    private final KhachHangRepository khachHangRepository;
    private final QuanTriVienRepository quanTriVienRepository;
    private final NguyenLieuRepository nguyenLieuRepository;
    private final CongThucRepository congThucRepository;
    private final NuocUongSanRepository nuocUongSanRepository;
    private final MaGiamGiaRepository maGiamGiaRepository;

    public DuLieuMauAspect(
            NguoiDungRepository nguoiDungRepository,
            KhachHangRepository khachHangRepository,
            QuanTriVienRepository quanTriVienRepository,
            NguyenLieuRepository nguyenLieuRepository,
            CongThucRepository congThucRepository,
            NuocUongSanRepository nuocUongSanRepository,
            MaGiamGiaRepository maGiamGiaRepository
    ) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.khachHangRepository = khachHangRepository;
        this.quanTriVienRepository = quanTriVienRepository;
        this.nguyenLieuRepository = nguyenLieuRepository;
        this.congThucRepository = congThucRepository;
        this.nuocUongSanRepository = nuocUongSanRepository;
        this.maGiamGiaRepository = maGiamGiaRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        taoNguoiDungMau();
        taoSanPhamMau();
        taoKhuyenMaiMau();
    }

    private void taoNguoiDungMau() {
        if (!nguoiDungRepository.existsByTenDangNhap("admin")) {
            QuanTriVien admin = QuanTriVien.builder()
                    .tenDangNhap("admin")
                    .email("admin@tkpmhdt.local")
                    .matKhauHash("admin123")
                    .vaiTro(VaiTro.QUAN_TRI_VIEN)
                    .trangThaiHoatDong(true)
                    .build();
            quanTriVienRepository.save(admin);
        }

        if (!nguoiDungRepository.existsByTenDangNhap("khach1")) {
            KhachHang khachHang = KhachHang.builder()
                    .tenDangNhap("khach1")
                    .email("khach1@tkpmhdt.local")
                    .matKhauHash("khach123")
                    .vaiTro(VaiTro.KHACH_HANG)
                    .trangThaiHoatDong(true)
                    .build();
            khachHangRepository.save(khachHang);
        }
    }

    private void taoSanPhamMau() {
        if (nuocUongSanRepository.count() > 0) {
            return;
        }

        NguyenLieu traDen = nguyenLieuRepository.save(NguyenLieu.builder()
                .ten("Tra den")
                .donVi("ml")
                .soLuongTon(new BigDecimal("10000"))
                .giaDonVi(new BigDecimal("0.20"))
                .build());

        NguyenLieu sua = nguyenLieuRepository.save(NguyenLieu.builder()
                .ten("Sua tuoi")
                .donVi("ml")
                .soLuongTon(new BigDecimal("8000"))
                .giaDonVi(new BigDecimal("0.35"))
                .build());

        CongThuc traSuaCt = congThucRepository.save(CongThuc.builder()
                .ten("Cong thuc tra sua truyen thong")
                .moTa("Tra den + sua tuoi + duong")
                .giaCoBan(new BigDecimal("28000"))
                .build());

        NuocUongSan traSua = NuocUongSan.builder()
                .ten("Tra sua truyen thong")
                .gia(new BigDecimal("30000"))
                .moTa("Tra sua vi truyen thong")
                .congThucCoBan(traSuaCt)
                .coTheTuyChinh(true)
                .build();
        traSua.setNguyenLieuSuDung(new HashSet<>());
        traSua.getNguyenLieuSuDung().add(traDen);
        traSua.getNguyenLieuSuDung().add(sua);
        nuocUongSanRepository.save(traSua);
    }

    private void taoKhuyenMaiMau() {
        if (maGiamGiaRepository.existsByMa("GIAM10")) {
            return;
        }

        MaGiamGia maGiamGia = MaGiamGia.builder()
                .ma("GIAM10")
                .loaiGiam(LoaiGiamGiaEnum.PHAN_TRAM)
                .giaTri(new BigDecimal("10"))
                .build();

        maGiamGiaRepository.save(maGiamGia);
    }
}


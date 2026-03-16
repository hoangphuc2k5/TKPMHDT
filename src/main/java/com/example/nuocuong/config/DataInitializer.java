package com.example.nuocuong.config;

import com.example.nuocuong.entity.*;
import com.example.nuocuong.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final NguoiDungRepository nguoiDungRepository;
    private final QuanTriVienRepository quanTriVienRepository;
    private final NhanVienBanHangRepository nhanVienBanHangRepository;
    private final QuanLyKhoRepository quanLyKhoRepository;
    private final KhachHangRepository khachHangRepository;
    private final NuocUongSanRepository nuocUongSanRepository;
    private final NguyenLieuRepository nguyenLieuRepository;
    private final MaGiamGiaRepository maGiamGiaRepository;
    private final CongThucRepository congThucRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        // Kiểm tra nếu chưa có người dùng nào thì mới khởi tạo
        if (nguoiDungRepository.count() == 0) {
            initUsers();
            initProducts();
            initCoupons();
            initFormulas();
            System.out.println(">>> Dữ liệu mẫu đã được khởi tạo tự động thành công!");
        } else {
            System.out.println(">>> Database đã có dữ liệu, bỏ qua bước khởi tạo mẫu.");
        }
    }

    private void initUsers() {
        String defaultPassword = passwordEncoder.encode("password123");

        // Admin
        QuanTriVien admin = QuanTriVien.builder()
                .tenDangNhap("admin")
                .matKhau(defaultPassword)
                .hoTen("Quản Trị Viên")
                .email("admin@drinkstore.com")
                .soDienThoai("0901234567")
                .diaChi("123 Admin Street")
                .vaiTro(Role.ADMIN)
                .quyenHan("FULL_ACCESS")
                .build();
        quanTriVienRepository.save(admin);

        // Staff
        NhanVienBanHang staff = NhanVienBanHang.builder()
                .tenDangNhap("staff")
                .matKhau(defaultPassword)
                .hoTen("Nguyễn Văn Bán")
                .email("staff@drinkstore.com")
                .soDienThoai("0901112222")
                .diaChi("456 Sales Road")
                .vaiTro(Role.NHAN_VIEN_BAN_HANG)
                .maNhanVien("NV001")
                .luong(10000000.0)
                .soDonDaBan(0)
                .build();
        nhanVienBanHangRepository.save(staff);

        // Warehouse Manager
        QuanLyKho warehouse = QuanLyKho.builder()
                .tenDangNhap("warehouse")
                .matKhau(defaultPassword)
                .hoTen("Trần Văn Kho")
                .email("warehouse@drinkstore.com")
                .soDienThoai("0903334444")
                .diaChi("789 Stock Ave")
                .vaiTro(Role.QUAN_LY_KHO)
                .maNhanVien("NV002")
                .luong(12000000.0)
                .khuVucQuanLy("Khu vực A")
                .build();
        quanLyKhoRepository.save(warehouse);

        // Customer
        KhachHang customer = KhachHang.builder()
                .tenDangNhap("customer")
                .matKhau(defaultPassword)
                .hoTen("Lê Văn Khách")
                .email("customer@gmail.com")
                .soDienThoai("0905556666")
                .diaChi("101 Customer Lane")
                .vaiTro(Role.KHACH_HANG)
                .maKhachHang("KH001")
                .diemTichLuy(100)
                .build();
        khachHangRepository.save(customer);
    }

    private void initProducts() {
        // Nước uống sẵn
        NuocUongSan traSua = NuocUongSan.builder()
                .ten("Trà Sữa Trân Châu")
                .moTa("Trà sữa truyền thống kèm trân châu đen dai giòn")
                .gia(35000.0)
                .hinhAnh("https://images.unsplash.com/photo-1576092768241-dec231879fc3?w=400")
                .dungTich("500ml")
                .loaiNuoc("Trà sữa")
                .build();
        nuocUongSanRepository.save(traSua);

        NuocUongSan cafeMuoi = NuocUongSan.builder()
                .ten("Cà Phê Muối")
                .moTa("Hương vị cà phê đậm đà kết hợp lớp kem muối béo ngậy")
                .gia(45000.0)
                .hinhAnh("https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=400")
                .dungTich("350ml")
                .loaiNuoc("Cà phê")
                .build();
        nuocUongSanRepository.save(cafeMuoi);

        // Nguyên liệu
        NguyenLieu matcha = NguyenLieu.builder()
                .ten("Bột Trà Xanh")
                .moTa("Bột trà xanh Matcha Nhật Bản")
                .gia(150000.0)
                .donViTinh("kg")
                .soLuongTon(10.5)
                .nguongCanhBao(2.0)
                .hanSuDung(LocalDate.of(2026, 12, 31))
                .loHang("MATCH-2026")
                .build();
        nguyenLieuRepository.save(matcha);

        NguyenLieu suaTuoi = NguyenLieu.builder()
                .ten("Sữa Tươi Không Đường")
                .moTa("Sữa tươi nguyên chất Dalat Milk")
                .gia(25000.0)
                .donViTinh("lít")
                .soLuongTon(50.0)
                .nguongCanhBao(10.0)
                .hanSuDung(LocalDate.of(2026, 4, 15))
                .loHang("MILK-001")
                .build();
        nguyenLieuRepository.save(suaTuoi);
    }

    private void initCoupons() {
        MaGiamGia welcome = MaGiamGia.builder()
                .ma("WELCOME2026")
                .moTa("Giảm 10% cho khách hàng mới")
                .giaTriGiam(10.0)
                .loaiGiamGia(LoaiGiamGia.PHAN_TRAM)
                .ngayBatDau(LocalDateTime.now())
                .ngayKetThuc(LocalDateTime.now().plusMonths(12))
                .soLuongSuDungToiDa(1000)
                .soLuongDaSuDung(0)
                .build();
        maGiamGiaRepository.save(welcome);
    }

    private void initFormulas() {
        NguyenLieu matcha = nguyenLieuRepository.findAll().stream().filter(n -> n.getTen().equals("Bột Trà Xanh")).findFirst().orElse(null);
        NguyenLieu suaTuoi = nguyenLieuRepository.findAll().stream().filter(n -> n.getTen().equals("Sữa Tươi Không Đường")).findFirst().orElse(null);

        if (matcha != null && suaTuoi != null) {
            CongThuc ct = CongThuc.builder()
                    .ten("Công thức Matcha Latte")
                    .moTa("Matcha nguyên chất kết hợp sữa tươi")
                    .build();

            LuongNguyenLieu lnl1 = LuongNguyenLieu.builder()
                    .congThuc(ct)
                    .nguyenLieu(matcha)
                    .soLuong(0.02)
                    .ghiChu("20g bột matcha")
                    .build();

            LuongNguyenLieu lnl2 = LuongNguyenLieu.builder()
                    .congThuc(ct)
                    .nguyenLieu(suaTuoi)
                    .soLuong(0.2)
                    .ghiChu("200ml sữa tươi")
                    .build();

            ct.setDanhSachNguyenLieu(List.of(lnl1, lnl2));
            congThucRepository.save(ct);
        }
    }
}

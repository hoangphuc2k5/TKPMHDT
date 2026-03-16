package com.example.nuocuong.config;

import com.example.nuocuong.entity.KhachHang;
import com.example.nuocuong.entity.LoaiMaGiamGia;
import com.example.nuocuong.entity.LoaiSanPham;
import com.example.nuocuong.entity.MaGiamGia;
import com.example.nuocuong.entity.NguyenLieu;
import com.example.nuocuong.entity.NhanVienBanHang;
import com.example.nuocuong.entity.NhanVienGiaoHang;
import com.example.nuocuong.entity.NuocUongSan;
import com.example.nuocuong.entity.QuanLyKho;
import com.example.nuocuong.entity.QuanTriVien;
import com.example.nuocuong.entity.VaiTro;
import com.example.nuocuong.repository.KhachHangRepository;
import com.example.nuocuong.repository.MaGiamGiaRepository;
import com.example.nuocuong.repository.NguoiDungRepository;
import com.example.nuocuong.repository.NguyenLieuRepository;
import com.example.nuocuong.repository.NhanVienRepository;
import com.example.nuocuong.repository.NuocUongSanRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 1 FILE seed data cho tất cả role.
 *
 * - Tạo sẵn user đăng nhập cho KHACH_HANG và QUAN_TRI_VIEN (bảng nguoi_dung, dùng Spring Security).
 * - Tạo sẵn dữ liệu nhân viên cho 3 role staff (bảng nhan_vien).
 * - Seed thêm sản phẩm + mã giảm giá để UI có dữ liệu demo.
 *
 * Best practice:
 * - Idempotent: chạy nhiều lần không tạo trùng (kiểm tra theo email/maNhanVien/ma).
 */
@Configuration
public class DataSeeder {
	@Bean
	CommandLineRunner seedAllRoles(
		NguoiDungRepository nguoiDungRepository,
		KhachHangRepository khachHangRepository,
		NhanVienRepository nhanVienRepository,
		NuocUongSanRepository nuocUongSanRepository,
		NguyenLieuRepository nguyenLieuRepository,
		MaGiamGiaRepository maGiamGiaRepository,
		PasswordEncoder passwordEncoder
	) {
		return args -> {
			// ===== USERS (có thể đăng nhập) =====
			seedAdmin(nguoiDungRepository, passwordEncoder);
			seedCustomer(khachHangRepository, passwordEncoder);

			// ===== STAFF (dữ liệu mẫu theo entity nhân viên) =====
			seedStaff(nhanVienRepository);

			// ===== PRODUCTS =====
			seedProducts(nuocUongSanRepository, nguyenLieuRepository);

			// ===== DISCOUNTS =====
			seedDiscounts(maGiamGiaRepository);
		};
	}

	private void seedAdmin(NguoiDungRepository repo, PasswordEncoder encoder) {
		String email = "admin@nuocuong.local";
		if (repo.existsByEmail(email)) return;

		QuanTriVien admin = QuanTriVien.builder()
			.email(email)
			.matKhauMaHoa(encoder.encode("123456"))
			.vaiTro(VaiTro.QUAN_TRI_VIEN)
			.kichHoat(true)
			.hoTen("Quản trị viên")
			.build();

		repo.save(admin);
	}

	private void seedCustomer(KhachHangRepository repo, PasswordEncoder encoder) {
		String email = "customer@nuocuong.local";
		if (repo.existsByEmail(email)) return;

		KhachHang kh = KhachHang.builder()
			.email(email)
			.matKhauMaHoa(encoder.encode("123456"))
			.vaiTro(VaiTro.KHACH_HANG)
			.kichHoat(true)
			.hoTen("Khách hàng demo")
			.soDienThoai("0900000000")
			.diaChiMacDinh("TP.HCM")
			.build();

		repo.save(kh);
	}

	private void seedStaff(NhanVienRepository repo) {
		if (repo.findByMaNhanVien("BH001").isEmpty()) {
			repo.save(NhanVienBanHang.builder()
				.maNhanVien("BH001")
				.hoTen("Nhân viên bán hàng")
				.soDienThoai("0911111111")
				.dangLamViec(true)
				.build());
		}

		if (repo.findByMaNhanVien("KHO001").isEmpty()) {
			repo.save(QuanLyKho.builder()
				.maNhanVien("KHO001")
				.hoTen("Quản lý kho")
				.soDienThoai("0922222222")
				.dangLamViec(true)
				.build());
		}

		if (repo.findByMaNhanVien("GH001").isEmpty()) {
			repo.save(NhanVienGiaoHang.builder()
				.maNhanVien("GH001")
				.hoTen("Nhân viên giao hàng")
				.soDienThoai("0933333333")
				.dangLamViec(true)
				.build());
		}
	}

	private void seedProducts(NuocUongSanRepository nuocRepo, NguyenLieuRepository nlRepo) {
		if (nuocRepo.count() == 0) {
			nuocRepo.save(NuocUongSan.builder()
				.ten("Trà sữa truyền thống")
				.moTa("Trà sữa béo nhẹ, dễ uống.")
				.giaBan(BigDecimal.valueOf(35000))
				.loai(LoaiSanPham.NUOC_UONG_SAN)
				.hinhAnhUrl("https://picsum.photos/seed/trasua/640/480")
				.dangKinhDoanh(true)
				.build());

			nuocRepo.save(NuocUongSan.builder()
				.ten("Cà phê sữa đá")
				.moTa("Đậm vị cà phê, thơm béo.")
				.giaBan(BigDecimal.valueOf(30000))
				.loai(LoaiSanPham.NUOC_UONG_SAN)
				.hinhAnhUrl("https://picsum.photos/seed/caphe/640/480")
				.dangKinhDoanh(true)
				.build());
		}

		if (nlRepo.count() == 0) {
			nlRepo.save(NguyenLieu.builder()
				.ten("Trân châu đen")
				.moTa("Topping trân châu dai.")
				.giaBan(BigDecimal.valueOf(5000))
				.loai(LoaiSanPham.NGUYEN_LIEU)
				.soLuongTon(BigDecimal.valueOf(10000))
				.donViTinh("g")
				.dangKinhDoanh(true)
				.build());

			nlRepo.save(NguyenLieu.builder()
				.ten("Sữa tươi")
				.moTa("Nguyên liệu sữa tươi.")
				.giaBan(BigDecimal.valueOf(8000))
				.loai(LoaiSanPham.NGUYEN_LIEU)
				.soLuongTon(BigDecimal.valueOf(5000))
				.donViTinh("ml")
				.dangKinhDoanh(true)
				.build());
		}
	}

	private void seedDiscounts(MaGiamGiaRepository repo) {
		if (repo.findByMa("SALE10").isEmpty()) {
			repo.save(MaGiamGia.builder()
				.ma("SALE10")
				.moTa("Giảm 10% cho đơn từ 50k")
				.loai(LoaiMaGiamGia.PHAN_TRAM)
				.giaTri(BigDecimal.valueOf(10))
				.donToiThieu(BigDecimal.valueOf(50000))
				.kichHoat(true)
				.batDauLuc(LocalDateTime.now().minusDays(1))
				.ketThucLuc(LocalDateTime.now().plusDays(30))
				.build());
		}

		if (repo.findByMa("FREESHIP5K").isEmpty()) {
			repo.save(MaGiamGia.builder()
				.ma("FREESHIP5K")
				.moTa("Giảm 5.000đ")
				.loai(LoaiMaGiamGia.SO_TIEN_CO_DINH)
				.giaTri(BigDecimal.valueOf(5000))
				.donToiThieu(BigDecimal.ZERO)
				.kichHoat(true)
				.batDauLuc(LocalDateTime.now().minusDays(1))
				.ketThucLuc(LocalDateTime.now().plusDays(30))
				.build());
		}
	}
}


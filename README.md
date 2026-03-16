# Dự án Website Bán Nước Uống (Spring Boot + Thymeleaf)

## 1) Quan hệ Entity (tóm tắt)

### Nhóm người dùng (SINGLE_TABLE inheritance)
- **`NguoiDung` (cha)** → `KhachHang`, `QuanTriVien`
  - `NguoiDung` có `email`, `matKhauMaHoa`, `vaiTro`, `trangThai`, ...
- **`NhanVien` (cha)** → `NhanVienBanHang`, `QuanLyKho`, `NhanVienGiaoHang`
  - `NhanVien` có `maNhanVien`, `hoTen`, `soDienThoai`, `trangThai`, ...

### Nhóm sản phẩm (SINGLE_TABLE inheritance)
- **`SanPham` (cha)** → `NuocUongSan`, `NguyenLieu`
  - `NuocUongSan`: đồ uống bán sẵn.
  - `NguyenLieu`: nguyên liệu dùng để pha chế (quản lý tồn kho).

### Giỏ hàng & đơn hàng
- `KhachHang` 1–1 `GioHang`
- `GioHang` 1–N `ChiTietGioHang`
- `ChiTietGioHang` N–1 `SanPham`
- `DonHang` 1–N `ChiTietDonHang`
- `ChiTietDonHang` N–1 `SanPham`
- `ChiTietDonHang` 1–0..1 `TuyChinhKhachHang` (tùy chỉnh: mức đường/đá/topping…)
- `DonHang` 1–1 `ThanhToan`
- `DonHang` 0..1–1 `MaGiamGia`

### Công thức & định lượng nguyên liệu
- `CongThuc` N–1 `NuocUongSan` (mỗi đồ uống bán sẵn có công thức)
- `CongThuc` 1–N `LuongNguyenLieu`
- `LuongNguyenLieu` N–1 `NguyenLieu`

## 2) Áp dụng Design Pattern (bắt buộc)

### Layered Architecture + Repository/Service/DTO
- **Controller**: chỉ nhận/validate request, gọi service, trả view/model.
- **Service (interface + impl)**: chứa toàn bộ business logic. **Chỉ làm việc bằng DTO**; entity chỉ tồn tại bên trong service để map.
- **Repository**: chỉ query DB (`JpaRepository`, `@Query`, `Specification`…), **không chứa logic nghiệp vụ**.
- **DTO + ModelMapper**: mọi dữ liệu trao đổi giữa tầng dùng DTO; map Entity ↔ DTO tại service.

### Factory Pattern — `CustomDrinkFactory`
- Mục tiêu: tạo “nước uống tùy chỉnh” từ `NuocUongSan` + `TuyChinhKhachHang`.
- Factory encapsulate việc kết hợp lựa chọn khách hàng (đường/đá/topping) để tạo ra đối tượng “đồ uống đặt” dùng trong `ChiTietDonHang`.

### Strategy Pattern — Thanh toán & mã giảm giá
- `PaymentStrategy`: `CodPaymentStrategy`, `OnlineMockPaymentStrategy` (giả lập).
- `DiscountStrategy`: giảm theo %, giảm số tiền cố định, điều kiện đơn tối thiểu.
- Service chọn strategy theo loại thanh toán / loại mã giảm giá.

### Builder Pattern — dựng `DonHang`, `ChiTietDonHang`
- Khi checkout, dữ liệu cần kết hợp: giỏ hàng, thông tin nhận hàng, tùy chỉnh, mã giảm giá, thanh toán…
- `DonHangBuilder`/`ChiTietDonHangBuilder` giúp dựng object “phức tạp” theo từng bước, đảm bảo SRP và dễ test.

### SOLID (điểm nhấn SRP + DIP)
- SRP: mỗi class chỉ 1 trách nhiệm (OTP service, payment strategy, discount strategy, factory, builder…).
- DIP: Service phụ thuộc vào interface (`PaymentStrategy`, `DiscountStrategy`, repository interfaces), inject qua constructor.

## 3) Chạy dự án (gợi ý)
- Cập nhật `application.properties` cho đúng SQL Server của bạn (`username/password`, `databaseName`).
- Build WAR bằng Maven (nếu đã cài Maven): `mvn clean package`.


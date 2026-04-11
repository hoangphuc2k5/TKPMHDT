# TKPMHDT - Hệ Thống Quản Lý Nước Uống (Drink Management System)

## Mô Tả Dự Án

TKPMHDT là một ứng dụng web e-commerce chuyên biệt xây dựng bằng **Spring Boot 3.1.5** dành cho quản lý và bán hàng nước uống. Hệ thống hỗ trợ tùy chỉnh sản phẩm theo yêu cầu khách hàng, quản lý kho nguyên liệu, xử lý đơn hàng, thanh toán online, quản lý khuyến mại và cung cấp giao diện quản trị cho nhân viên.

## Công Nghệ Sử Dụng

- **Framework**: Spring Boot 3.1.5
- **Ngôn Ngữ**: Java 21
- **Cơ Sở Dữ Liệu**: SQL Server / MySQL
- **ORM**: JPA/Hibernate
- **Template Engine**: Thymeleaf
- **Security**: Spring Security + JWT
- **Build Tool**: Maven
- **Quản Lý Tài Khoản**: Session-based & JWT Token

---

## Cơ Sở Dữ Liệu

### Sơ Đồ Thực Thể - Mối Quan Hệ (ER Diagram)

#### 1. **Độc Lập Chính (Independent Tables)**

| Bảng | Mô Tả | Unicode Field |
|------|-------|---------|
| `nguyen_lieu` | Danh sách nguyên liệu thô | ✓ |
| `cong_thuc` | Công thức pha chế nước uống | ✓ |
| `san_pham_hinh_anh` | Hình ảnh sản phẩm | - |
| `ma_giam_gia` | Mã giảm giá/Khuyến mại | ✓ |

#### 2. **Sản Phẩm (Product Hierarchy - Inheritance)**

```
san_pham (Base Class)
    ├── nuoc_uong_san (Nước uống bán)
    └── (Có thể mở rộng cho sản phẩm khác)
```

| Bảng | Cột Chính | Mô Tả |
|------|-----------|-------|
| `san_pham` | `id`, `ten`, `gia`, `mo_ta` | Bảng cha cho tất cả sản phẩm |
| `nuoc_uong_san` | `cong_thuc_id`, `co_the_tuy_chinh` | Nước uống với công thức cơ bản, có/không tùy chỉnh |
| `nuoc_uong_san_nguyen_lieu` | Join table | Liên kết nước uống - nguyên liệu sử dụng |
| `luong_nguyen_lieu` | Số lượng nguyên liệu | Từng nguyên liệu trong công thức |

#### 3. **Người Dùng (Users - Single Table Inheritance)**

```
nguoi_dung (Base User)
    ├── KHACH_HANG (Khách hàng)
    ├── QUAN_TRI_VIEN (Quản trị viên)
    └── NHAN_VIEN_BAN_HANG (Nhân viên bán hàng, JPA extends QuanTriVien)
```

| Bảng | Cột Chính | Mô Tả |
|------|-----------|-------|
| `nguoi_dung` | `id`, `ten_dang_nhap`, `email`, `vai_tro` | Bảng người dùng (SINGLE_TABLE) |
| `dia_chi` | `id`, `khach_hang_id` | Địa chỉ giao hàng của khách hàng |
| `password_reset_otp` | `otp_code`, `het_han_luc` | OTP đặt lại mật khẩu (TTL: 5 phút) |

#### 4. **Giỏ Hàng**

| Bảng | Cột Chính | Mô Tả |
|------|-----------|-------|
| `gio_hang` | `id`, `khach_hang_id` (1:1), `tong_tien` | Giỏ hàng của khách hàng |
| `chi_tiet_gio_hang` | `id`, `gio_hang_id`, `nuoc_uong_san_id`, `tuy_chinh` | Mặt hàng trong giỏ (có tùy chỉnh) |

#### 5. **Đơn Hàng**

| Bảng | Cột Chính | Mô Tả |
|------|-----------|-------|
| `don_hang` | `id`, `khach_hang_id`, `ngay_dat`, `trang_thai` | Đơn hàng chính |
| `chi_tiet_don_hang` | `id`, `don_hang_id`, `nuoc_uong_san_id`, `so_luong`, `tuy_chinh` | Chi tiết từng mặt hàng trong đơn |
| `ma_giam_gia_san_pham` | Join table | Liên kết mã giảm giá - sản phẩm áp dụng |

#### 6. **Thanh Toán & Hóa Đơn**

| Bảng | Cột Chính | Mô Tả |
|------|-----------|-------|
| `thanh_toan` | `id`, `don_hang_id` (1:1), `phuong_thuc`, `trang_thai` | Thông tin thanh toán |
| `hoa_don` | `id`, `don_hang_id` (1:1), `so_hoa_don`, `tien_giam` | Hóa đơn chính thức |
| `phieu_giao` | `id`, `don_hang_id` (1:1), `nhan_vien_giao_id` | Phiếu giao hàng |

#### 7. **Trạng Thái Đơn Hàng**

| Trạng Thái | Mô Tả | Enum Name |
|-----------|-------|-----------|
| CHO_XAC_NHAN | Chờ xác nhận | `TrangThaiChoXacNhan` |
| DA_XAC_NHAN | Đã xác nhận | `TrangThaiDaXacNhan` |
| DANG_GIAO | Đang giao | `TrangThaiDangGiao` |
| DA_GIAO | Đã giao | `TrangThaiDaGiao` |
| DA_HUY | Đã hủy | `TrangThaiDaHuy` |

#### 8. **Thanh Toán - Phương Thức & Trạng Thái**

**Phương Thức Thanh Toán:**
- `TIEN_MAT` (Cash)
- `VNPAY` (VNPay Gateway)
- `MOMO` (MoMo Wallet)

**Trạng Thái Thanh Toán:**
- `CHO_XU_LY` (Pending)
- `DA_THANH_TOAN` (Paid)
- `THAT_BAI` (Failed)

#### 9. **Khuyến Mại**

| Bảng | Cột Chính | Mô Tả |
|------|-----------|-------|
| `ma_giam_gia` | `ma` (unique), `loai_giam`, `gia_tri` | Mã: PHAN_TRAM (%) hoặc CO_DINH (VNĐ) |
| `ma_giam_gia_san_pham` | Join table | Áp dụng cho sản phẩm cụ thể |

#### 10. **Tùy Chỉnh Sản Phẩm (Embedded)**

```java
TuyChinhKhachHang {
    mucDuong: Integer      // Mức đường
    mucDa: Integer         // Mức đá
    ghiChu: String        // Ghi chú thêm
    nguyenLieuThem: List  // Nguyên liệu thêm
}
```

---

## Các Chức Năng Hiện Có

### 1. **Xác Thực & Phân Quyền**

#### UC1: Đăng Ký Tài Khoản
- **Service**: `DangKyService`
- **Chức năng**:
  - Đăng ký tài khoản khách hàng mới
  - Kiểm tra trùng lặp (username, email)
  - Mã hóa mật khẩu bằng BCrypt
  - Thiết lập vai trò mặc định: `KHACH_HANG`

#### UC2: Đăng Nhập
- **Service**: `DangNhapService`
- **Chức năng**:
  - Xác thực bằng username/email + password
  - Kiểm tra trạng thái hoạt động của tài khoản
  - Tạo session/JWT token

#### UC3: Đăng Xuất
- **Service**: `DangXuatService`
- **Chức năng**:
  - Xóa SecurityContext
  - Hủy session người dùng

#### UC4: Quên Mật Khẩu
- **Service**: `QuenMatKhauService`
- **Chức năng**:
  - Tạo OTP 6 chữ số (hết hạn 5 phút)
  - Gửi OTP qua email
  - Xác thực OTP và đặt lại mật khẩu
  - Đánh dấu OTP đã sử dụng

---

### 2. **Quản Lý Sản Phẩm**

#### UC5: Duyệt & Tìm Kiếm Sản Phẩm
- **Service**: `SanPhamService`
- **Chức năng**:
  - Lấy danh sách tất cả nước uống
  - Tìm kiếm theo tên (case-insensitive)
  - Lấy chi tiết sản phẩm theo ID
  - Hiển thị hình ảnh, giá, mô tả

#### UC6: Xem Chi Tiết Sản Phẩm
- **Controller**: `SanPhamController`
- **Kiến thức sản phẩm**:
  - Công thức cơ bản (CongThuc)
  - Nguyên liệu sử dụng
  - Khả năng tùy chỉnh
  - Giá cơ bản

#### UC7: Tùy Chỉnh Sản Phẩm (Advanced)
- **Service**: `TuyChinhSanPhamService`
- **Chức năng**:
  - Kiểm tra khả năng tùy chỉnh
  - Lấy danh sách nguyên liệu thêm
  - Tính giá với topping
  - Ghi chú tuỳ chỉnh:
    - Mức đường
    - Mức đá
    - Ghi chú đặc biệt

---

### 3. **Giỏ Hàng**

#### UC8: Quản Lý Giỏ Hàng
- **Service**: `GioHangService`
- **Chức năng**:
  - Tạo/Lấy giỏ hàng (1:1 với khách hàng)
  - Thêm mặt hàng (với thông tin tùy chỉnh)
  - Xóa mặt hàng
  - Cập nhật số lượng
  - Tính toán tổng tiền

**Controller**: `GioHangController`
- `GET /api/gio-hang/{khachHangId}` - Lấy giỏ hàng
- `POST /api/gio-hang/them-mat-hang` - Thêm mặt hàng
- `DELETE /api/gio-hang/{chiTietId}` - Xóa mặt hàng

---

### 4. **Đơn Hàng**

#### UC9: Tạo Đơn Hàng
- **Service**: `DonHangService`
- **Chức năng**:
  - Tạo đơn hàng từ giỏ hàng
  - Chọn địa chỉ giao hàng
  - Áp dụng mã giảm giá
  - Tính toán tổng tiền (giá - giảm giá)
  - Thiết lập trạng thái: `CHO_XAC_NHAN`

#### UC10: Theo Dõi Đơn Hàng
- **Controller**: `DonHangController`
- **Chức năng**:
  - Lấy chi tiết đơn hàng
  - Xem lịch sử trạng thái
  - Lấy danh sách đơn hàng của khách hàng

**Trạng Thái Chuyển Đổi:**
```
CHO_XAC_NHAN → DA_XAC_NHAN → DANG_GIAO → DA_GIAO
                   ↓
                DA_HUY (bất cứ lúc nào)
```

---

### 5. **Địa Chỉ Khách Hàng**

#### UC11: Quản Lý Địa Chỉ
- **Service**: `DiaChiService`
- **Chức năng**:
  - Thêm địa chỉ giao hàng
  - Lấy danh sách địa chỉ
  - Xóa địa chỉ
  - Lưu trữ:
    - Tên người nhận
    - Số điện thoại
    - Địa chỉ cụ thể
    - Phường/Xã, Quận/Huyện, Tỉnh/Thành phố

**Controller**: `DiaChiController`
- `POST /api/dia-chi/{khachHangId}` - Thêm
- `GET /api/dia-chi/{khachHangId}` - Lấy danh sách
- `DELETE /api/dia-chi/{diaChiId}` - Xóa

---

### 6. **Quản Lý Tài Khoản Cá Nhân**

#### UC12: Tài Khoản Người Dùng
- **Service**: `NguoiDungService`
- **Chức năng**:
  - Xem thông tin tài khoản
  - Cập nhật hồ sơ (họ tên, điện thoại)
  - Tải lên avatar
  - Thay đổi mật khẩu

**Controller**: `NguoiDungController`
- `GET /api/nguoi-dung/{userId}` - Xem hồ sơ
- `PATCH /api/nguoi-dung/{userId}` - Cập nhật
- `POST /api/nguoi-dung/upload-avatar` - Tải avatar

---

### 7. **Thanh Toán**

#### UC13: Xử Lý Thanh Toán
- **Service**: `ThanhToanService`
- **Chức năng**:
  - Tạo bản ghi thanh toán từ đơn hàng
  - Hỗ trợ 3 phương thức:
    - Tiền mặt (TIEN_MAT)
    - VNPay (VNPAY)
    - MoMo (MOMO)
  - Theo dõi trạng thái: CHO_XU_LY → DA_THANH_TOAN / THAT_BAI
  - Ghi nhận thời gian thanh toán

**Controller**: `ThanhToanController`
- `POST /api/thanh-toan` - Tạo thanh toán
- `PATCH /api/thanh-toan/{thanhToanId}/trang-thai` - Cập nhật trạng thái
- Hỗ trợ callback từ VNPAY/MoMo

---

### 8. **Khuyến Mại & Mã Giảm Giá**

#### UC14: Quản Lý Khuyến Mại
- **Service**: `KhuyenMaiService`
- **Chức năng**:
  - Tạo mã giảm giá
  - 2 loại giảm:
    - **PHAN_TRAM**: Giảm theo % (ví dụ: 10%)
    - **CO_DINH**: Giảm số tiền cố định (ví dụ: 50.000 VNĐ)
  - Áp dụng cho sản phẩm cụ thể hoặc toàn bộ
  - Tính toán tiền giảm tự động

#### UC15: Sử Dụng Mã Giảm Giá
- Nhập mã khi tạo đơn hàng
- Tính lại tổng tiền tự động

**Controller**: `KhuyenMaiController`
- `GET /api/khuyen-mai/ma/{ma}` - Tìm mã
- `GET /api/khuyen-mai` - Danh sách (Admin only)
- `POST /api/khuyen-mai` - Tạo mới (Admin only)

---

### 9. **Quản Trị Hệ Thống**

#### UC16: Quản Lý Sản Phẩm & Nguyên Liệu
- **Service**: `SanPhamService`, `TuyChinhSanPhamService`
- **Chức năng**:
  - Thêm/Sửa/Xóa sản phẩm
  - Quản lý công thức
  - Quản lý nguyên liệu
  - Quản lý hình ảnh
  - Kiểm soát giá

#### UC17: Quản Lý Kho
- Theo dõi tồn kho nguyên liệu
- Cảnh báo hết hàng
- Nhập kho nguyên liệu

#### UC18: Quản Lý Đơn Hàng
- Xem tất cả đơn hàng
- Xác nhận đơn
- Cập nhật trạng thái
- Quản lý phiếu giao

#### UC19: Quản Lý Khách Hàng
- Xem danh sách khách hàng
- Xem lịch sử mua hàng
- Kích hoạt/khóa tài khoản
- Quản lý địa chỉ của khách hàng

#### UC20: Quản Lý Khuyến Mại
- Tạo/Sửa/Xóa mã giảm giá
- Xem hiệu quả từng mã
- Đặt thời gian hết hạn

#### UC21: Báo Cáo Doanh Thu
- Thống kê doanh thu theo ngày/tháng
- Sản phẩm bán chạy
- Tỷ suất lợi nhuận

#### UC22: Quản Lý Nhân Viên
- Thêm/Sửa/Xóa nhân viên
- Phân công vai trò (hệ thống chỉ còn 3 `VaiTro`): `KHACH_HANG`, `NHAN_VIEN_BAN_HANG`, `QUAN_TRI_VIEN`. Form admin chỉ tạo nhân viên bán hàng; tài khoản quản trị viên tạo qua seed/cấu hình.

**Controller**: `AdminController`
- Protected: `@PreAuthorize("hasRole('QUAN_TRI_VIEN')")`
- Tất cả UC16-UC22 đều ở `/api/admin`

---

### 10. **Giao Diện Web**

#### UC23: Trang Chủ & Duyệt Sản Phẩm
- **Controller**: `PageController`
- **View**: `index.html`
- Hiển thị danh sách nước uống

#### UC24: Chi Tiết Sản Phẩm
- **View**: `sanpham-chi-tiet.html`
- Công thức, giá, hình ảnh, tùy chỉnh

#### UC25: Giỏ Hàng Web
- **View**: `giohang.html` (cơ bản)
- **View**: `giohang-nang-cao.html` (tùy chỉnh)
- Thêm/xóa mặt hàng, xem tổng tiền

#### UC26: Thanh Toán Web
- **View**: `thanhtoan.html`
- Chọn phương thức thanh toán
- Xác nhận thông tin

#### UC27: Theo Dõi Đơn Hàng Web
- **View**: `theo-doi-don-hang.html`
- Xem trạng thái, chi tiết đơn hàng

#### UC28: Quản Lý Tài Khoản Web
- **View**: `tai-khoan.html`
- Hồ sơ, địa chỉ, lịch sử mua hàng

#### UC29: Giao Diện Admin Web
- **View**: `admin/dashboard.html` - Tổng quan
- **View**: `admin/manage-products.html` - Quản lý sản phẩm
- **View**: `admin/manage-customers.html` - Quản lý khách hàng
- **View**: `admin/manage-staff.html` - Quản lý nhân viên

#### UC30: Giao Diện POS (Point of Sale)
- **View**: `pos/pos-interface.html`
- Bán hàng tại quầy
- Chọn sản phẩm, tùy chỉnh, thanh toán nhanh

---

## Các Services Chính

| Service | Chức Năng |
|---------|----------|
| `DangKyService` | Đăng ký tài khoản |
| `DangNhapService` | Xác thực người dùng |
| `DangXuatService` | Đăng xuất |
| `QuenMatKhauService` | Quên/đặt lại mật khẩu |
| `NguoiDungService` | Quản lý người dùng |
| `DiaChiService` | Quản lý địa chỉ |
| `SanPhamService` | Quản lý sản phẩm |
| `TuyChinhSanPhamService` | Tùy chỉnh sản phẩm |
| `GioHangService` | Quản lý giỏ hàng |
| `DonHangService` | Quản lý đơn hàng |
| `ThanhToanService` | Xử lý thanh toán |
| `KhuyenMaiService` | Quản lý khuyến mại |
| `EmailService` | Gửi email OTP |
| `FileStorageService` | Lưu trữ file upload |
| `CustomUserDetailsService` | Tải chi tiết người dùng (Spring Security) |

---

## Bảo Mật & Phân Quyền

### Vai Trò Người Dùng

```
VaiTro enum:
├── KHACH_HANG         // Khách hàng
├── NHAN_VIEN_BAN_HANG // Nhân viên bán hàng
└── QUAN_TRI_VIEN      // Quản trị viên (đầy đủ quyền vận hành / kho / nhân sự)
```

### Kiểm Soát Truy Cập

```java
@PreAuthorize("hasRole('KHACH_HANG')")      // Chỉ khách hàng
@PreAuthorize("hasRole('QUAN_TRI_VIEN')")   // Chỉ admin
@PreAuthorize("hasAnyRole('KHACH_HANG', 'NHAN_VIEN_BAN_HANG')")  // Nhiều vai trò
```

### Mã Hóa Mật Khẩu
- **Thuật toán**: BCrypt
- **Hash**: Được lưu trong `mat_khau_hash`
- **Xác thực**: Khi login, so sánh plaintext với hash

### OTP Đặt Lại Mật Khẩu
- **Độ dài**: 6 chữ số
- **Thời gian**: 5 phút hết hạn
- **Lưu trữ**: Bảng `password_reset_otp`
- **Gửi qua**: Email

---

## Cấu Trúc Project

```
src/main/java/TKPMHDT/
├── Application.java          # Điểm vào Spring Boot
├── ServletInitializer.java   # WAR deployment config
├── aspect/
│   └── DuLieuMauAspect.java  # AOP aspect cho dữ liệu
├── config/                   # Spring configuration
├── Controller/               # REST API + Web controllers (10 classes)
├── Entity/                   # JPA entities (41 files)
│   ├── donhang/              # Đơn hàng, chi tiết, hóa đơn, phiếu giao
│   ├── giohang/              # Giỏ hàng, chi tiết
│   ├── khuyenmai/            # Mã giảm giá + strategy pattern
│   ├── nguoidung/            # Người dùng + subclass, OTP
│   ├── sanpham/              # Sản phẩm, nước uống, nguyên liệu, công thức
│   └── thanhtoan/            # Thanh toán + strategy pattern (VNPay, MoMo, Cash)
├── Repository/               # JPA repositories (Spring Data)
├── Service/                  # Business logic
│   ├── donhang/
│   ├── giohang/
│   ├── khuyenmai/
│   ├── mail/                 # Email service
│   ├── nguoidung/
│   ├── sanpham/
│   └── thanhtoan/
├── security/                 # Spring Security config + CustomUserDetailsService
└── aspect/

src/main/resources/
├── application.properties     # Database, mail, app config
├── static/
│   ├── css/
│   ├── js/
│   └── uploads/              # Thư mục lưu file upload
└── templates/
    ├── index.html
    ├── sanpham*.html
    ├── giohang*.html
    ├── thanhtoan.html
    ├── theo-doi-don-hang.html
    ├── login.html / register.html
    ├── tai-khoan.html
    ├── admin/
    │   ├── dashboard.html
    │   ├── manage-*.html
    └── pos/
        └── pos-interface.html
```

---

## Hướng Dẫn Development

### 1. **Cấu Hình Database**

Chỉnh sửa `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/tkpmhdt
spring.datasource.username=root
spring.datasource.password=password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

### 2. **Chạy Application**

```bash
# Dùng Maven
mvn spring-boot:run

# Hoặc dùng WAR
mvn clean package
# Deploy WAR file lên Tomcat
```

### 3. **Tạo Dữ Liệu Test**

```sql
-- Tạo admin
INSERT INTO nguoi_dung VALUES (UUID(), 'admin', 'admin@example.com', BCRYPT('123456'), 'QUAN_TRI_VIEN', 'admin');

-- Tạo khách hàng
INSERT INTO nguoi_dung VALUES (UUID(), 'user1', 'user1@example.com', BCRYPT('123456'), 'KHACH_HANG', 'user1');

-- Tạo nguyên liệu
INSERT INTO nguyen_lieu VALUES (UUID(), 'Đường', 'Kg', 10, 5000);
INSERT INTO nguyen_lieu VALUES (UUID(), 'Đá', 'Kg', 5, 2000);

-- Tạo công thức
INSERT INTO cong_thuc VALUES (UUID(), 'Nước chanh', 'Nước chanh đơn giản', 15000);
```

---

## API Endpoints Chính

### Authentication
```
POST   /api/auth/dang-ky          - Đăng ký
POST   /api/auth/dang-nhap        - Đăng nhập
POST   /api/auth/dang-xuat        - Đăng xuất
POST   /api/auth/quen-mat-khau    - Quên mật khẩu
```

### Products
```
GET    /api/san-pham              - Danh sách sản phẩm
GET    /api/san-pham/{id}         - Chi tiết sản phẩm
```

### Shopping Cart
```
GET    /api/gio-hang/{userId}     - Lấy giỏ hàng
POST   /api/gio-hang/them-mat-hang - Thêm mặt hàng
DELETE /api/gio-hang/{itemId}      - Xóa mặt hàng
```

### Orders
```
POST   /api/don-hang/tao-tu-gio-hang - Tạo đơn từ giỏ
GET    /api/don-hang/{id}            - Chi tiết đơn hàng
```

### Address
```
GET    /api/dia-chi/{userId}      - Danh sách địa chỉ
POST   /api/dia-chi/{userId}      - Thêm địa chỉ
DELETE /api/dia-chi/{id}          - Xóa địa chỉ
```

### Payment
```
POST   /api/thanh-toan             - Tạo thanh toán
PATCH  /api/thanh-toan/{id}/trang-thai - Cập nhật trạng thái
```

### Promotions
```
GET    /api/khuyen-mai/ma/{ma}    - Tìm mã giảm giá
GET    /api/khuyen-mai            - Danh sách (Admin)
POST   /api/khuyen-mai            - Tạo (Admin)
```

### User Profile
```
GET    /api/nguoi-dung/{id}       - Xem hồ sơ
PATCH  /api/nguoi-dung/{id}       - Cập nhật hồ sơ
POST   /api/nguoi-dung/upload-avatar - Tải avatar
```

### Admin
```
GET    /api/admin/...             - Quản lý chung hệ thống
```

---

## Lưu Ý Quan Trọng

1. **UUID**: Tất cả ID sử dụng UUID (CHAR(36))
2. **Unicode**: Dùng `nvarchar` cho văn bản tiếng Việt
3. **Decimal**: Giá dùng `DECIMAL(18,2)` cho độ chính xác
4. **Inheritance**: Sử dụng Single Table Inheritance cho Người Dùng
5. **Cascade**: OnHoaD hàng → ChiTietDonHang có `CASCADE`
6. **Soft Delete**: Test nếu cần (chưa implement)
7. **JWT**: Token-based hoặc Session-based tùy config

---

## Phát Triển Tiếp Theo

- [ ] Notification (Email, SMS, Push)
- [ ] Analytics Dashboard
- [ ] Mobile App (Flutter/React Native)
- [ ] Advanced Search Filters
- [ ] Recommendation Engine
- [ ] Customer Loyalty Points
- [ ] Inventory Alerts
- [ ] Multi-language Support (i18n)
- [ ] API Documentation (Swagger/SpringDoc)
- [ ] Unit Tests & Integration Tests

---

## Liên Hệ & Hỗ Trợ

**Phiên bản**: 1.0.0  
**Java**: 21  
**Spring Boot**: 3.1.5  
**Cập nhật**: Tháng 4, 2026

---

*Tài liệu được tạo cho mục đích phát triển nội bộ.*

# Dự án Website Bán Nước Uống (Spring Boot + HTML/JS)

## Giới thiệu
Đây là một dự án hoàn chỉnh cho website bán nước uống, hỗ trợ cả đồ uống sẵn và đồ uống tùy chỉnh. Dự án được xây dựng với kiến trúc RESTful API sử dụng Spring Boot và giao diện người dùng thuần HTML/CSS/JS.

## Công nghệ sử dụng
- **Backend**: Java 21, Spring Boot 3.3.5, Spring Data JPA, Spring Security, JWT, Lombok, SQL Server.
- **Frontend**: HTML5, CSS3, JavaScript (ES6+), Bootstrap 5.
- **Database**: Microsoft SQL Server.
- **Tài liệu API**: Swagger UI (SpringDoc OpenAPI 3).

## Cấu trúc dự án
- `src/main/java/com/example/nuocuong`: Mã nguồn Backend.
    - `config`: Cấu hình Security, JWT, CORS.
    - `controller`: Các API endpoint theo vai trò (Customer, Staff, Warehouse, Admin).
    - `entity`: Các thực thể cơ sở dữ liệu (NguoiDung, SanPham, DonHang, ...).
    - `service`: Logic nghiệp vụ.
    - `repository`: Giao tiếp với database.
    - `dto`: Data Transfer Objects.
    - `security`: JWT utilities và UserDetailsService.
- `src/main/resources/static`: Mã nguồn Frontend.
    - `customer`: Giao diện dành cho khách hàng.
    - `staff`: Giao diện dành cho nhân viên bán hàng.
    - `warehouse`: Giao diện dành cho quản lý kho.
    - `admin`: Giao diện dành cho quản trị viên.

## Hướng dẫn cài đặt
1.  **Cơ sở dữ liệu**: 
    - Cài đặt SQL Server.
    - Tạo database tên `NuocUongDB`.
    - Cập nhật thông tin đăng nhập (username, password) trong file `src/main/resources/application.yml`.
2.  **Chạy dự án**:
    - Sử dụng Maven: `mvn spring-boot:run`.
    - Hoặc chạy trực tiếp từ IDE (IntelliJ, Eclipse).
3.  **Truy cập**:
    - Trang chủ: `http://localhost:8080/index.html`.
    - Swagger UI: `http://localhost:8080/swagger-ui.html`.

## Tài khoản mẫu (Cần đăng ký hoặc import SQL)
- Mặc định sau khi chạy lần đầu, bạn có thể sử dụng API `/api/v1/auth/register` để tạo tài khoản Khách hàng.
- Để tạo tài khoản Admin/Staff, hãy thay đổi role trực tiếp trong database sau khi đăng ký.

## Các chức năng chính
### Khách hàng
- Đăng ký/Đăng nhập (JWT).
- Tìm kiếm và lọc sản phẩm.
- Xem chi tiết sản phẩm và công thức.
- Tùy chỉnh đồ uống.
- Quản lý giỏ hàng và thanh toán (mô phỏng).
- Theo dõi đơn hàng.

### Nhân viên bán hàng
- Quản lý đơn hàng.
- Cập nhật trạng thái đơn hàng.
- In hóa đơn HTML.

### Quản lý kho
- Quản lý nhập/xuất kho nguyên liệu.
- Cảnh báo tồn kho thấp.
- Quản lý lô hàng và hạn sử dụng.

### Quản trị viên
- Quản lý người dùng, sản phẩm, đơn hàng, khuyến mãi.
- Báo cáo doanh thu và thống kê hệ thống.

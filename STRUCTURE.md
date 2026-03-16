# Cấu trúc thư mục dự án (tóm tắt)

## Java packages

```
src/main/java/com/example/nuocuong
├── NuocUongApplication.java
├── ServletInitializer.java
├── config
│   ├── ModelMapperConfig.java
│   └── SecurityConfig.java
├── controller
│   ├── HomeController.java
│   ├── GlobalExceptionHandler.java
│   ├── khachhang
│   │   ├── AuthController.java
│   │   ├── ProductController.java
│   │   ├── CartController.java
│   │   └── OrderController.java
│   ├── nhanvienbanhang
│   │   └── NhanVienBanHangController.java
│   ├── quanlykho
│   │   └── QuanLyKhoController.java
│   ├── nhanviengiaohang
│   │   └── NhanVienGiaoHangController.java
│   └── quantri
│       └── AdminController.java
├── dto
│   └── (các DTO chính: auth/product/cart/order/payment/discount)
├── entity
│   └── (19 entity + enums)
├── exception
│   ├── BusinessException.java
│   └── NotFoundException.java
├── factory
│   └── CustomDrinkFactory.java
├── repository
│   └── (JpaRepository cho từng entity)
├── service
│   ├── (interfaces)
│   └── impl
│       ├── (service implementations)
│       └── builder
│           ├── DonHangBuilder.java
│           └── ChiTietDonHangBuilder.java
└── strategy
    └── (payment + discount strategies + StrategyRegistry)
```

## Thymeleaf templates (sẽ tạo đầy đủ ở Phần 10)

```
src/main/resources/templates
├── fragments/
├── auth/
├── customer/
├── admin/
├── staff/
└── error/
```


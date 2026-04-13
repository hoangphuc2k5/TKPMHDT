/*
  TKPMHDT — script nạp dữ liệu mẫu (SQL Server)
  ---------------------------------------------------------------------------
  - Chạy trên database: TKPMHDT (khớp application.properties).
  - Mật khẩu tài khoản mẫu: 123456
    Cột mat_khau_hash dùng tiền tố {noop} để khớp CompatiblePasswordEncoder
    (có thể đổi sang bcrypt sau khi đăng nhập lần đầu qua app).
  - UUID cố định để dễ tham chiếu khi debug; nếu bảng đã có cùng id/unique → bỏ qua hoặc xóa dữ liệu cũ.

  Thứ tự: vai_tro_quyen → nguoi_dung → dia_chi → nguyen_lieu → cong_thuc →
          luong_nguyen_lieu → san_pham → nuoc_uong_san → (hinh/M2M) →
          ma_giam_gia → khuyen_mai_gia_san_pham → gio_hang → chi_tiet_gio_hang →
          don_hang → chi_tiet_don_hang → thanh_toan → hoa_don
*/

SET NOCOUNT ON;
SET XACT_ABORT ON;

USE TKPMHDT;
GO

BEGIN TRANSACTION;

/* ========== 1. RBAC ========== */
IF NOT EXISTS (SELECT 1 FROM dbo.vai_tro_quyen WHERE vai_tro = N'KHACH_HANG')
    INSERT INTO dbo.vai_tro_quyen (id, vai_tro, quyen_csv)
    VALUES ('10000001-0000-4000-8000-000000000001', N'KHACH_HANG', N'');

IF NOT EXISTS (SELECT 1 FROM dbo.vai_tro_quyen WHERE vai_tro = N'NHAN_VIEN_BAN_HANG')
    INSERT INTO dbo.vai_tro_quyen (id, vai_tro, quyen_csv)
    VALUES ('10000001-0000-4000-8000-000000000002', N'NHAN_VIEN_BAN_HANG', N'');

IF NOT EXISTS (SELECT 1 FROM dbo.vai_tro_quyen WHERE vai_tro = N'QUAN_TRI_VIEN')
    INSERT INTO dbo.vai_tro_quyen (id, vai_tro, quyen_csv)
    VALUES ('10000001-0000-4000-8000-000000000003', N'QUAN_TRI_VIEN', N'');

/* ========== 2. Người dùng (single table + discriminator) ========== */
IF NOT EXISTS (SELECT 1 FROM dbo.nguoi_dung WHERE ten_dang_nhap = N'admin')
    INSERT INTO dbo.nguoi_dung (id, loai_nguoi_dung, ten_dang_nhap, email, mat_khau_hash, vai_tro, avatar_url, trang_thai_hoat_dong, ho_ten, so_dien_thoai)
    VALUES (
        '20000001-0000-4000-8000-000000000001',
        N'QUAN_TRI_VIEN',
        N'admin',
        N'admin@tkpmhdt.local',
        N'{noop}123456',
        N'QUAN_TRI_VIEN',
        NULL,
        1,
        NULL,
        NULL
    );

IF NOT EXISTS (SELECT 1 FROM dbo.nguoi_dung WHERE ten_dang_nhap = N'nhanvien')
    INSERT INTO dbo.nguoi_dung (id, loai_nguoi_dung, ten_dang_nhap, email, mat_khau_hash, vai_tro, avatar_url, trang_thai_hoat_dong, ho_ten, so_dien_thoai)
    VALUES (
        '20000001-0000-4000-8000-000000000002',
        N'NHAN_VIEN_BAN_HANG',
        N'nhanvien',
        N'nv@tkpmhdt.local',
        N'{noop}123456',
        N'NHAN_VIEN_BAN_HANG',
        NULL,
        1,
        NULL,
        NULL
    );

IF NOT EXISTS (SELECT 1 FROM dbo.nguoi_dung WHERE ten_dang_nhap = N'khachhang')
    INSERT INTO dbo.nguoi_dung (id, loai_nguoi_dung, ten_dang_nhap, email, mat_khau_hash, vai_tro, avatar_url, trang_thai_hoat_dong, ho_ten, so_dien_thoai)
    VALUES (
        '20000001-0000-4000-8000-000000000003',
        N'KHACH_HANG',
        N'khachhang',
        N'khach@tkpmhdt.local',
        N'{noop}123456',
        N'KHACH_HANG',
        NULL,
        1,
        N'Nguyễn Văn Khách',
        N'0909123456'
    );

/* ========== 3. Địa chỉ khách ========== */
IF NOT EXISTS (SELECT 1 FROM dbo.dia_chi WHERE id = '20000001-0000-4000-8000-000000000010')
    INSERT INTO dbo.dia_chi (id, khach_hang_id, ten_nguoi_nhan, so_dien_thoai, dia_chi_cu_the, phuong_xa, quan_huyen, tinh_thanh_pho, la_mac_dinh)
    VALUES (
        '20000001-0000-4000-8000-000000000010',
        '20000001-0000-4000-8000-000000000003',
        N'Nguyễn Văn Khách',
        N'0909123456',
        N'123 Đường Lê Lợi',
        N'Phường Bến Nghé',
        N'Quận 1',
        N'TP. Hồ Chí Minh',
        1
    );

/* ========== 4. Cấu hình hệ thống (tùy chọn) ========== */
IF NOT EXISTS (SELECT 1 FROM dbo.cau_hinh_he_thong WHERE config_key = N'shop.name')
    INSERT INTO dbo.cau_hinh_he_thong (id, config_key, config_value, mo_ta)
    VALUES (NEWID(), N'shop.name', N'Cửa hàng TKPMHDT Demo', N'Tên hiển thị');

IF NOT EXISTS (SELECT 1 FROM dbo.cau_hinh_he_thong WHERE config_key = N'shop.phone')
    INSERT INTO dbo.cau_hinh_he_thong (id, config_key, config_value, mo_ta)
    VALUES (NEWID(), N'shop.phone', N'02838221234', N'Hotline');

/* ========== 5. Nguyên liệu ========== */
IF NOT EXISTS (SELECT 1 FROM dbo.nguyen_lieu WHERE id = '30000001-0000-4000-8000-000000000001')
    INSERT INTO dbo.nguyen_lieu (id, ten, don_vi, so_luong_ton, gia_don_vi, nguong_canh_bao, gia, loai)
    VALUES (N'30000001-0000-4000-8000-000000000001', N'Trà đen', N'g', 5000.000, 0.05, 500.000, NULL, N'INGREDIENT');

IF NOT EXISTS (SELECT 1 FROM dbo.nguyen_lieu WHERE id = '30000001-0000-4000-8000-000000000002')
    INSERT INTO dbo.nguyen_lieu (id, ten, don_vi, so_luong_ton, gia_don_vi, nguong_canh_bao, gia, loai)
    VALUES (N'30000001-0000-4000-8000-000000000002', N'Sữa tươi', N'ml', 10000.000, 0.02, 1000.000, NULL, N'INGREDIENT');

IF NOT EXISTS (SELECT 1 FROM dbo.nguyen_lieu WHERE id = '30000001-0000-4000-8000-000000000003')
    INSERT INTO dbo.nguyen_lieu (id, ten, don_vi, so_luong_ton, gia_don_vi, nguong_canh_bao, gia, loai)
    VALUES (N'30000001-0000-4000-8000-000000000003', N'Đường mật', N'ml', 8000.000, 0.03, 800.000, NULL, N'INGREDIENT');

IF NOT EXISTS (SELECT 1 FROM dbo.nguyen_lieu WHERE id = '30000001-0000-4000-8000-000000000004')
    INSERT INTO dbo.nguyen_lieu (id, ten, don_vi, so_luong_ton, gia_don_vi, nguong_canh_bao, gia, loai)
    VALUES (N'30000001-0000-4000-8000-000000000004', N'Trân châu đen', N'g', 3000.000, 0.08, 300.000, NULL, N'TOPPING');

IF NOT EXISTS (SELECT 1 FROM dbo.nguyen_lieu WHERE id = '30000001-0000-4000-8000-000000000005')
    INSERT INTO dbo.nguyen_lieu (id, ten, don_vi, so_luong_ton, gia_don_vi, nguong_canh_bao, gia, loai)
    VALUES (N'30000001-0000-4000-8000-000000000005', N'Kem cheese', N'g', 2000.000, 0.15, 200.000, NULL, N'TOPPING');

/* ========== 6. Công thức ========== */
IF NOT EXISTS (SELECT 1 FROM dbo.cong_thuc WHERE id = '40000001-0000-4000-8000-000000000001')
    INSERT INTO dbo.cong_thuc (id, ten, mo_ta, gia_co_ban)
    VALUES (
        '40000001-0000-4000-8000-000000000001',
        N'CT Trà sữa truyền thống',
        N'Pha chế cơ bản',
        35000.00
    );

IF NOT EXISTS (SELECT 1 FROM dbo.cong_thuc WHERE id = '40000001-0000-4000-8000-000000000002')
    INSERT INTO dbo.cong_thuc (id, ten, mo_ta, gia_co_ban)
    VALUES (
        '40000001-0000-4000-8000-000000000002',
        N'CT Trà đào',
        N'Trà đào mát lạnh',
        32000.00
    );

/* ========== 7. Lượng nguyên liệu trong công thức ========== */
IF NOT EXISTS (SELECT 1 FROM dbo.luong_nguyen_lieu WHERE id = '41000001-0000-4000-8000-000000000001')
    INSERT INTO dbo.luong_nguyen_lieu (id, cong_thuc_id, nguyen_lieu_id, so_luong, don_vi)
    VALUES ('41000001-0000-4000-8000-000000000001', '40000001-0000-4000-8000-000000000001', '30000001-0000-4000-8000-000000000001', 8.000, N'g');

IF NOT EXISTS (SELECT 1 FROM dbo.luong_nguyen_lieu WHERE id = '41000001-0000-4000-8000-000000000002')
    INSERT INTO dbo.luong_nguyen_lieu (id, cong_thuc_id, nguyen_lieu_id, so_luong, don_vi)
    VALUES ('41000001-0000-4000-8000-000000000002', '40000001-0000-4000-8000-000000000001', '30000001-0000-4000-8000-000000000002', 200.000, N'ml');

IF NOT EXISTS (SELECT 1 FROM dbo.luong_nguyen_lieu WHERE id = '41000001-0000-4000-8000-000000000003')
    INSERT INTO dbo.luong_nguyen_lieu (id, cong_thuc_id, nguyen_lieu_id, so_luong, don_vi)
    VALUES ('41000001-0000-4000-8000-000000000003', '40000001-0000-4000-8000-000000000001', '30000001-0000-4000-8000-000000000003', 30.000, N'ml');

IF NOT EXISTS (SELECT 1 FROM dbo.luong_nguyen_lieu WHERE id = '41000001-0000-4000-8000-000000000004')
    INSERT INTO dbo.luong_nguyen_lieu (id, cong_thuc_id, nguyen_lieu_id, so_luong, don_vi)
    VALUES ('41000001-0000-4000-8000-000000000004', '40000001-0000-4000-8000-000000000002', '30000001-0000-4000-8000-000000000001', 6.000, N'g');

IF NOT EXISTS (SELECT 1 FROM dbo.luong_nguyen_lieu WHERE id = '41000001-0000-4000-8000-000000000005')
    INSERT INTO dbo.luong_nguyen_lieu (id, cong_thuc_id, nguyen_lieu_id, so_luong, don_vi)
    VALUES ('41000001-0000-4000-8000-000000000005', '40000001-0000-4000-8000-000000000002', '30000001-0000-4000-8000-000000000002', 180.000, N'ml');

/* ========== 8. Sản phẩm (JOINED) + nước uống sẵn ========== */
IF NOT EXISTS (SELECT 1 FROM dbo.san_pham WHERE id = '50000001-0000-4000-8000-000000000001')
    INSERT INTO dbo.san_pham (id, ten, gia, mo_ta, danh_muc, dang_kinh_doanh)
    VALUES (
        '50000001-0000-4000-8000-000000000001',
        N'Trà sữa truyền thống',
        45000.00,
        N'Trà đen, sữa tươi, đường mật — size M.',
        N'Trà sữa',
        1
    );

IF NOT EXISTS (SELECT 1 FROM dbo.nuoc_uong_san WHERE id = '50000001-0000-4000-8000-000000000001')
    INSERT INTO dbo.nuoc_uong_san (id, cong_thuc_id, co_the_tuy_chinh, muc_da_tuy_chon, muc_da_mac_dinh, topping_cho_phep)
    VALUES (
        '50000001-0000-4000-8000-000000000001',
        '40000001-0000-4000-8000-000000000001',
        1,
        N'Không đá,Ít đá,Bình thường,Nhiều đá',
        N'Bình thường',
        N''
    );

IF NOT EXISTS (SELECT 1 FROM dbo.san_pham WHERE id = '50000001-0000-4000-8000-000000000002')
    INSERT INTO dbo.san_pham (id, ten, gia, mo_ta, danh_muc, dang_kinh_doanh)
    VALUES (
        '50000001-0000-4000-8000-000000000002',
        N'Trà đào cam sả',
        42000.00,
        N'Trà đào thanh mát, thêm sả.',
        N'Trà trái cây',
        1
    );

IF NOT EXISTS (SELECT 1 FROM dbo.nuoc_uong_san WHERE id = '50000001-0000-4000-8000-000000000002')
    INSERT INTO dbo.nuoc_uong_san (id, cong_thuc_id, co_the_tuy_chinh, muc_da_tuy_chon, muc_da_mac_dinh, topping_cho_phep)
    VALUES (
        '50000001-0000-4000-8000-000000000002',
        '40000001-0000-4000-8000-000000000002',
        1,
        N'Không đá,Ít đá,Bình thường,Nhiều đá',
        N'Ít đá',
        N''
    );

/* Hình ảnh (collection table) */
IF NOT EXISTS (SELECT 1 FROM dbo.san_pham_hinh_anh WHERE san_pham_id = '50000001-0000-4000-8000-000000000001')
    INSERT INTO dbo.san_pham_hinh_anh (san_pham_id, hinh_anh_url)
    VALUES ('50000001-0000-4000-8000-000000000001', N'/uploads/demo/tra-sua.jpg');

IF NOT EXISTS (SELECT 1 FROM dbo.san_pham_hinh_anh WHERE san_pham_id = '50000001-0000-4000-8000-000000000002')
    INSERT INTO dbo.san_pham_hinh_anh (san_pham_id, hinh_anh_url)
    VALUES ('50000001-0000-4000-8000-000000000002', N'/uploads/demo/tra-dao.jpg');

/* M2M nuoc_uong_san <-> nguyen_lieu (tùy chọn) */
IF NOT EXISTS (
    SELECT 1 FROM dbo.nuoc_uong_san_nguyen_lieu
    WHERE nuoc_uong_san_id = '50000001-0000-4000-8000-000000000001' AND nguyen_lieu_id = '30000001-0000-4000-8000-000000000004'
)
    INSERT INTO dbo.nuoc_uong_san_nguyen_lieu (nuoc_uong_san_id, nguyen_lieu_id)
    VALUES ('50000001-0000-4000-8000-000000000001', '30000001-0000-4000-8000-000000000004');

/* ========== 9. Mã giảm giá (voucher) ========== */
IF NOT EXISTS (SELECT 1 FROM dbo.ma_giam_gia WHERE ma = N'WELCOME10')
    INSERT INTO dbo.ma_giam_gia (id, ma, loai_giam, gia_tri, ngay_bat_dau, ngay_ket_thuc, kich_hoat, ap_dung_toan_he_thong)
    VALUES (
        '60000001-0000-4000-8000-000000000001',
        N'WELCOME10',
        N'PHAN_TRAM',
        10.00,
        CAST('2020-01-01' AS DATE),
        CAST('2035-12-31' AS DATE),
        1,
        1
    );

IF NOT EXISTS (SELECT 1 FROM dbo.ma_giam_gia WHERE ma = N'GIA20K')
    INSERT INTO dbo.ma_giam_gia (id, ma, loai_giam, gia_tri, ngay_bat_dau, ngay_ket_thuc, kich_hoat, ap_dung_toan_he_thong)
    VALUES (
        '60000001-0000-4000-8000-000000000002',
        N'GIA20K',
        N'SO_TIEN_CO_DINH',
        20000.00,
        CAST('2020-01-01' AS DATE),
        CAST('2035-12-31' AS DATE),
        1,
        0
    );

/* Gắn GIA20K cho một sản phẩm */
IF NOT EXISTS (
    SELECT 1 FROM dbo.ma_giam_gia_san_pham
    WHERE ma_giam_gia_id = '60000001-0000-4000-8000-000000000002' AND san_pham_id = '50000001-0000-4000-8000-000000000001'
)
    INSERT INTO dbo.ma_giam_gia_san_pham (ma_giam_gia_id, san_pham_id)
    VALUES ('60000001-0000-4000-8000-000000000002', '50000001-0000-4000-8000-000000000001');

/* ========== 10. Khuyến mãi giá sản phẩm ========== */
IF NOT EXISTS (SELECT 1 FROM dbo.khuyen_mai_gia_san_pham WHERE id = '61000001-0000-4000-8000-000000000001')
    INSERT INTO dbo.khuyen_mai_gia_san_pham (id, ten, pham_vi, loai_giam, gia_tri, thoi_gian_bat_dau, thoi_gian_ket_thuc, kich_hoat, san_pham_don_id, danh_muc)
    VALUES (
        '61000001-0000-4000-8000-000000000001',
        N'Giảm 15% cho Trà sữa truyền thống',
        N'MOT_SAN_PHAM',
        N'PHAN_TRAM',
        15.00,
        CAST('2020-01-01T00:00:00' AS DATETIME2),
        CAST('2035-12-31T23:59:59' AS DATETIME2),
        1,
        '50000001-0000-4000-8000-000000000001',
        NULL
    );

IF NOT EXISTS (SELECT 1 FROM dbo.khuyen_mai_gia_san_pham WHERE id = '61000001-0000-4000-8000-000000000002')
    INSERT INTO dbo.khuyen_mai_gia_san_pham (id, ten, pham_vi, loai_giam, gia_tri, thoi_gian_bat_dau, thoi_gian_ket_thuc, kich_hoat, san_pham_don_id, danh_muc)
    VALUES (
        '61000001-0000-4000-8000-000000000002',
        N'Giảm 5000đ toàn danh mục Trà sữa',
        N'DANH_MUC',
        N'SO_TIEN_CO_DINH',
        5000.00,
        CAST('2020-01-01T00:00:00' AS DATETIME2),
        CAST('2035-12-31T23:59:59' AS DATETIME2),
        1,
        NULL,
        N'Trà sữa'
    );

IF NOT EXISTS (SELECT 1 FROM dbo.khuyen_mai_gia_san_pham WHERE id = '61000001-0000-4000-8000-000000000003')
    INSERT INTO dbo.khuyen_mai_gia_san_pham (id, ten, pham_vi, loai_giam, gia_tri, thoi_gian_bat_dau, thoi_gian_ket_thuc, kich_hoat, san_pham_don_id, danh_muc)
    VALUES (
        '61000001-0000-4000-8000-000000000003',
        N'Combo 2 món: giảm 8%',
        N'NHIEU_SAN_PHAM',
        N'PHAN_TRAM',
        8.00,
        CAST('2020-01-01T00:00:00' AS DATETIME2),
        CAST('2035-12-31T23:59:59' AS DATETIME2),
        1,
        NULL,
        NULL
    );

INSERT INTO dbo.khuyen_mai_gia_san_pham_ap_dung (khuyen_mai_id, san_pham_id)
SELECT '61000001-0000-4000-8000-000000000003', '50000001-0000-4000-8000-000000000001'
WHERE NOT EXISTS (
    SELECT 1 FROM dbo.khuyen_mai_gia_san_pham_ap_dung
    WHERE khuyen_mai_id = '61000001-0000-4000-8000-000000000003' AND san_pham_id = '50000001-0000-4000-8000-000000000001'
);

INSERT INTO dbo.khuyen_mai_gia_san_pham_ap_dung (khuyen_mai_id, san_pham_id)
SELECT '61000001-0000-4000-8000-000000000003', '50000001-0000-4000-8000-000000000002'
WHERE NOT EXISTS (
    SELECT 1 FROM dbo.khuyen_mai_gia_san_pham_ap_dung
    WHERE khuyen_mai_id = '61000001-0000-4000-8000-000000000003' AND san_pham_id = '50000001-0000-4000-8000-000000000002'
);

/* ========== 11. Giỏ hàng mẫu (chỉ khi khách chưa có giỏ — tránh vi phạm unique khach_hang_id) ========== */
IF NOT EXISTS (SELECT 1 FROM dbo.gio_hang WHERE khach_hang_id = '20000001-0000-4000-8000-000000000003')
BEGIN
    INSERT INTO dbo.gio_hang (id, khach_hang_id, tong_tien)
    VALUES ('70000001-0000-4000-8000-000000000001', '20000001-0000-4000-8000-000000000003', 45000.00);

    INSERT INTO dbo.chi_tiet_gio_hang (id, gio_hang_id, nuoc_uong_san_id, so_luong, muc_da, ghi_chu, thanh_tien, duoc_chon_thanh_toan)
    VALUES (
        '71000001-0000-4000-8000-000000000001',
        '70000001-0000-4000-8000-000000000001',
        '50000001-0000-4000-8000-000000000001',
        1,
        NULL,
        NULL,
        45000.00,
        1
    );
END

/* ========== 12. Đơn hàng mẫu (đã giao + thanh toán + hóa đơn) ========== */
IF NOT EXISTS (SELECT 1 FROM dbo.don_hang WHERE id = '80000001-0000-4000-8000-000000000001')
    INSERT INTO dbo.don_hang (id, khach_hang_id, ngay_dat, trang_thai, tong_tien, ma_giam_gia_id, tien_giam_ap_dung, dia_chi_giao_hang_id)
    VALUES (
        '80000001-0000-4000-8000-000000000001',
        '20000001-0000-4000-8000-000000000003',
        SYSUTCDATETIME(),
        N'DA_GIAO',
        40000.00,
        '60000001-0000-4000-8000-000000000001',
        4500.00,
        '20000001-0000-4000-8000-000000000010'
    );

IF NOT EXISTS (SELECT 1 FROM dbo.chi_tiet_don_hang WHERE id = '81000001-0000-4000-8000-000000000001')
    INSERT INTO dbo.chi_tiet_don_hang (id, don_hang_id, nuoc_uong_san_id, so_luong, muc_da, ghi_chu, thanh_tien)
    VALUES (
        '81000001-0000-4000-8000-000000000001',
        '80000001-0000-4000-8000-000000000001',
        '50000001-0000-4000-8000-000000000001',
        1,
        NULL,
        NULL,
        40000.00
    );

IF NOT EXISTS (SELECT 1 FROM dbo.thanh_toan WHERE don_hang_id = '80000001-0000-4000-8000-000000000001')
    INSERT INTO dbo.thanh_toan (id, don_hang_id, so_tien, phuong_thuc, trang_thai)
    VALUES (
        NEWID(),
        '80000001-0000-4000-8000-000000000001',
        40000.00,
        N'TIEN_MAT',
        N'THANH_CONG'
    );

IF NOT EXISTS (SELECT 1 FROM dbo.hoa_don WHERE don_hang_id = '80000001-0000-4000-8000-000000000001')
    INSERT INTO dbo.hoa_don (id, don_hang_id, so_hoa_don, ngay_lap, tong_tien, tien_giam, tien_thanh_toan, phuong_thuc_thanh_toan, ghi_chu, trang_thai_hoa_don, ngay_in)
    VALUES (
        '82000001-0000-4000-8000-000000000001',
        '80000001-0000-4000-8000-000000000001',
        N'HD-DEMO-00001',
        SYSUTCDATETIME(),
        44500.00,
        4500.00,
        40000.00,
        N'TIEN_MAT',
        N'Đơn mẫu từ script seed',
        N'CHO_IN',
        NULL
    );

COMMIT TRANSACTION;
GO

PRINT N'Hoàn tất seed TKPMHDT. Đăng nhập: admin / nhanvien / khachhang — mật khẩu: 123456';
GO

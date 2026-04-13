package TKPMHDT.Controller.api.quantrivien;

import TKPMHDT.Entity.khuyenmai.enums.LoaiGiamGiaEnum;
import TKPMHDT.Entity.khuyenmai.enums.PhamViKhuyenMaiGia;
import TKPMHDT.Entity.nguoidung.enums.VaiTro;
import TKPMHDT.Entity.sanpham.enums.LoaiNguyenLieu;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO / record dùng cho các REST admin (cùng package, không public để gom một file).
 */
record SanPhamRequest(
        String ten,
        BigDecimal gia,
        String moTa,
        String danhMuc,
        Boolean dangBan,
        List<String> hinhAnh,
        UUID congThucId) {
}

record TrangThaiBanRequest(boolean dangBan) {
}

record NguyenLieuRequest(
        String ten,
        String donVi,
        BigDecimal soLuongTon,
        BigDecimal giaDonVi,
        BigDecimal nguongCanhBao,
        LoaiNguyenLieu loaiNguyenLieu) {
}

record CapNhatKhoRequest(UUID nguyenLieuId, BigDecimal soLuong, String ghiChu) {
}

record CongThucNguyenLieuRequest(UUID nguyenLieuId, BigDecimal soLuong, String donVi) {
}

record TuyChinhCongThucRequest(List<String> mucDaTuyChon, String mucDaMacDinh, List<UUID> toppingChoPhep) {
}

record CapNhatTrangThaiDonHangRequest(String trangThai) {
}

record CapNhatNguoiDungRequest(String email, String hoTen, String soDienThoai, Boolean kichHoat) {
}

record TaoNhanVienRequest(String tenDangNhap, String email, String matKhau, VaiTro vaiTro) {
}

record CapNhatNhanVienRequest(String email, VaiTro vaiTro, Boolean kichHoat) {
}

record CapNhatTrangThaiTaiKhoanRequest(UUID nguoiDungId, Boolean active) {
}

record KhuyenMaiRequest(
        String maGiamGia,
        LoaiGiamGiaEnum loaiGiam,
        BigDecimal giaTri,
        LocalDate ngayBatDau,
        LocalDate ngayKetThuc,
        Boolean kichHoat,
        Boolean apDungToanHeThong,
        List<UUID> sanPhamIds,
        List<LocalDate> cacNgayApDung,
        List<String> danhMucApDung) {
}

record KhuyenMaiGiaSanPhamRequest(
        String ten,
        PhamViKhuyenMaiGia phamVi,
        LoaiGiamGiaEnum loaiGiam,
        BigDecimal giaTri,
        LocalDateTime thoiGianBatDau,
        LocalDateTime thoiGianKetThuc,
        Boolean kichHoat,
        UUID sanPhamDonId,
        List<UUID> sanPhamIds,
        String danhMuc) {
}

record CauHinhRequest(String configKey, String configValue, String moTa) {
}

record RbacRequest(List<String> quyens) {
}

record TaoKhachHangRequest(String tenDangNhap, String email, String hoTen, String soDienThoai) {
}

record TaoDiaChiRequest(
        String tenNguoiNhan,
        String soDienThoai,
        String diaChiCuThe,
        String phuongXa,
        String quanHuyen,
        String tinhThanhPho,
        Boolean laMacDinh) {
}

record CapNhatDiaChiRequest(
        String tenNguoiNhan,
        String soDienThoai,
        String diaChiCuThe,
        String phuongXa,
        String quanHuyen,
        String tinhThanhPho,
        Boolean laMacDinh) {
}

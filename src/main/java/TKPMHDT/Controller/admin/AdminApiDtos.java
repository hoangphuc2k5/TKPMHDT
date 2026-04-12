package TKPMHDT.Controller.admin;

import TKPMHDT.Entity.khuyenmai.enums.LoaiGiamGiaEnum;
import TKPMHDT.Entity.nguoidung.enums.VaiTro;
import TKPMHDT.Entity.sanpham.enums.LoaiNguyenLieu;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public final class AdminApiDtos {

    private AdminApiDtos() {}

    public record SanPhamRequest(
            String ten,
            BigDecimal gia,
            String moTa,
            String danhMuc,
            Boolean dangBan,
            List<String> hinhAnh,
            UUID congThucId) {}

    public record TrangThaiBanRequest(boolean dangBan) {}

    public record TuyChonRequest(
            String ten,
            String nhom,
            BigDecimal giaThem,
            Boolean kichHoat) {}

    public record NguyenLieuRequest(
            String ten,
            String donVi,
            BigDecimal soLuongTon,
            BigDecimal giaDonVi,
            BigDecimal nguongCanhBao,
            LoaiNguyenLieu loaiNguyenLieu) {}

    public record CapNhatKhoRequest(UUID nguyenLieuId, BigDecimal soLuong, String ghiChu) {}

    public record CongThucNguyenLieuRequest(UUID nguyenLieuId, BigDecimal soLuong, String donVi) {}

    public record TuyChinhCongThucRequest(
            List<String> mucDuongTuyChon,
            String mucDuongMacDinh,
            List<String> mucDaTuyChon,
            String mucDaMacDinh,
            List<String> kichCoTuyChon,
            String kichCoMacDinh,
            Boolean coApDungSize,
            List<UUID> toppingChoPhep) {}

    public record CapNhatTrangThaiDonHangRequest(String trangThai) {}

    public record CapNhatNguoiDungRequest(
            String email,
            String hoTen,
            String soDienThoai,
            Boolean kichHoat) {}

    public record TaoNhanVienRequest(
            String tenDangNhap,
            String email,
            String matKhau,
            VaiTro vaiTro) {}

    public record CapNhatNhanVienRequest(String email, VaiTro vaiTro, Boolean kichHoat) {}

    public record CapNhatTrangThaiTaiKhoanRequest(UUID nguoiDungId, Boolean active) {}

    public record KhuyenMaiRequest(
            String maGiamGia,
            LoaiGiamGiaEnum loaiGiam,
            BigDecimal giaTri,
            LocalDate ngayBatDau,
            LocalDate ngayKetThuc,
            Boolean kichHoat,
            Boolean apDungToanHeThong,
            List<UUID> sanPhamIds) {}

    public record CauHinhRequest(String configKey, String configValue, String moTa) {}

    public record RbacRequest(List<String> quyens) {}

    public record TaoKhachHangRequest(
            String tenDangNhap,
            String email,
            String hoTen,
            String soDienThoai) {}

    public record TaoDiaChiRequest(
            String tenNguoiNhan,
            String soDienThoai,
            String diaChiCuThe,
            String phuongXa,
            String quanHuyen,
            String tinhThanhPho,
            Boolean laMacDinh) {}

    public record CapNhatDiaChiRequest(
            String tenNguoiNhan,
            String soDienThoai,
            String diaChiCuThe,
            String phuongXa,
            String quanHuyen,
            String tinhThanhPho,
            Boolean laMacDinh) {}
}

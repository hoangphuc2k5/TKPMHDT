package TKPMHDT.Controller.admin;

import TKPMHDT.Entity.donhang.ChiTietDonHang;
import TKPMHDT.Entity.donhang.DonHang;
import TKPMHDT.Entity.donhang.HoaDon;
import TKPMHDT.Entity.donhang.PhieuGiao;
import TKPMHDT.Entity.khuyenmai.MaGiamGia;
import TKPMHDT.Entity.nguoidung.DiaChi;
import TKPMHDT.Entity.nguoidung.KhachHang;
import TKPMHDT.Entity.nguoidung.NguoiDung;
import TKPMHDT.Entity.sanpham.NuocUongSan;
import TKPMHDT.Entity.sanpham.TuyChinhKhachHang;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class AdminPayloadHelper {

    public Map<String, Object> toUserPayload(NguoiDung user) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", user.getId());
        payload.put("tenDangNhap", user.getTenDangNhap());
        payload.put("email", user.getEmail());
        payload.put("vaiTro", user.getVaiTro());
        payload.put("active", user.isTrangThaiHoatDong());
        payload.put("trangThaiHoatDong", user.isTrangThaiHoatDong());
        if (user instanceof KhachHang kh) {
            payload.put("hoTen", kh.getHoTen());
            payload.put("soDienThoai", kh.getSoDienThoai());
        }
        return payload;
    }

    public boolean checkUserMatch(NguoiDung u, String keyword) {
        String k = keyword.toLowerCase(Locale.ROOT);
        String username = u.getTenDangNhap() != null ? u.getTenDangNhap().toLowerCase(Locale.ROOT) : "";
        String email = u.getEmail() != null ? u.getEmail().toLowerCase(Locale.ROOT) : "";
        String hoten = "";
        if (u instanceof KhachHang kh && kh.getHoTen() != null) {
            hoten = kh.getHoTen().toLowerCase(Locale.ROOT);
        }
        return username.contains(k) || email.contains(k) || hoten.contains(k);
    }

    public String mapTrangThaiVeDb(String input) {
        if (input == null) {
            return "CHO_XAC_NHAN";
        }
        String normalized = input.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "CHO_XU_LY", "CHO_XAC_NHAN" -> "CHO_XAC_NHAN";
            case "DANG_LAM", "DA_XAC_NHAN", "DANG_CHUAN_BI" -> "DA_XAC_NHAN";
            case "HOAN_THANH", "DA_GIAO" -> "DA_GIAO";
            case "HUY", "DA_HUY" -> "DA_HUY";
            default -> normalized;
        };
    }

    public Map<String, Object> toDiaChiPayload(DiaChi diaChi) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", diaChi.getId());
        payload.put("tenNguoiNhan", diaChi.getTenNguoiNhan());
        payload.put("soDienThoai", diaChi.getSoDienThoai());
        payload.put("diaChiCuThe", diaChi.getDiaChiCuThe());
        payload.put("phuongXa", diaChi.getPhuongXa());
        payload.put("quanHuyen", diaChi.getQuanHuyen());
        payload.put("tinhThanhPho", diaChi.getTinhThanhPho());
        payload.put("laMacDinh", diaChi.isLaMacDinh());
        return payload;
    }

    public Map<String, Object> toDonHangDetailPayload(DonHang donHang) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", donHang.getId());
        payload.put("ngayDat", donHang.getNgayDat());
        payload.put("tongTien", donHang.getTongTien());
        payload.put("trangThaiDb", donHang.getTrangThaiDb());
        KhachHang kh = donHang.getKhachHang();
        payload.put("khachHang", kh != null && kh.getTenDangNhap() != null ? kh.getTenDangNhap() : "Khách lẻ");
        if (donHang.getDiaChiGiaoHang() != null) {
            payload.put("diaChiGiaoHang", toDiaChiPayload(donHang.getDiaChiGiaoHang()));
        }
        if (donHang.getMaGiamGia() != null) {
            payload.put("maGiamGia", donHang.getMaGiamGia().getMa());
        }
        List<ChiTietDonHang> lines = donHang.getChiTietDonHangs();
        payload.put("chiTiet", lines == null ? List.of() : lines.stream().map(this::toChiTietDonHangAdminPayload).toList());
        return payload;
    }

    public Map<String, Object> toChiTietDonHangAdminPayload(ChiTietDonHang ct) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", ct.getId());
        NuocUongSan sp = ct.getNuocUong();
        m.put("sanPham", sp != null && sp.getTen() != null ? sp.getTen() : "");
        m.put("soLuong", ct.getSoLuong());
        m.put("thanhTien", ct.getThanhTien());
        String congThucTen = "";
        if (sp != null && sp.getCongThucCoBan() != null && sp.getCongThucCoBan().getTen() != null) {
            congThucTen = sp.getCongThucCoBan().getTen();
        }
        m.put("congThuc", congThucTen);
        m.put("tuyChinh", toTuyChinhKhachHangPayload(ct.getTuyChinh()));
        if (ct.getToppings() != null && !ct.getToppings().isEmpty()) {
            m.put("toppings", ct.getToppings().stream().map(t -> {
                Map<String, Object> tm = new HashMap<>();
                tm.put("ten", t.getNguyenLieu() != null ? t.getNguyenLieu().getTen() : null);
                tm.put("soLuong", t.getSoLuong());
                tm.put("donGia", t.getDonGia());
                return tm;
            }).toList());
        }
        return m;
    }

    public Map<String, Object> toTuyChinhKhachHangPayload(TuyChinhKhachHang tc) {
        Map<String, Object> m = new HashMap<>();
        if (tc == null) {
            return m;
        }
        m.put("kichCo", tc.getKichCo());
        m.put("mucDuong", null);
        m.put("mucDa", tc.getMucDa());
        m.put("ghiChu", tc.getGhiChu());
        return m;
    }

    public Map<String, Object> toHoaDonPayload(HoaDon hoaDon) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", hoaDon.getId());
        payload.put("soHoaDon", hoaDon.getSoHoaDon());
        payload.put("ngayLap", hoaDon.getNgayLap());
        payload.put("tongTien", hoaDon.getTongTien());
        payload.put("tienGiam", hoaDon.getTienGiam());
        payload.put("tienThanhToan", hoaDon.getTienThanhToan());
        payload.put("trangThaiHoaDon", hoaDon.getTrangThaiHoaDon());
        payload.put("donHangId", hoaDon.getDonHang() != null ? hoaDon.getDonHang().getId() : null);
        return payload;
    }

    public Map<String, Object> toHoaDonDetailPayload(HoaDon hoaDon) {
        Map<String, Object> payload = toHoaDonPayload(hoaDon);
        payload.put("phuongThucThanhToan", hoaDon.getPhuongThucThanhToan());
        payload.put("ghiChu", hoaDon.getGhiChu());
        payload.put("ngayIn", hoaDon.getNgayIn());
        if (hoaDon.getDonHang() != null) {
            payload.put("donHang", toDonHangDetailPayload(hoaDon.getDonHang()));
        }
        return payload;
    }

    public Map<String, Object> toPhieuGiaoPayload(PhieuGiao phieuGiao) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", phieuGiao.getId());
        payload.put("soPhieuGiao", phieuGiao.getSoPhieuGiao());
        payload.put("ngayTao", phieuGiao.getNgayTao());
        payload.put("trangThaiGiao", phieuGiao.getTrangThaiGiao());
        payload.put("donHangId", phieuGiao.getDonHang() != null ? phieuGiao.getDonHang().getId() : null);
        return payload;
    }

    public Map<String, Object> toPhieuGiaoDetailPayload(PhieuGiao phieuGiao) {
        Map<String, Object> payload = toPhieuGiaoPayload(phieuGiao);
        payload.put("diaChiGiao", phieuGiao.getDiaChiGiao());
        payload.put("soDienThoaiNhan", phieuGiao.getSoDienThoaiNhan());
        payload.put("tenNhan", phieuGiao.getTenNhan());
        payload.put("nhanVienGiao", phieuGiao.getNhanVienGiao() != null ? phieuGiao.getNhanVienGiao().getTenDangNhap() : null);
        payload.put("ngayGiaoDuKien", phieuGiao.getNgayGiaoDuKien());
        payload.put("ngayGiaoThucTe", phieuGiao.getNgayGiaoThucTe());
        payload.put("ghiChu", phieuGiao.getGhiChu());
        if (phieuGiao.getDonHang() != null) {
            payload.put("donHang", toDonHangDetailPayload(phieuGiao.getDonHang()));
        }
        return payload;
    }

    public Map<String, Object> toKhuyenMaiPayload(MaGiamGia entity) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", entity.getId());
        payload.put("ma", entity.getMa());
        payload.put("loaiGiam", entity.getLoaiGiam());
        payload.put("giaTri", entity.getGiaTri());
        payload.put("ngayBatDau", entity.getNgayBatDau());
        payload.put("ngayKetThuc", entity.getNgayKetThuc());
        payload.put("kichHoat", entity.isKichHoat());
        payload.put("apDungToanHeThong", entity.isApDungToanHeThong());
        payload.put(
                "sanPhamIds",
                entity.getSanPhamApDung() == null
                        ? List.of()
                        : entity.getSanPhamApDung().stream().map(s -> s.getId()).toList());
        return payload;
    }
}

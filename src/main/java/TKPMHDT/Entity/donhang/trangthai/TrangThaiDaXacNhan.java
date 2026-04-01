package TKPMHDT.Entity.donhang.trangthai;

import TKPMHDT.Entity.donhang.DonHang;

public class TrangThaiDaXacNhan implements TrangThaiDonHang {

    @Override
    public void choXacNhan(DonHang donHang) {
        throw new IllegalStateException("Không thể quay lại trạng thái chờ xác nhận.");
    }

    @Override
    public void daXacNhan(DonHang donHang) {
        throw new IllegalStateException("Đơn hàng đã được xác nhận.");
    }

    @Override
    public void dangGiao(DonHang donHang) {
        donHang.setTrangThai(new TrangThaiDangGiao());
    }

    @Override
    public void daGiao(DonHang donHang) {
        throw new IllegalStateException("Không thể chuyển sang đã giao từ đã xác nhận.");
    }

    @Override
    public void daHuy(DonHang donHang) {
        donHang.setTrangThai(new TrangThaiDaHuy());
    }

    @Override
    public String getTenTrangThai() {
        return "DA_XAC_NHAN";
    }
}

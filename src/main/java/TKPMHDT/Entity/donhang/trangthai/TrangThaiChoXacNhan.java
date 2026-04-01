package TKPMHDT.Entity.donhang.trangthai;

import TKPMHDT.Entity.donhang.DonHang;

public class TrangThaiChoXacNhan implements TrangThaiDonHang {

    @Override
    public void choXacNhan(DonHang donHang) {
        throw new IllegalStateException("Đơn hàng đã ở trạng thái chờ xác nhận.");
    }

    @Override
    public void daXacNhan(DonHang donHang) {
        donHang.setTrangThai(new TrangThaiDaXacNhan());
    }

    @Override
    public void dangGiao(DonHang donHang) {
        throw new IllegalStateException("Không thể chuyển sang đang giao từ chờ xác nhận.");
    }

    @Override
    public void daGiao(DonHang donHang) {
        throw new IllegalStateException("Không thể chuyển sang đã giao từ chờ xác nhận.");
    }

    @Override
    public void daHuy(DonHang donHang) {
        donHang.setTrangThai(new TrangThaiDaHuy());
    }

    @Override
    public String getTenTrangThai() {
        return "CHO_XAC_NHAN";
    }
}

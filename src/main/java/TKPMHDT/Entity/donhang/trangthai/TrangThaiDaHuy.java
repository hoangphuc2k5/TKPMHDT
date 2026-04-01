package TKPMHDT.Entity.donhang.trangthai;

import TKPMHDT.Entity.donhang.DonHang;

public class TrangThaiDaHuy implements TrangThaiDonHang {

    @Override
    public void choXacNhan(DonHang donHang) {
        throw new IllegalStateException("Đơn hàng đã hủy, không thể thay đổi trạng thái.");
    }

    @Override
    public void daXacNhan(DonHang donHang) {
        throw new IllegalStateException("Đơn hàng đã hủy, không thể thay đổi trạng thái.");
    }

    @Override
    public void dangGiao(DonHang donHang) {
        throw new IllegalStateException("Đơn hàng đã hủy, không thể thay đổi trạng thái.");
    }

    @Override
    public void daGiao(DonHang donHang) {
        throw new IllegalStateException("Đơn hàng đã hủy, không thể thay đổi trạng thái.");
    }

    @Override
    public void daHuy(DonHang donHang) {
        throw new IllegalStateException("Đơn hàng đã ở trạng thái đã hủy.");
    }

    @Override
    public String getTenTrangThai() {
        return "DA_HUY";
    }
}

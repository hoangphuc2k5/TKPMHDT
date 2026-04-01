package TKPMHDT.Entity.donhang.trangthai;

import TKPMHDT.Entity.donhang.DonHang;

public class TrangThaiDaGiao implements TrangThaiDonHang {

    @Override
    public void choXacNhan(DonHang donHang) {
        throw new IllegalStateException("Đơn hàng đã giao, không thể thay đổi trạng thái.");
    }

    @Override
    public void daXacNhan(DonHang donHang) {
        throw new IllegalStateException("Đơn hàng đã giao, không thể thay đổi trạng thái.");
    }

    @Override
    public void dangGiao(DonHang donHang) {
        throw new IllegalStateException("Đơn hàng đã giao, không thể thay đổi trạng thái.");
    }

    @Override
    public void daGiao(DonHang donHang) {
        throw new IllegalStateException("Đơn hàng đã ở trạng thái đã giao.");
    }

    @Override
    public void daHuy(DonHang donHang) {
        throw new IllegalStateException("Không thể hủy đơn hàng đã giao.");
    }

    @Override
    public String getTenTrangThai() {
        return "DA_GIAO";
    }
}

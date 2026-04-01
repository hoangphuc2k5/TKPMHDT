package TKPMHDT.Entity.donhang.trangthai;

import TKPMHDT.Entity.donhang.DonHang;

public class TrangThaiDangGiao implements TrangThaiDonHang {

    @Override
    public void choXacNhan(DonHang donHang) {
        throw new IllegalStateException("Không thể quay lại các trạng thái trước đó.");
    }

    @Override
    public void daXacNhan(DonHang donHang) {
        throw new IllegalStateException("Không thể quay lại các trạng thái trước đó.");
    }

    @Override
    public void dangGiao(DonHang donHang) {
        throw new IllegalStateException("Đơn hàng đã ở trạng thái đang giao.");
    }

    @Override
    public void daGiao(DonHang donHang) {
        donHang.setTrangThai(new TrangThaiDaGiao());
    }

    @Override
    public void daHuy(DonHang donHang) {
        throw new IllegalStateException("Không thể hủy đơn hàng đang trên đường giao.");
    }

    @Override
    public String getTenTrangThai() {
        return "DANG_GIAO";
    }
}

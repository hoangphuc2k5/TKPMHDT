package TKPMHDT.Entity.donhang.trangthai;

import TKPMHDT.Entity.donhang.DonHang;

public interface TrangThaiDonHang {
    void choXacNhan(DonHang donHang);
    void daXacNhan(DonHang donHang);
    void dangGiao(DonHang donHang);
    void daGiao(DonHang donHang);
    void daHuy(DonHang donHang);
    String getTenTrangThai();
}

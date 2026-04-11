package TKPMHDT.security;

import TKPMHDT.Entity.hethong.VaiTroQuyen;
import TKPMHDT.Entity.nguoidung.enums.VaiTro;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class PermissionCatalog {

    private PermissionCatalog() {
    }

    public static Set<String> defaultPermissions(VaiTro vaiTro) {
        Set<String> permissions = new LinkedHashSet<>(List.of(
                "auth:login",
                "auth:register",
                "auth:reset-password",
                "product:view",
                "product:customize"
        ));

        if (vaiTro == null) {
            return permissions;
        }

        switch (vaiTro) {
            case KHACH_HANG -> permissions.addAll(List.of(
                    "cart:manage",
                    "order:customer-create",
                    "order:track"
            ));
            case NHAN_VIEN_BAN_HANG -> permissions.addAll(List.of(
                    "pos:create",
                    "order:view",
                    "order:update",
                    "print:invoice",
                    "print:recipe"
            ));
            case QUAN_TRI_VIEN -> permissions.addAll(List.of(
                    "pos:create",
                    "order:view",
                    "order:update",
                    "print:invoice",
                    "print:recipe",
                    "product:manage",
                    "recipe:edit",
                    "order:manage-all",
                    "customer:manage",
                    "promotion:manage",
                    "report:view",
                    "inventory:manage",
                    "staff:manage",
                    "role:assign"
            ));
            case QUAN_TRI_VIEN_CAP_CAO, QUAN_LY_KHO -> permissions.addAll(List.of(
                    "pos:create",
                    "order:view",
                    "order:update",
                    "print:invoice",
                    "print:recipe",
                    "product:manage",
                    "recipe:edit",
                    "order:manage-all",
                    "customer:manage",
                    "promotion:manage",
                    "report:view",
                    "inventory:manage"
            ));
            default -> {
            }
        }
        return permissions;
    }

    public static Set<String> resolvePermissions(VaiTro vaiTro, VaiTroQuyen customPermissions) {
        Set<String> permissions = defaultPermissions(vaiTro);
        if (customPermissions == null || customPermissions.getQuyenCsv() == null) {
            return permissions;
        }
        Set<String> custom = Arrays.stream(customPermissions.getQuyenCsv().split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(s -> s.toLowerCase(Locale.ROOT))
                .collect(LinkedHashSet::new, LinkedHashSet::add, LinkedHashSet::addAll);
        if (!custom.isEmpty()) {
            permissions.addAll(custom);
        }
        return permissions;
    }
}

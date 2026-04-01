package TKPMHDT.Entity.hethong;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "cau_hinh_he_thong")
public class CauHinhHeThong {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "config_key", nullable = false, unique = true, length = 120)
    private String configKey;

    @Column(name = "config_value", nullable = false, columnDefinition = "nvarchar(max)")
    private String configValue;

    @Column(name = "mo_ta", columnDefinition = "nvarchar(max)")
    private String moTa;
}

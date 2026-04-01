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
@Table(name = "vai_tro_quyen")
public class VaiTroQuyen {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "vai_tro", nullable = false, unique = true, length = 50)
    private String vaiTro;

    @Column(name = "quyen_csv", nullable = false, columnDefinition = "nvarchar(max)")
    private String quyenCsv;
}

package mqproducer.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DiskInfo{

    private String systemRoot;
    private String totalSpace;
    private String freeSpace;
    private String usableSpace;
}

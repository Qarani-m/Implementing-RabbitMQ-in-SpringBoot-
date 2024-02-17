package mqproducer.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class DiskInfo{

    public DiskInfo(String systemRoot, String totalSpace, String freeSpace, String usableSpace) {
        this.systemRoot = systemRoot;
        this.totalSpace = totalSpace;
        this.freeSpace = freeSpace;
        this.usableSpace = usableSpace;
    }

    public  DiskInfo(){}
    private String systemRoot;
    private String totalSpace;
    private String freeSpace;
    private String usableSpace;
}

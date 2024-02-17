package mqproducer.entity;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageEntity {
    private DiskInfo diskInfo;
    private  OsInfo osInfo;
    private MemInfo memInfo;
    private double cpuLoad;
}


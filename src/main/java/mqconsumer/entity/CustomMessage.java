package mqconsumer.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomMessage {
    private  ServicesInfo servicesInfo;

    private DiskInfo diskInfo;
    private OsInfo osInfo;
}

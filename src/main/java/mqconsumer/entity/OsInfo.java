package mqconsumer.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OsInfo{
    private String os;
    private String osVersion;
    private String osArch;
    private String processors;
}

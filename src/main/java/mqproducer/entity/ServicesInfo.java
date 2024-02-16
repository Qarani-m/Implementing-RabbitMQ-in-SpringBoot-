package mqproducer.entity;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicesInfo {
    private String[] services;
    private double cpuLoad;
}


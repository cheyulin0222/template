package com.arplanets.template.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServerInfoContext {

    private String app;

    private String os;

    private String serverHost;

    private String serverIp;

    private String startupTime;

    private String issueTime;

    private String duration;

    private String heapMemoryUsage;

    private String nonHeapMemoryUsage;

    private String timeZone;
}

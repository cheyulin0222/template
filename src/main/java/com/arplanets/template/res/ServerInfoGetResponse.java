package com.arplanets.template.res;

import com.arplanets.template.bo.ServerInfoContext;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServerInfoGetResponse {
    private String productId;
    private String service;
    private String env;
    private String gitVer;
    private String clientIp;
    private ServerInfoContext context;
}

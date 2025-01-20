package com.arplanets.template.service.impl;

import com.arplanets.template.bo.ServerInfoContext;
import com.arplanets.template.cofig.ServerConfig;
import com.arplanets.template.res.ServerInfoGetResponse;
import com.arplanets.template.service.ServerInfoService;
import com.arplanets.template.utils.ServerInfoUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.*;

import static com.arplanets.template.utils.RequestUtil.LOCALHOST_IP;
import static com.arplanets.template.utils.TimeUtil.DATE_FORMAT_ISO8601;

@Service
@RequiredArgsConstructor
public class ServerInfoServiceImpl implements ServerInfoService {

    @Value("${spring.profiles.active}")
    private String env;

    @Value("${git.commit.id.abbrev:UNKNOWN}")
    private String gitVersion;

    @Value("${application.product.id:UNKNOWN}")
    private String productId;

    @Value("${application.service.id:UNKNOWN}")
    private String service;

    private final ServerConfig serverConfig;
    private final ServerInfoUtil serverInfoUtil;
    private final HttpServletRequest request;

    @Override
    public ServerInfoGetResponse getServerInfo() {

        // 取得 Client IP
        String clientIp = serverInfoUtil.getClientIp();

        // 依 Client IP 判斷是否取得 Server 資訊
        ServerInfoContext context = getServerInfoContext(clientIp);

        return ServerInfoGetResponse.builder()
                .productId(productId)
                .service(service)
                .env(env)
                .gitVer(gitVersion)
                .clientIp(clientIp)
                .context(context)
                .build();
    }

    private boolean isAllowedIp(String clientIp) {
        return serverConfig.getServerInfoAllowIp().contains(clientIp)
                || request.getRemoteAddr().equals(LOCALHOST_IP);
    }

    private ServerInfoContext getServerInfoContext(String clientIp) {
        if (isAllowedIp(clientIp)) {
            ZonedDateTime startupTime = serverInfoUtil.getStartupTime();
            ZonedDateTime currentTime = ZonedDateTime.now();

            return ServerInfoContext.builder()
                    .os(serverInfoUtil.getOSDetails())
                    .app(serverInfoUtil.getJavaDetails())
                    .serverHost(serverInfoUtil.getServerHost())
                    .serverIp(serverInfoUtil.getServerIp())
                    .heapMemoryUsage(serverInfoUtil.getHeapMemorUsage())
                    .nonHeapMemoryUsage(serverInfoUtil.getNonHeapMemorUsage())
                    .startupTime(startupTime.format(DATE_FORMAT_ISO8601))
                    .issueTime(currentTime.format(DATE_FORMAT_ISO8601))
                    .duration(serverInfoUtil.formatPeriodAndDuration(startupTime, currentTime))
                    .timeZone(ZoneId.systemDefault().getId())
                    .build();
        }

        return null;
    }

}

package com.arplanets.template.cofig;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.TimeZone;

/**
 * 從設定黨取得 time_zone，並在全域註冊
 * 從設定檔取得能夠取得 server info 的 client ip
 */
@Getter
@Configuration
public class ServerConfig {

    @Value("${app.time.zone}")
    private String timeZone;
    @Value("#{'${server.info.allow.ip}'.split(',')}")
    private List<String> serverInfoAllowIp;

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
    }
}

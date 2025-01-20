package com.arplanets.template.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.net.InetAddress;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
@RequiredArgsConstructor
public class ServerInfoUtil {

    private final ApplicationContext applicationContext;
    private final HttpServletRequest request;

    public String getHeapMemorUsage() {
        final MemoryUsage hm = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        return (hm.getUsed() / 1024 / 1024) + " / " + (hm.getMax() / 1024 / 1024) + " mb";
    }

    public String getNonHeapMemorUsage() {
        final MemoryUsage nhm = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
        return (nhm.getUsed() / 1024 / 1024) + " / " + (nhm.getMax() / 1024 / 1024) + " mb";
    }

    public String getOSDetails() {
        String osVersion = System.getProperty("os.version");
        String osName = System.getProperty("os.name");
        String osArch = System.getProperty("os.arch");
        return String.format("%s %s %s", osName, osVersion, osArch);
    }

    public String getJavaDetails() {
        String javaVmName = System.getProperty("java.vm.name");
        String javaVersion = System.getProperty("java.version");
        System.getProperty("java.version");
        return String.format("%s %s", javaVmName, javaVersion);
    }

    public ZonedDateTime getStartupTime() {
        long startupDate = applicationContext.getStartupDate();
        return Instant.ofEpochMilli(startupDate).atZone(ZoneId.systemDefault());
    }

    public String getClientIp() {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            // 如果 X-Forwarded-For 包含多個 IP 地址，取第一個
            if (ip.contains(",")) {
                ip = ip.split(",")[0].trim();
            }
            return ip;
        }
        return request.getRemoteAddr();
    }

    public String getServerHost() {
        InetAddress serverAddr = InetAddress.getLoopbackAddress();
        return serverAddr.getHostName();
    }

    public String getServerIp() {
        InetAddress serverAddr = InetAddress.getLoopbackAddress();
        return serverAddr.getHostAddress();
    }


    public String formatPeriodAndDuration(ZonedDateTime start, ZonedDateTime end) {
        // 直接計算兩個時間點之間的時間差
        Duration duration = Duration.between(start, end);

        // 轉換成年月日時分秒
        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        StringBuilder result = new StringBuilder();

        if (days >= 365) {
            long years = days / 365;
            result.append(years).append("y");
            days = days % 365;
        }

        if (days >= 30) {
            long months = days / 30;
            result.append(months).append("m");
            days = days % 30;
        }

        if (days > 0) {
            result.append(days).append("d");
        }

        if (hours > 0) {
            result.append(hours).append("h");
        }

        if (minutes > 0) {
            result.append(minutes).append("m");
        }

        if (seconds > 0) {
            result.append(seconds).append("s");
        }

        return result.toString().trim();
    }





}

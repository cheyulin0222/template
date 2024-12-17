package com.arplanet.template.casbin;

import lombok.RequiredArgsConstructor;
import org.casbin.jcasbin.main.Enforcer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class CasbinConfig {

    private final JDBCAdapter jdbcAdapter;

    @Bean
    public Enforcer enforcer() throws IOException {
        Resource resource = new ClassPathResource("casbin/model.conf");
        String modelPath = resource.getFile().getAbsolutePath();
        return new Enforcer(modelPath, jdbcAdapter);
    }
}

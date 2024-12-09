package com.arplanet.template.casbin;

import org.casbin.jcasbin.main.Enforcer;
import org.casbin.jcasbin.persist.Adapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class CasbinConfig {

    @Bean
    public Enforcer enforcer(@Qualifier("casbinDataSource") DataSource dataSource) {

        String modelPath = "classpath:casbin/model.conf";
        Adapter adapter = new JDBCAdapter(dataSource);

        // 配置 Casbin Enforcer，使用模型文件和数据库适配器
        return new Enforcer(modelPath, adapter);
    }
}

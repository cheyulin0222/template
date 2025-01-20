package com.arplanets.template.cofig;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 設定 git version 來源
 */
@Configuration
@PropertySource("classpath:git.properties")
public class GitConfig {
}

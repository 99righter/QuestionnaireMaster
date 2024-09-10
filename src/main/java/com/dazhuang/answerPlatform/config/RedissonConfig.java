package com.dazhuang.answerPlatform.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;

import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {
    //redisson配置
    /**
     * 主机名
     */
    private String host;
    /**
     * 端口号
     */
    private Integer port;
    /**
     * 数据库名
     */
    private Integer database;
    /**
     * 密码
     */
    private String password;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + host + ":" + port)
                .setDatabase(database)
                .setPassword(password);
        return Redisson.create(config);
    }
}

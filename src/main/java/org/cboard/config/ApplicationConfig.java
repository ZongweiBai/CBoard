package org.cboard.config;

import org.cboard.cache.RedisCacheManager;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.*;

/**
 * 应用配置
 *
 * @author BaiZongwei
 * @date 2021/2/19 14:44
 */
@Slf4j
@Configuration
public class ApplicationConfig implements InfoContributor {

    @Autowired
    private RedisTemplate redisTemplate;

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setApplicationContextSchedulerContextKey("applicationContext");
        return schedulerFactoryBean;
    }

    @Bean
    @Qualifier("rawDataCache")
    public RedisCacheManager cacheManager() {
        RedisCacheManager cacheManager = new RedisCacheManager<>();
        cacheManager.setRedisTemplate(redisTemplate);
        return cacheManager;
    }

    @Primary
    @Bean("dataSource")
    @ConfigurationProperties(prefix = "spring.datasource.primary")
    public DataSource dataSource(){
        return DataSourceBuilder.create().build();
    }

    @Bean("h2DataSource")
    @ConfigurationProperties(prefix = "spring.datasource.h2")
    public DataSource h2DataSource(){
        return DataSourceBuilder.create().build();
    }

    @Override
    public void contribute(Info.Builder builder) {
        Map<String, String> userDetails = new HashMap<>();

        Properties infoProp = new Properties();
        try (InputStream infoInput = ApplicationConfig.class.getResourceAsStream("/gitInfo.properties")) {
            infoProp.load(infoInput);
            SortedMap<String, String> gitInfoMap = new TreeMap(infoProp);
            for (String key : gitInfoMap.keySet()) {
                userDetails.put(key, gitInfoMap.get(key));
            }
        } catch (Exception ex) {
            log.warn(" Can not get the git information !");
        }
        builder.withDetail("gitInfo", userDetails);
    }

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> configurer(@Value("${spring.application.name}") String applicationName) {
        return (registry) -> registry.config().commonTags("application", applicationName);
    }
}

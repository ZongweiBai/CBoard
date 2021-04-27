package org.cboard.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * Mybatis配置
 *
 * @author BaiZongwei
 * @date 2021/2/20 10:28
 */
@Configuration
@MapperScan(basePackages = {"org.cboard.dao"}, sqlSessionFactoryRef = "sqlSessionFactory")
public class MybatisConfig {

}

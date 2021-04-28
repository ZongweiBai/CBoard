package org.cboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.solr.SolrAutoConfiguration;

/**
 * Cboard SpringBoot版本
 * <p>
 * 禁用Solr的自动配置
 */
@SpringBootApplication(exclude = SolrAutoConfiguration.class)
public class CboardApplication {

    public static void main(String[] args) {
        SpringApplication.run(CboardApplication.class, args);
    }

}

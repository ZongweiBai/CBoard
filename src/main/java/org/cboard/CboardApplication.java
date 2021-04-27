package org.cboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.solr.SolrAutoConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Cboard SpringBoot版本
 * <p>
 * 禁用Solr的自动配置
 */
@SpringBootApplication(exclude = SolrAutoConfiguration.class)
public class CboardApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(CboardApplication.class, args);
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println(passwordEncoder.encode("root123"));
    }
}

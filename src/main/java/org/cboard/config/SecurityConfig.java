package org.cboard.config;

import org.cboard.security.service.DbUserDetailService;
import org.cboard.security.service.DefaultAuthenticationService;
import org.cboard.security.service.ShareAuthenticationProviderDecorator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsUtils;

import javax.sql.DataSource;

/**
 * WebSecurity配置
 *
 * @author BaiZongwei
 * @date 2021/2/19 11:00
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //.cors().and()
                .csrf().disable()
                // 允许iframe嵌入
                .headers().frameOptions().disable().and()
                .authorizeRequests()
                // 处理跨域请求中的Preflight请求
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                // 静态资源不需要校验
                .antMatchers("/**.ico", "/lib/**", "/dist/**", "/bootstrap/**", "/plugins/**", "/druid/**",
                        "/js/**", "/login**", "/static/**", "/css/**", "/fonts/**", "/imgs/**", "/cboardApi/**", "/actuator/**",
                        "/api/*/authn/**", "/api-docs/webjars/**", "/api-docs/swagger-resources**", "/api-docs/v2/**",
                        "/**/**.js", "/**/**.json", "/**").permitAll()
                // 其他页面需要添加登录校验
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login.html").usernameParameter("username").passwordParameter("password")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/starter.html", true)
                .and()
                .logout().logoutUrl("/j_spring_cas_security_logout")
                .and()
                .rememberMe().rememberMeParameter("remember_me");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(shareAuthenticationProviderDecorator());
    }

    @Bean
    public ShareAuthenticationProviderDecorator shareAuthenticationProviderDecorator() {
        ShareAuthenticationProviderDecorator decorator = new ShareAuthenticationProviderDecorator();
        decorator.setAuthenticationProvider(daoAuthenticationProvider());
        return decorator;
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Override
    @Bean
    public DbUserDetailService userDetailsService() {
        DbUserDetailService userDetailService = new DbUserDetailService();
        userDetailService.setDataSource(dataSource);

        String authoritiesByUsernameQuery = "SELECT login_name username, 'admin' AS authority\n" +
                "                           FROM dashboard_user\n" +
                "                          WHERE login_name = ?";
        userDetailService.setAuthoritiesByUsernameQuery(authoritiesByUsernameQuery);

        String usersByUsernameQuery = "SELECT user_id,user_name,login_name, user_password, 1 AS enabled\n" +
                "                           FROM dashboard_user\n" +
                "                          WHERE login_name = ? ";
        userDetailService.setUsersByUsernameQuery(usersByUsernameQuery);
        return userDetailService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DefaultAuthenticationService dbAuthenticationService() {
        return new DefaultAuthenticationService();
    }
}

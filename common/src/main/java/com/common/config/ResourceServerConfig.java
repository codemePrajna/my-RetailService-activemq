package com.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests().antMatchers("/oauth/token", "/oauth/authorize**").permitAll();
//			 .anyRequest().authenticated();
        http.requestMatchers().antMatchers("/v1/**")
                .and().authorizeRequests()
                .antMatchers("/v1/**").access("hasRole('USER')")
                .and().requestMatchers().antMatchers("/admin/**")
                .and().authorizeRequests()
                .antMatchers("/admin/**").access("hasRole('ADMIN')");
    }

}

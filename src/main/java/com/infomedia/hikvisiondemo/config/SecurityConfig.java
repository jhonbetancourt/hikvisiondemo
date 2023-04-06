package com.infomedia.hikvisiondemo.config;


import com.infomedia.hikvisiondemo.util.ApiKeyFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
public class SecurityConfig {

    @Autowired
    private ApiKeyFilter.Validator apiKeyValidator;

    @Bean
    public AuthenticationManager authenticationManager(){
        return authentication -> authentication;
    }

    @Bean
    public SecurityFilterChain securityFilterChain1(HttpSecurity http) throws Exception {
        http.httpBasic().disable().csrf().disable()
                .requestMatchers()
                .antMatchers("/demo/login")
                .antMatchers("/demo/register")
                .and().authorizeRequests()
                .antMatchers("/demo/login").permitAll()
                .antMatchers("/demo/register").authenticated();
        return http.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain2(HttpSecurity http) throws Exception {
        http.httpBasic().disable()
                .anonymous().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .requestMatchers()
                .antMatchers("/demo/api/register")
                .antMatchers("/demo/api/hikcentral/**")
                .and().authorizeRequests()
                .anyRequest().authenticated().and()
                .addFilterBefore(new ApiKeyFilter(apiKeyValidator) , UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}

package com.moon.config;

import com.moon.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

@Log4j2
@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // http 시큐리티 빌더
        http.cors() // WebMvcConfig 에서 이미 설정했으므로 기본 cors 설정
                .and()
                .csrf().disable() // csrf 는 현재 사용하지 않으므로 disable
                .httpBasic().disable() // token을 사용하므로 basic 인증 disable
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Session 기반이 아님을 선언
                .and()
                .authorizeRequests().antMatchers("/", "/auth/**").permitAll() // /와 /auth/** 경로는 인증 안해도 됨
                .anyRequest()
                .authenticated(); // /와 /auth/** 이외의 모든 경로는 인증 해야 됨

        /*
            filter 등록
            매 요청마다 CorsFilter 실행한 후에 jwtAuthenticationFilter 실행한다.
         */
        http.addFilterAfter(jwtAuthenticationFilter, CorsFilter.class);

        return http.build();
    }

}

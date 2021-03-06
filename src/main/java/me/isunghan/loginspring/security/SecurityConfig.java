package me.isunghan.loginspring.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity  // 해당 애노테이션을 붙인 필터(현재 클래스)를 스프링 필터체인에 등록.
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    // spring security에서 제공하는 암호화 객체를 빈으로 등록
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    // WebSecurity에 필터를 거는 게 훨씬 빠르다. HttpSecrity에 필터를 걸면, 이미 스프링 시큐리티 내부에 들어와서 걸러지기 때문..
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().mvcMatchers("/members/**","/image/**");    // /image/** 있는 모든 파일들은 시큐리티 적용을 무시한다.
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());    // 정적인 리소스들에 대해서 시큐리티 적용 무시.
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                    .anyRequest()   // 모든 요청에 대해서
                    .permitAll()    // 허용하라
                .and()
                    .logout()   // 로그아웃에 대한 설정
                    .logoutSuccessUrl("/")  // 성공 시 이동 url
                .and()
                    .oauth2Login()  // oauth2 로그인에 대해서
                    .userInfoEndpoint() // 로그인에 성공한 유저 정보를
                    .userService(customOAuth2UserService)   // 지정한 서비스에서 후처리를 하겠다.
                .and()
                    .defaultSuccessUrl("/login-success");   // 로그인 성공시 이동 url
    }
}

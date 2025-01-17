package click.applemt.apmt.security;

import click.applemt.apmt.security.filter.AuthFilterContainer;
import click.applemt.apmt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

import java.io.File;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true,jsr250Enabled = true,prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    UserService userService;
    @Autowired
    private AuthFilterContainer authFilterContainer;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .cors().and()
                .authorizeRequests().antMatchers("/images/**","/**","static","/static").permitAll()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(authFilterContainer.getFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
    }

    @Override public void configure(WebSecurity web) throws Exception { // 인증 예외 URL설정
        String itemRoot = new File("").getAbsolutePath() + "\\src\\main\\resources\\static\\";

         web.ignoring().antMatchers(HttpMethod.GET,"/api/image").antMatchers(itemRoot)
                 .antMatchers(HttpMethod.GET, "/api/items")
                 .antMatchers(HttpMethod.GET,"/api/items/*");
    }

}

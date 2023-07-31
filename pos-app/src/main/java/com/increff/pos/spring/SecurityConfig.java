package com.increff.pos.spring;

import com.increff.pos.model.enums.UserRole;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http//
                // Match only these URLs
                .requestMatchers()//
                .antMatchers("/api/**")
                .antMatchers("/ui/**")
                .and()
                .authorizeRequests()
                .antMatchers("/api/session/**").permitAll()//
                .antMatchers(HttpMethod.GET, "/api/brands/**").hasAnyAuthority(UserRole.SUPERVISOR.name(), UserRole.OPERATOR.name())//
                .antMatchers("/api/brands/**").hasAuthority(UserRole.SUPERVISOR.name())//
                .antMatchers(HttpMethod.GET, "/api/products/**").hasAnyAuthority(UserRole.SUPERVISOR.name(), UserRole.OPERATOR.name())//
                .antMatchers("/api/products/**").hasAuthority(UserRole.SUPERVISOR.name())//
                .antMatchers(HttpMethod.GET, "/api/inventory/**").hasAnyAuthority(UserRole.SUPERVISOR.name(), UserRole.OPERATOR.name())//
                .antMatchers(HttpMethod.POST, "/api/inventory/check").hasAnyAuthority(UserRole.SUPERVISOR.name(), UserRole.OPERATOR.name())//
                .antMatchers("/api/inventory/**").hasAuthority(UserRole.SUPERVISOR.name())//
                .antMatchers("/api/orders/**").hasAnyAuthority(UserRole.SUPERVISOR.name(), UserRole.OPERATOR.name())//
                .antMatchers("/api/reports/**").hasAuthority(UserRole.SUPERVISOR.name())//
                .antMatchers("/api/**").hasAnyAuthority(UserRole.SUPERVISOR.name(), UserRole.OPERATOR.name())//
                .antMatchers("/ui/reports/**").hasAuthority(UserRole.SUPERVISOR.name())//
                .antMatchers("/ui/**").hasAnyAuthority(UserRole.SUPERVISOR.name(), UserRole.OPERATOR.name())//
                .anyRequest().authenticated()
                .and()
                // Ignore CSRF and CORS
                .csrf().disable()
                .cors().disable();
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources", "/configuration/security",
                "/swagger-ui.html", "/webjars/**");
    }

}
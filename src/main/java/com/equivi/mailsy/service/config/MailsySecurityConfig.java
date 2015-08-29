package com.equivi.mailsy.service.config;

import com.equivi.mailsy.data.dao.UserDao;
import com.equivi.mailsy.service.authentication.AuthenticationPredicate;
import com.equivi.mailsy.service.authentication.AuthenticationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class MailsySecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDao userDao;

    @Autowired
    private AuthenticationPredicate authenticationPredicate;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/main_theme/**").and()
                .ignoring().antMatchers("/forgetPassword/**").and()
                .ignoring().antMatchers("/unsubscribe/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().
                authorizeRequests().antMatchers("/main/merchant/**")
                .authenticated().and()
                .authorizeRequests().antMatchers("/main/**").authenticated().and()
                .authorizeRequests().antMatchers("/main/admin/**").hasRole("MAILSY_ADMIN").and()
                .authorizeRequests().antMatchers("/main/admin/**").authenticated().and()
                .formLogin().loginPage("/login-page").loginProcessingUrl("/login").defaultSuccessUrl("/main", true)
                .usernameParameter("username").passwordParameter("password").failureUrl("/login-error").and()
                .logout().invalidateHttpSession(true).logoutSuccessUrl("/login-page");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(new AuthenticationServiceImpl(userDao, authenticationPredicate)).passwordEncoder(new Md5PasswordEncoder());
    }
}

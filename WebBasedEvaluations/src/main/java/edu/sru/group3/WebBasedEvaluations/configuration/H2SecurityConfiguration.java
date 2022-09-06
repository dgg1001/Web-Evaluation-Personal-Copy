/*
package edu.sru.group3.WebBasedEvaluations.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


@Configuration
public class H2SecurityConfiguration extends WebSecurityConfigurerAdapter {

	 @Override
	    protected void configure(HttpSecurity http) throws Exception {
	        http.authorizeRequests()
	                .antMatchers("/").permitAll()
	                .antMatchers("/h2-console/**").permitAll()
	                .and().formLogin()
			        .loginPage("/login")
			        .permitAll()
			        .defaultSuccessUrl("/home2", true)
			        .and().logout(logout -> logout                                                
			                .logoutSuccessUrl("/home"));;

	        http.csrf().disable();
	        http.headers().frameOptions().disable();
	    }
    }
    */
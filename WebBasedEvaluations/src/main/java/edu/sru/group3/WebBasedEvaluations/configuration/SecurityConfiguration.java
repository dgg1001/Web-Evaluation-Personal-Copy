
package edu.sru.group3.WebBasedEvaluations.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Class for the security configuration where hash checks are performed as well
 * as authority access to particular webpages/mappings
 * 
 * @author Dalton Stenzel
 *
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	private static final String ADMIN = "ADMIN";
	private static final String EVALUATOR_EVAL = "EVALUATOR_EVAL";
	private static final String EVAL_ADMIN = "EVAL_ADMIN";
	private static final String EVALUATOR = "EVALUATOR";
	private static final String USER = "USER";

	// AbstractUserDetailsAuthenticationProvider customAuthProvider;

	@Autowired
	private UserDetailsService userDetailsService;

	/**
	 * Some sort of method tagged to be managed by the Spring container for
	 * providing a password encoder using BCrypt
	 * 
	 * @return provider instance containing a userDetailsService and a
	 *         BCryptPasswordEncoder.
	 */
	@Bean
	AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService);
		provider.setPasswordEncoder(new BCryptPasswordEncoder());
		return provider;
	}

	/*
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService);
		// auth.authenticationProvider(customAuthProvider).authenticationProvider(authenticationProvider());

		// auth.inMemoryAuthentication().withUser("memuser").password(encoder().encode("pass")).roles("USER");
	}
	*/

	/**
	 * Method for granting users with particular roles access to particular
	 * webpages/mappings as well as setting up where the login page drops users
	 *
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		// Added to permit multipart file upload
		http.cors().and().csrf().disable();
		http.csrf().disable();
		http.headers().frameOptions().disable();
		http.authorizeRequests().antMatchers("/edit/*").hasAuthority(ADMIN)
				// .antMatchers("/pages/*").hasAuthority(ADMIN)

				// .antMatchers("/register").permitAll()
				// .antMatchers("/registerUser").permitAll()

				.antMatchers("/email_sent").permitAll().antMatchers("/reset").permitAll().antMatchers("/resetting")
				.permitAll().antMatchers("/changePassword").permitAll().antMatchers("/recoverChanges/*").permitAll()

				.antMatchers("/adduser").hasAuthority(ADMIN).antMatchers("/download_log_txt").hasAuthority(ADMIN).antMatchers("/addgroup").hasAuthority(ADMIN)
				// .antMatchers("/signup").hasAuthority(ADMIN)

				.antMatchers("/admin_users/").hasAuthority(ADMIN).antMatchers("/admin_evaluations")
				.hasAnyAuthority(EVAL_ADMIN, EVALUATOR_EVAL, EVALUATOR).antMatchers("/admin_groups")
				.hasAnyAuthority(EVAL_ADMIN, EVALUATOR_EVAL, EVALUATOR).antMatchers("/admin_home").hasAuthority(ADMIN)
				// .antMatchers("/base").permitAll()
				.antMatchers("/home").hasAnyAuthority(ADMIN, USER, EVALUATOR, EVAL_ADMIN, EVALUATOR_EVAL)

				.antMatchers("/").hasAnyAuthority(USER, ADMIN, EVAL_ADMIN).antMatchers("/h2-console/**")
				.hasAuthority(ADMIN).antMatchers("/").permitAll().antMatchers("/h2-console/**").hasAuthority(ADMIN)
				.antMatchers("/h2-console/login.do").hasAuthority(ADMIN).anyRequest().authenticated().and().formLogin()
				.loginPage("/login").permitAll().defaultSuccessUrl("/logging", true).and().logout()
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/logout.done")
				.deleteCookies("JSESSIONID").invalidateHttpSession(true);
	}

	/*
	 * @Bean public PasswordEncoder getPasswordEncoder() { return
	 * NoOpPasswordEncoder.getInstance(); }
	 */
}

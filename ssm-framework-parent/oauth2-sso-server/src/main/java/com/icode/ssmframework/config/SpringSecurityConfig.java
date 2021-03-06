package com.icode.ssmframework.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.icode.ssmframework.security.authentication.dao.SysDaoAuthenticationProvider;
import com.icode.ssmframework.security.userdetials.SysJdbcUserDetailService;
import com.icode.ssmframework.sso.filter.SSOServerLoginFilter;

/**
 * Oauth资源服务器启动的SpringSecurity顺序是3默认EnableWebSecurity启动顺序是100（深坑）
 * 所以这里要声明一个Order为2的，保证应用本身SpringSecurity在资源服务器前面
 * @author 许小宝
 *
 */
@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
	private Logger logger = LoggerFactory.getLogger(SpringSecurityConfig.class); 
	@Bean
	public UserDetailsService sysJdbcUserDetailService() {
		return new SysJdbcUserDetailService();
	}
	@Bean
	public PasswordEncoder passwordEnCoder() {
		return new BCryptPasswordEncoder();
	}
	@Bean 
	public SysDaoAuthenticationProvider daoAuthenticationProvider() {
		SysDaoAuthenticationProvider daoAuthenticationProvider = new SysDaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(sysJdbcUserDetailService());
		daoAuthenticationProvider.setPasswordEncoder(passwordEnCoder());
		daoAuthenticationProvider.setHideUserNotFoundExceptions(false);
		return daoAuthenticationProvider;
	}
	@Bean
	public UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter() throws Exception {
		SSOServerLoginFilter filter = new SSOServerLoginFilter();
		filter.setAuthenticationManager(authenticationManagerBean());
		filter.setAllowSessionCreation(true);
		filter.setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler("/login?error"));
//		filter.setAuthenticationSuccessHandler(new SimpleUrlAuthenticationSuccessHandler("/"));//成功登陆后跳转策略
		return filter;
	}
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(daoAuthenticationProvider());
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		logger.debug("SpringSecurity安全配置....");
		http
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
		.and()
		.addFilterBefore(usernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
		.regexMatcher("^(?!/api/).*$")	//匹配不以/api/开头的路径
		.authorizeRequests()
		.antMatchers("/login").permitAll()
		.antMatchers("/**").hasAnyRole("USER","ADMIN")
		.and()
			.formLogin()
				.loginPage("/login")
				.permitAll()
		.and()
			.logout()
				.invalidateHttpSession(true)
				.logoutSuccessUrl("/")
				.logoutUrl("/logout")
				.deleteCookies("JSESSIONID")
			;
	}
	@Override
	public void configure(WebSecurity web) throws Exception {
//		web.ignoring().antMatchers("/statics/**","/app/**","/oauth/**");
		web.ignoring().antMatchers("/statics/**");
	}
	
}

/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drugis.trialverse.config;

import org.drugis.trialverse.security.SimpleSocialUsersDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.social.UserIdSource;
import org.springframework.social.security.AuthenticationNameUserIdSource;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.social.security.SpringSocialConfigurer;

import javax.inject.Inject;
import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private ApplicationContext context;

  @Inject
  @Qualifier("dsTrialverse")
  private DataSource dataSource;

  @Override
  protected void registerAuthentication(AuthenticationManagerBuilder auth) throws Exception {
    auth.jdbcAuthentication()
            .dataSource(dataSource)
            .usersByUsernameQuery("SELECT username, password, TRUE FROM Account WHERE username = ?")
            .authoritiesByUsernameQuery("SELECT Account.username, COALESCE(AccountRoles.role, 'ROLE_USER') FROM Account" +
                    " LEFT OUTER JOIN AccountRoles ON Account.id = AccountRoles.accountId WHERE Account.username = ?")
            .passwordEncoder(passwordEncoder());
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    web
            .ignoring()
            .antMatchers("/resources/**");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
            .formLogin()
            .loginPage("/signin")
            .loginProcessingUrl("/signin/authenticate")
            .failureUrl("/signin?param.error=bad_credentials")
            .defaultSuccessUrl("/")
            .and().logout()
            .logoutUrl("/signout")
            .deleteCookies("JSESSIONID")
            .and().authorizeRequests()
            .antMatchers("/", "/favicon.ico", "/app/**", "/auth/**", "/signin", "/signup").permitAll()
            .antMatchers("/monitoring").hasRole("MONITORING")
            .antMatchers("/**").authenticated()
            .and().rememberMe()
            .and().exceptionHandling()
            .authenticationEntryPoint(new Http403ForbiddenEntryPoint())
            .and().apply(
            new SpringSocialConfigurer()
                    .postLoginUrl("/")
                    .alwaysUsePostLoginUrl(true))
            .and().setSharedObject(ApplicationContext.class, context);
  }

  @Bean
  public SocialUserDetailsService socialUsersDetailService() {
    return new SimpleSocialUsersDetailService(userDetailsService());
  }

  @Bean
  public UserIdSource userIdSource() {
    return new AuthenticationNameUserIdSource();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }


}

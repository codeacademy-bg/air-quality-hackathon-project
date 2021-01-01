package bg.startit.hackathon.airquiality.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  public void configure(HttpSecurity http) throws Exception {
    // @formatter:off
    http
        .cors()
          .and()
        .csrf()
          .disable()
        .formLogin()
          .loginPage("/login.html")
          .loginProcessingUrl("/login")
          .defaultSuccessUrl("/")
          .permitAll()
          .and()
        .logout()
          .and()
        .headers()
          .frameOptions().sameOrigin().and()
        .authorizeRequests()
          // allow static resources
          .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
          // don't render index page unless logged in
          .antMatchers("/").authenticated()
          .antMatchers("/index.html*").authenticated()
          // allow user registration
          .antMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
          // protect API
          .antMatchers( "/api/v1/**").authenticated()
    ;
    // @formatter:on
  }

  @Bean
  PasswordEncoder getPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

}

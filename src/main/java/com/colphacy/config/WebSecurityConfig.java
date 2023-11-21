package com.colphacy.config;

import com.colphacy.security.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Qualifier("employeeDetailsService")
    private UserDetailsService employeeDetailsService;
    private JwtFilter jwtFilter;

    @Qualifier("customerDetailsService")
    private UserDetailsService customerDetailsService;
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public void setEmployeeUserDetailsService(UserDetailsService employeeDetailsService) {
        this.employeeDetailsService = employeeDetailsService;
    }

    @Autowired
    public void setCustomerUserDetailsService(UserDetailsService customerDetailsService) {
        this.customerDetailsService = customerDetailsService;
    }

    @Autowired
    public void setJwtFilter(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(employeeDetailsService).passwordEncoder(passwordEncoder());
        auth.userDetailsService(customerDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests()
                .antMatchers("/api/auth/employee/login", "/api/auth/customer/login").permitAll()
                .antMatchers("/api/docs").permitAll()
                .antMatchers("/api/swagger-ui/**").permitAll()
                .antMatchers("/api/docs.json/**").permitAll()
                .antMatchers(("/api/products/best-sellers")).permitAll()
                .antMatchers(("/api/products/customers")).permitAll()
                .antMatchers("/api/auth/employee/logout").hasAnyAuthority("STAFF", "ADMIN")
                .antMatchers("/api/employees/profile/**", "/api/employees/change-password").hasAnyAuthority("ADMIN", "STAFF")
                .antMatchers("/api/location/**").hasAnyAuthority("ADMIN", "STAFF")
                .antMatchers("/api/units/all").permitAll()
                .antMatchers("/api/units/**").hasAnyAuthority("ADMIN", "STAFF")
                .antMatchers("/api/categories/all").permitAll()
                .antMatchers("/api/categories/**").hasAnyAuthority("ADMIN", "STAFF")
                .antMatchers("/api/providers").hasAnyAuthority("ADMIN", "STAFF")
                .antMatchers("/api/branches/**").hasAuthority("ADMIN")
                .antMatchers("/api/employees/**").hasAnyAuthority("ADMIN")
                .antMatchers(HttpMethod.GET, "/api/categories").authenticated()
                .antMatchers("/api/carts").hasAuthority("CUSTOMER")
                .antMatchers("/api/roles").hasAnyAuthority("ADMIN")
                .anyRequest().authenticated();
        http.exceptionHandling()
                .authenticationEntryPoint(
                        (request, response, ex) ->
                            response.sendError(
                                    HttpServletResponse.SC_UNAUTHORIZED,
                                    ex.getMessage()
                            )
                );
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }
}

package com.colphacy.security;

import com.colphacy.service.CustomerService;
import com.colphacy.service.EmployeeService;
import com.colphacy.service.LoggedTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private JwtUtil jwtUtil;
    private EmployeeService employeeService;
    private CustomerService customerService;
    private LoggedTokenService loggedTokenService;

    @Autowired
    public void setJwtUtil(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Autowired
    public void setLoggedTokenService(LoggedTokenService loggedTokenService) {
        this.loggedTokenService = loggedTokenService;
    }

    @Autowired
    public void setEmployeeDetailsService(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Autowired
    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtFilter.class);
    private final RequestMatcher loginRequestsPattern = new AntPathRequestMatcher("/api/auth/**/login");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!this.loginRequestsPattern.matches(request)) {
            String token = getAccessToken(request);
            if (token != null && jwtUtil.validateAccessToken(token) && loggedTokenService.findByToken(token).isEmpty()) {
                String id = jwtUtil.getUserIdFromAccessToken(token);
                String authority = jwtUtil.getAuthorityFromAccessToken(token);
                UserDetails userDetails;
                if ("CUSTOMER".equals(authority)) {
                    userDetails = customerService.findById(Long.parseLong(id));
                } else {
                    userDetails = employeeService.findById(Long.parseLong(id));
                }
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails((new WebAuthenticationDetailsSource()).buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }


    private String getAccessToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        return !ObjectUtils.isEmpty(header) && header.startsWith("Bearer ") ? header.split(" ")[1].trim() : null;
    }
}
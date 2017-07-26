package com.patriotenergygroup.peauthservice.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import com.patriotenergygroup.peauthservice.service.TokenAuthenticationService;

public class JwtAuthenticationFilter extends GenericFilterBean {

	private TokenAuthenticationService tokenAuthenticationService;
	
	public JwtAuthenticationFilter(TokenAuthenticationService service) {
		this.tokenAuthenticationService = service;
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		Authentication authentication = 
				tokenAuthenticationService.getAuthentication((HttpServletRequest) request);

		SecurityContextHolder.getContext().setAuthentication(authentication);
		filterChain.doFilter(request, response);
	}
}

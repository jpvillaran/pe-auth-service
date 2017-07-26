package com.patriotenergygroup.peauthservice.filter;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.patriotenergygroup.peauthservice.domain.AccountCredentials;
import com.patriotenergygroup.peauthservice.service.TokenAuthenticationService;

public class JwtLoginFilter extends AbstractAuthenticationProcessingFilter {
	
	private TokenAuthenticationService tokenAuthenticationService;
	
	public JwtLoginFilter(String url, AuthenticationManager authManager, TokenAuthenticationService service) {
		super(new AntPathRequestMatcher(url));
		this.tokenAuthenticationService = service;
		setAuthenticationManager(authManager);
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
			throws AuthenticationException, IOException, ServletException {
		AccountCredentials creds = new ObjectMapper().readValue(req.getInputStream(), AccountCredentials.class);
		return getAuthenticationManager().authenticate(
				new UsernamePasswordAuthenticationToken(
						creds.getUsername(),
						creds.getPassword(), 
						Collections.emptyList()));
	}

	@Override
	protected void successfulAuthentication(
			HttpServletRequest req, HttpServletResponse res, 
			FilterChain chain, Authentication auth) throws IOException, ServletException {
		
		tokenAuthenticationService.addAuthentication(res, auth);
	}
	
}

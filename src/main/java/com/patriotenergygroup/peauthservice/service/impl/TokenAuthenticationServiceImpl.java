package com.patriotenergygroup.peauthservice.service.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.patriotenergygroup.peauthservice.configuration.AuthProperties;
import com.patriotenergygroup.peauthservice.service.TokenAuthenticationService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Collection;
import java.util.Collections;

@Service
public class TokenAuthenticationServiceImpl implements TokenAuthenticationService {
	private final Logger logger = LoggerFactory.getLogger(TokenAuthenticationServiceImpl.class);
	
	private static final String ROLE_KEY = "roles";
	
	@Autowired
	private AuthProperties properties;
	
	public void addAuthentication(HttpServletResponse res, Authentication auth) {
		
        Claims claims = Jwts.claims().setSubject(auth.getName());
        claims.put(ROLE_KEY, auth.getAuthorities().stream().map(s -> s.toString()).collect(Collectors.toList()));
		
		String JWT = Jwts.builder()
		        .setClaims(claims)
		        .setExpiration(new Date(System.currentTimeMillis() + properties.getExpiration()))
		        .signWith(SignatureAlgorithm.HS512, properties.getSecret())
		        .compact();
		res.addHeader(properties.getHeaderString(), properties.getTokenPrefix() + " " + JWT);
	}
	
	public Authentication getAuthentication(HttpServletRequest request) {
		String token = request.getHeader(properties.getHeaderString());
		if (token != null) {
			// parse the token.
			try {
				Claims claims = Jwts.parser()
						.setSigningKey(properties.getSecret())
						.parseClaimsJws(token.replace(properties.getTokenPrefix(), "")).getBody(); 
				String user = claims.getSubject();
				
				List<?> roles = claims.get(ROLE_KEY, List.class);
				
				Collection<? extends GrantedAuthority> authorities =
					roles != null
						? roles.stream()
							.map(authority -> new SimpleGrantedAuthority(authority.toString()))
							.collect(Collectors.toList())
						: Collections.emptyList();


				return user != null 
						? new UsernamePasswordAuthenticationToken(user, null, authorities) 
						: null;
			}
			catch (MalformedJwtException ex) {
				logger.info("Malformed token.");
				return null;
			}
		}
		return null;
	}
	
}

package com.patriotenergygroup.peauthservice.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;

public interface TokenAuthenticationService {
	void addAuthentication(HttpServletResponse res, Authentication auth);
	Authentication getAuthentication(HttpServletRequest request);
}

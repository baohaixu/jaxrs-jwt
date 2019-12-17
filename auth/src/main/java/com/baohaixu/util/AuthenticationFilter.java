package com.baohaixu.util;

import java.io.IOException;
import java.security.Principal;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.DatatypeConverter;

import com.baohaixu.controller.UserController;
import com.baohaixu.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter{
	private static final String REALM = "example";
    public static final String AUTHENTICATION_SCHEME = "Bearer";
    
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
		if (!isTokenBasedAuthentication(authorizationHeader)) {
			abortWithUnauthorized(requestContext);
			return;
		}
		
		String token = authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).trim();
		User user = new User();
		try {
			validateToken(token, user);
		} catch (Exception e) {
			abortWithUnauthorized(requestContext);
		}
		
		requestContext.setSecurityContext(new SecurityContext() {
			
			@Override
			public boolean isUserInRole(String role) {
				// TODO Auto-generated method stub
				return true;
			}
			
			@Override
			public boolean isSecure() {
				// TODO Auto-generated method stub
				return true;
			}
			
			@Override
			public Principal getUserPrincipal() {
				// TODO Auto-generated method stub
				return user;
			}
			
			@Override
			public String getAuthenticationScheme() {
				// TODO Auto-generated method stub
				return AUTHENTICATION_SCHEME;
			}
		});
	}

	private void validateToken(String token, User user) throws Exception {
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(UserController.SECRET_KEY);
		Claims claims = Jwts.parser().setSigningKey(apiKeySecretBytes).parseClaimsJws(token).getBody();
		String scope = claims.get("scope").toString();
		if (!scope.equals("admins")) {
			throw new Exception("invalid");
		}
		String name = claims.get("name").toString();
        user.setName(name);
	}

	private void abortWithUnauthorized(ContainerRequestContext requestContext) {
		requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
				.header(HttpHeaders.WWW_AUTHENTICATE, 
						AUTHENTICATION_SCHEME + " realm=\"" + REALM + "\"")
				.build());
		
	}

	private boolean isTokenBasedAuthentication(String authorizationHeader) {
		return authorizationHeader != null
				&& authorizationHeader.toLowerCase().startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
	}

}

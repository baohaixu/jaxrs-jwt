package com.baohaixu.controller;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;

import com.baohaixu.model.JWT;
import com.baohaixu.model.LoginUser;
import com.baohaixu.model.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Stateless
public class UserController {
	public static final String SECRET_KEY = "secret";

	@PersistenceContext(name = "primary")
	private EntityManager em;

	@GET
	public Response getAllUsers() {
		TypedQuery<User> query = em.createQuery(User.QUERY_ALL, User.class);
		List<User> users = query.getResultList();
		return Response.ok(users).build();
	}

	@POST
	@Path("login")
	public Response login(LoginUser loginUser) {
		TypedQuery<User> query = em.createQuery("from User u where u.email=:email", User.class);
		query.setParameter("email", loginUser.getEmail());
		boolean authenticated = false;
		JWT jwt = new JWT();
		try {
			User user = query.getSingleResult();
			authenticated = user.verifyPassword(loginUser.getPassword());
			if (authenticated) {				
				String token = issueToken(user);
				jwt.setSuccess(true);
				jwt.setToken(token);
			} else {
				jwt.setSuccess(false);
			}
			
		} catch (NoResultException e) {
			jwt.setSuccess(false);
		}
        return Response.ok(jwt).build();
	}

	@POST
	public Response registerUser(User user) {
		em.persist(user);
		return Response.ok(user).build();
	}

	private String issueToken(User user) {
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY);

		return Jwts.builder()
				.claim("name", user.getName())
				.claim("password", user.getPassword())
				.claim("scope", "admins")
				.signWith(SignatureAlgorithm.HS256, 
						apiKeySecretBytes)
				.compact();
	}

}

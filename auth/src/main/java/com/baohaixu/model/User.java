package com.baohaixu.model;

import java.io.Serializable;
import java.security.Principal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.baohaixu.util.Password;

import javax.persistence.GenerationType;

@Entity
@Table(name = "users")
public class User implements Serializable, Principal {
	private static final long serialVersionUID = 1L;
    public static final String QUERY_ALL = "select u from User u";
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "salt")
	private String salt;

	@Column(name = "password")
	private String password;
	
	public String getSalt() {
		return salt;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.salt = Password.getSalt(30);
		String securePassword = Password.generateSecurePassword(password, this.salt);
		this.password = securePassword;
	}

	public boolean verifyPassword(String providedPassword) {
		return Password.verifyUserPassword(providedPassword, this.password, this.salt);
	}
	
	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", email=" + email + ", password=" + password + "]";
	}
	
	
}

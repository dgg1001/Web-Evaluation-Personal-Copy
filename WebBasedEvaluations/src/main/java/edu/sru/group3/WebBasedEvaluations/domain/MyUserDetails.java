package edu.sru.group3.WebBasedEvaluations.domain;


import java.util.stream.Collectors;
import java.util.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Domain class for obtaining the currently logged in user
 * @author Dalton
 *
 */
public class MyUserDetails implements UserDetails {
	private User user;
	 private String userName;
	 private String password;
	 private String role;
	 private long Id;
	    //private boolean active;
	 private List<GrantedAuthority> authorities;
	
	 
	  public MyUserDetails(User user) {
		  	this.Id = user.getId(); 
	        this.userName = user.getEmail();
	        this.password = user.getPassword();
	        this.role = user.getRoles();

	        //this.active = user.isActive();
	        this.authorities = Arrays.stream(user.getRoles().split(","))
	                    .map(SimpleGrantedAuthority::new)
	                    .collect(Collectors.toList());
	    }
	/**
	 *A method for obtaining authority based of relation from user's role
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return authorities;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return password;
	}
	public long getID() {
	
		return Id;
	}
	
	public String getRoles() {
		// TODO Auto-generated method stub
		return role;
	}
	
	public String getUsername() {
		// TODO Auto-generated method stub
		return userName;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

}

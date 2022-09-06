package edu.sru.group3.WebBasedEvaluations.domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

/**Class for creating verification token objects for password resetting.
 * @author Dalton Stenzel
 *
 */
@Entity
@Data
@NoArgsConstructor
@Table(name ="Verification_Token")
public class PasswordResetToken {
	//expiration time is in minutes
    private static final int EXPIRATION = 3;
 
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
 
    private String token;
    
    private long userID;
    
    private LocalDate localDate;
    
    private LocalTime expireTime;
    



	@OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "id")
    private User user;
 
    //private Date expiryDate;

	public PasswordResetToken(String token, User user) {
		this.token=token;
		this.user=user;
		this.userID=user.getId();
		this.localDate=LocalDate.now();
		this.expireTime=LocalTime.now().plusMinutes(EXPIRATION);
		// TODO Auto-generated constructor stub
	}
	public PasswordResetToken() {

		// TODO Auto-generated constructor stub
	}

	/*
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	*/
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public long getUserIdReset() {
		return userID;
	}

	public void setUserIdReset(long userIdReset) {
		this.userID = userIdReset;
	}
	
	
	public void setExpireTime(LocalDate localDate, LocalTime expireTime) {
		this.localDate = localDate;
		this.expireTime = expireTime;
	}
	
	public LocalTime getExpireTime() {
		return expireTime;
	}
	
	public LocalDate getExpiredDate() {
		return localDate;
	}

}
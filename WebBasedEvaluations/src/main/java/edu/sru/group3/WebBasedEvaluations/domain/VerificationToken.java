package edu.sru.group3.WebBasedEvaluations.domain;

import java.util.Calendar;
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


/*
@Entity
@Data
@NoArgsConstructor
@Table(name ="Verification_Token")
public class VerificationToken {

	private static final int EXPIRATION_TIME = 10;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String token;

	private Date expirationTime;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false /* , foreignKey = @ForeignKey (name = "FK_USER_VERIFY_TOKEN") *//*)
/*
	private User user;

	public VerificationToken(User user, String token) {
		super();
		this.token = token;
		this.user = user;
		this.expirationTime = calculateExpirationDate(EXPIRATION_TIME);
	}
	/*
	public VerificationToken(String token) {
		super();
		this.token = token;
		this.expirationTime = calculateExpirationDate(EXPIRATION_TIME);
	}*/
/*
	/**Method for calculating the expiration time of the token.
	 * @param expirationTime is the time in minutes for when the token will expire.
	 * @return date and time that the token will expire.
	 *//*
	private Date calculateExpirationDate(int expirationTime) {
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(new Date().getTime());
		calendar.add(Calendar.MINUTE, expirationTime);
		// TODO Auto-generated method stub
		return new Date(calendar.getTime().getTime());
	}
}*/


package edu.sru.group3.WebBasedEvaluations.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**Class that creates and hold evaluation made by the reviewee
 * id is auto generated 
 * DateEdited hold date when ever a user edits there self evaluation
 * Reviewee is the user who  created the self evaluation 
 * path hold the the evaluation data 
 * Complete determines if the selfevaluation is don or not 
 */
@Entity
@Table(name ="SelfEvaluation")
public class SelfEvaluation {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
	private Date dateEdited;
	@OneToOne
	private Reviewee reviewee;
	@Lob
	private byte[] path;
	private boolean completed;
	
	public SelfEvaluation(){
		
	}
	
	public SelfEvaluation (Reviewee reviewee){
		this.reviewee =reviewee;
		this.completed = false;
	}

	public  Reviewee  getReviewee() {
	return reviewee;
}
public void setReviewee (Reviewee reviewee) {
	this.reviewee = reviewee;
}
	public byte[] getPath() {
		return path;
	}
	public void setPath(byte[] path) {
		this.path = path;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Date getDateEdited() {
		return dateEdited;
	}
	public void setDateEdited(Date date) {
		this.dateEdited = date;
	}

	public boolean getCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	

}

package edu.sru.group3.WebBasedEvaluations.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
/**
 * Archive 
 *  is the deleted evaluation
 *
 */
@Entity
public class Archive {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
	
	private Date dateEdited;
	
	private String evaluator;
	private String role;
	private String reviewee;
	
	@Lob
	private byte[] path;

		
	public  Archive() {

	}
	
	/**
	 * @param log is EvaluationLog
	 */
	public  Archive(EvaluationLog log) {
		this.dateEdited = log.getDateEdited();
		this.evaluator =log.getEvaluator().getUser().getName();
		this.path =  log.getPath();
		this.reviewee= log.getReviewee().getUser().getName();
		this.role =log.getEvaluator().getLevel().getName();

	}
	/**
	 * @param log is SelfEvaluation
	 */
	public  Archive(SelfEvaluation log) {
		this.dateEdited = log.getDateEdited();
		this.evaluator =log.getReviewee().getUser().getName();
		this.path =  log.getPath();
		this.reviewee= log.getReviewee().getUser().getName();
		this.role ="self Evaluation";

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

	public void setDateEdited(Date dateEdited) {
		this.dateEdited = dateEdited;
	}

	public String getEvaluator() {
		return evaluator;
	}

	public void setEvaluator(String evaluator) {
		this.evaluator = evaluator;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getReviewee() {
		return reviewee;
	}

	public void setReviewee(String reviewee) {
		this.reviewee = reviewee;
	}

	public byte[] getPath() {
		return path;
	}

	public void setPath(byte[] path) {
		this.path = path;
	}


}

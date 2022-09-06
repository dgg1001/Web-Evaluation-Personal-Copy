package edu.sru.group3.WebBasedEvaluations.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
/**EvaluationLog
 *  stores evaluation of a reviewee made by an evaluator 
 *
 */
@Entity
@Table(name ="EvaluationLog")
public class EvaluationLog {
	
	/**
	 * id is auto generated
	 * dateEdited is the date it was last edited 
	 * evaluator is the evaluator associated with the the evaluation log
	 * Reviewee is the reviewee associated with the evaluation log 
	 * path stores the evaluation
	 * auth determines if user allowed to make an evaluation
	 * completed determine if evaluation is complete 
	 * 
	 */
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
	
	private Date dateEdited;
	@ManyToOne()
    @JoinColumn()
	private Evaluator evaluator;
	
	@ManyToOne()
    @JoinColumn()
	private Reviewee reviewee;
	@Lob
	private byte[] path;
	@Lob
	private byte[] attach;
	private Boolean auth;
	private String attachname;
	private Boolean completed;
public EvaluationLog(){
		
	}
	public EvaluationLog(Evaluator eval1){
		this.evaluator = eval1;
	}

/** EvaluationLog constructor
 * @param evaluator  is the evaluator associated with the the evaluation log
 * if evaluator  level id is equal to 1 the auth is true else its false 
 * @param reviewee is the reviewee associated with the evaluation log 
 */
public EvaluationLog(Evaluator evaluator, Reviewee reviewee){
	
		this.evaluator = evaluator;
		this.reviewee = reviewee;
		// DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		  //Date date = new Date();
		//this.datecompleted = new Date() ;
		if(evaluator.getLevel().getId() ==1) {
			this.auth =true;
		}else {
			this.auth = false;
		}
		this.completed = false;
	}

	

	public Evaluator getEvaluator() {
		return evaluator;
	}
	public void setEvaluator(Evaluator evaluator) {
		this.evaluator = evaluator;
	}
	public Reviewee getReviewee() {
		return reviewee;
	}
	public void setReviewee(Reviewee reviewee) {
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
	public Boolean getAuth() {
		return auth;
	}
	public void setAuth(Boolean auth) {
		this.auth = auth;
	}
	
	public Boolean getCompleted() {
		return completed;
	}
	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}
	public byte[] getAttach() {
		return attach;
	}
	public void setAttach(byte[] attach) {
		this.attach = attach;
	}
	public String getAttachname() {
		return attachname;
	}
	public void setAttachname(String attachname) {
		this.attachname = attachname;
	}
	

}

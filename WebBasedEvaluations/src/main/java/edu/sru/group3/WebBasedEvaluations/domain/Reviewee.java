package edu.sru.group3.WebBasedEvaluations.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

/**Reviewee
 *   is the user that being evaluated 
 *
 */
@Entity
@Table(name ="Reviewee")
public class Reviewee {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@ManyToOne
	@JoinColumn(name="group_id")
	private Group group;
	private String name;
	@OneToOne
	@JoinColumn(name="user_id")
	private User user;
	/**
	 * id is auto generated
	 * group is the evaluation group the reviewee is associated with
	 * user  is the reviewee account
	 */
	@OneToMany(mappedBy = "reviewee",cascade = CascadeType.ALL)
	
 	private List<EvaluationLog> evalutationLog= new ArrayList<>();
	
	@OneToOne(mappedBy="reviewee")
    private SelfEvaluation selfEvaluation;
	
	public Reviewee() {
		
	}
	/**Reviewee constructor 
	 * @param group is the evaluation group the reviewee is associated with
	 * @param name  is the name of the reviewee
	 * @param user is the reviewee account
	 */
	public Reviewee(Group group, String name, User user) {
		
		this.user  = user;
		this.name = name;
		this.group =group;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Group getGroup() {
		return group;
	}
	public void setGroup(Group group) {
		this.group = group;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public List<EvaluationLog> getEvalutationLog() {
		return evalutationLog;
	}
	public void setEvalutationLog(List<EvaluationLog> evalutationLog) {
		this.evalutationLog = evalutationLog;
	}
	public SelfEvaluation getSelfEvaluation() {
		return selfEvaluation;
	}
	public void setSelfEvaluation(SelfEvaluation selfEvaluation) {
		this.selfEvaluation = selfEvaluation;
	}
	
	
}

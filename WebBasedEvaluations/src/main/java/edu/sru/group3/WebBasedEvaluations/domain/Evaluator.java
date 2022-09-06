package edu.sru.group3.WebBasedEvaluations.domain;

import java.util.*;

import javax.persistence.*;
/**Evaluator
 * is the user that doing  the Evaluation
 *
 */
@Entity
@Table(name ="Evaluator")
public class Evaluator {

    /**
      * id is auto generated
	 * group is the evaluation group the Evaluator  is associated with
	 * user  is the Evaluator  account
	 * sync determines if the evaluator after has to wait for the current evaluator to finish or not 
	 * level determine what role the evaluator has
	 * evalutationLog determine what evaluation Log is accosited with the evaluator 
     */
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	long id;
	
	@ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;

	@ManyToOne
	@JoinColumn(name="group_id")
	private Group group;
    
    private boolean sync;
    private boolean preview;
  
    @ManyToOne()
    @JoinColumn(name = "level")
    private EvalRole level;
    
    
    @OneToMany(mappedBy = "evaluator",cascade = CascadeType.ALL)
 	private List<EvaluationLog> evalutationLog= new ArrayList<>();
    
    
	public Evaluator() {
		
	}
	/**
	 * @param user is the evaluator
	 * @param group is the evaluation group the evaluator is associated  with 
	 * @param level is the role the evaluator  is responsible for 
	 */
	public Evaluator(User user ,Group group, EvalRole level) {
	
		this.user = user;
		this.group = group;
		this.level=level;
	
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Group getGroup() {
		return group;
	}
	public void setGroup(Group group) {
		this.group = group;
	}
	public List<EvaluationLog> getEvalutationLog() {
		return evalutationLog;
	}
	public void setEvalutationLog(List<EvaluationLog> evalutationLog) {
		this.evalutationLog = evalutationLog;
	}
	public void appendEvalutationLog(EvaluationLog evalutationLog) {
		this.evalutationLog.add(evalutationLog);
	}
	public EvalRole getLevel() {
		return level;
	}
	public void setLevel(EvalRole level) {
		this.level = level;
	}
	public boolean isSync() {
		return sync;
	}
	public void setSync(boolean sync) {
		this.sync = sync;
	}
	public boolean isPreview() {
		return preview;
	}
	public void setPreview(boolean preview) {
		this.preview = preview;
	}
	
	
}

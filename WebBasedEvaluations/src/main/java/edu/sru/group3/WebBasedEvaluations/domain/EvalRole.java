package edu.sru.group3.WebBasedEvaluations.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 * Eval roles is the roles being used in the evaluation 
 *
 */
@Entity
public class EvalRole {
	
	@Id
	private int id;
	private String  name;
	
	@OneToMany(mappedBy = "level",
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	private List<Evaluator> evaluator= new ArrayList<>();
	public EvalRole() {
		
	}
	
	/**
	 * @param name is the name of the role 
	 * @param id the the level this role is at
	 */
	public EvalRole(String name, int id) {
		
		this.id = id;
		this.name =name;
		
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Evaluator> getEvaluator() {
		return evaluator;
	}
	public void setEvaluator(List<Evaluator> evaluator) {
		this.evaluator = evaluator;
	}


}

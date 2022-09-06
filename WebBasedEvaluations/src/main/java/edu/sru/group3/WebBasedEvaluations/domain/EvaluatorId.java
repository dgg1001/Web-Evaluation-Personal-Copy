package edu.sru.group3.WebBasedEvaluations.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class EvaluatorId implements Serializable{
	
	//@Column(name = "user_id")
   // private long userId;


	@Column(name = "group_id")
    private long groupId;
	
	@Column(name = "level")
    private int level;
	
    public EvaluatorId(){
    	
    }
  
   // long userId,
    public EvaluatorId( long groupId ,int level) {
		//this.userId = userId;
		this.groupId = groupId;
		this.level = level;
	}


	//public long getUserId() {
	//	return userId;
	//}
	//public void setUserId(long userId) {
	//	this.userId = userId;
//	}
	public long getGroupId() {
		return groupId;
	}
	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}


	public int getLevel() {
		return level;
	}


	public void setLevel(int level) {
		this.level = level;
	}
	
	
}

package edu.sru.group3.WebBasedEvaluations.repository;


import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.*;

import edu.sru.group3.WebBasedEvaluations.domain.Reviewee;
import edu.sru.group3.WebBasedEvaluations.domain.Group;

@Repository
public interface RevieweeRepository extends CrudRepository<Reviewee,Long >{

	@Query(value = "SELECT name from web_eval.reviewee where id = ?", nativeQuery = true)
	String findNameById(long ID);
	
	@Query(value = "SELECT group_id from web_eval.reviewee where id = ? ", nativeQuery = true)
	String findGroupById(long ID);
	
	
	
	
	List<Reviewee>findBygroup_Id(long ID);

	List<Reviewee> findBygroup(Group group);

	List<Reviewee> findByUser_Id(long l);

	Reviewee findByname(String string);

	List<Reviewee> findByuser_Id(long id);
	
	
}

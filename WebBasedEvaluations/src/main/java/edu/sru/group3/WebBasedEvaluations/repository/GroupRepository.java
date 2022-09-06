package edu.sru.group3.WebBasedEvaluations.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.*;

import edu.sru.group3.WebBasedEvaluations.domain.Evaluator;
import edu.sru.group3.WebBasedEvaluations.domain.Group;
import edu.sru.group3.WebBasedEvaluations.domain.User;

@Repository
public interface GroupRepository extends CrudRepository<Group,Long > {

	List<Group> findByevaluatorUserId(long ID);
	Group findById(long ID);
	//EvaluatorRepository evaluatorRepository = ;
	//List<Group>findByEvaluator(Evaluator evaluator);
	//void removeAll(List<Group> grouplist);
	List<Group> findByevaluatorUserId(long id, Sort by);
	
	
}

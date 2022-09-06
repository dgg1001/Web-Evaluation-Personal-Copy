package edu.sru.group3.WebBasedEvaluations.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import edu.sru.group3.WebBasedEvaluations.domain.EvaluationLog;
import edu.sru.group3.WebBasedEvaluations.domain.Evaluator;
import edu.sru.group3.WebBasedEvaluations.domain.EvaluatorId;
import edu.sru.group3.WebBasedEvaluations.domain.Group;
import edu.sru.group3.WebBasedEvaluations.domain.Reviewee;



public interface EvaluationLogRepository extends CrudRepository<EvaluationLog,Long > {
	
	@Query("Select path from EvaluationLog where reviewee.id = ?1")
	Iterable<EvaluationLog> findByReviewee(long id);
	
	@Query("SELECT path FROM EvaluationLog where path is not null\n"
			+ "and reviewee.id = ?1")
	Iterable<EvaluationLog> findByIdNotNull(Long id);
	
	
	List<EvaluationLog> findByevaluator(Evaluator eval);

	EvaluationLog findByEvaluatorAndReviewee(Evaluator evaluator, Reviewee rev);

	
	EvaluationLog findByEvaluatorId(EvaluatorId evaluator);

	EvaluationLog findByEvaluatorIdAndRevieweeId(long evalid, long revid);

	

	//dvoid deleteAllByGroup(Group group);
	

	//EvaluationLog findByevaluatorAndreviewee(Evaluator evaluator, Reviewee rev);
}

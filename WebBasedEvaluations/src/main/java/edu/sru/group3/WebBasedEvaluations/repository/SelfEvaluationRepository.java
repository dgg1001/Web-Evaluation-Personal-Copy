package edu.sru.group3.WebBasedEvaluations.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.sru.group3.WebBasedEvaluations.domain.EvalTemplates;
import edu.sru.group3.WebBasedEvaluations.domain.Reviewee;
import edu.sru.group3.WebBasedEvaluations.domain.SelfEvaluation;
import edu.sru.group3.WebBasedEvaluations.domain.User;
@Repository
public interface SelfEvaluationRepository extends  CrudRepository<SelfEvaluation,Long>{
	


	SelfEvaluation findByRevieweeUser_Id(long id);

	SelfEvaluation findByReviewee(Reviewee reviewee);

}

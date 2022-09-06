package edu.sru.group3.WebBasedEvaluations.repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.sru.group3.WebBasedEvaluations.domain.EvalRole;
import edu.sru.group3.WebBasedEvaluations.domain.Evaluator;
import edu.sru.group3.WebBasedEvaluations.domain.Group;
import edu.sru.group3.WebBasedEvaluations.domain.User;
@Repository
public interface EvaluatorRepository extends CrudRepository<Evaluator,Long >{
List<Evaluator>findByUser(User user);
List<Evaluator>findByUserId(long user);
List<Evaluator>findByGroupId(long id);
Optional<Evaluator> findById(long id);
//query to get evaluator by name
List<Evaluator> findByUserIdAndGroupId(Long userid,long groupid);
List<Evaluator> findByLevelIdAndGroupId(int id, long groupid);



void deleteByGroupId(long id);
Boolean existsBylevelAndGroup(EvalRole evalRole, Group group);




}

package edu.sru.group3.WebBasedEvaluations.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import edu.sru.group3.WebBasedEvaluations.domain.EvalTemplates;


public interface EvaluationRepository extends CrudRepository<EvalTemplates,String > {
    List<EvalTemplates> findByNameIn(List<String> names);
    long count();



}

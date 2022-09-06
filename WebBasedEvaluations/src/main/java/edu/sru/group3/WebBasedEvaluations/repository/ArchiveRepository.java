package edu.sru.group3.WebBasedEvaluations.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.sru.group3.WebBasedEvaluations.domain.Archive;

@Repository
public interface ArchiveRepository extends CrudRepository<Archive,Long>{

}

package edu.sru.group3.WebBasedEvaluations.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.sru.group3.WebBasedEvaluations.domain.PasswordResetToken;
import edu.sru.group3.WebBasedEvaluations.domain.User;

@Repository
public interface PasswordTokenRepository extends CrudRepository<PasswordResetToken,Long>{
	public PasswordResetToken findByToken(String token);
	public User findByid(long l);



}

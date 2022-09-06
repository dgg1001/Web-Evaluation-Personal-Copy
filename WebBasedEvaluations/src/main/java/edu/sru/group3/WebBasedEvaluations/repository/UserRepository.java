package edu.sru.group3.WebBasedEvaluations.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import edu.sru.group3.WebBasedEvaluations.domain.User;
/**
 * @author Tanuj Rane, Dalton Stenzel
 *
 */
@Repository
public interface UserRepository extends CrudRepository<User,Long>{

	
	public User findByEmail(String email);
	public User findByName(String name);
	public User findByFirstName(String firstName);
	public User findByLastName(String lastName);


	public User findByid(long l);
	@Query(value= "select * from user u where u.first_name like %:keyword% or u.email like %:keyword% or u.last_name like %:keyword%", nativeQuery = true)
	List<User> findByKeyword(@Param("keyword") String keyword);
	//public User findById(long id);
	public Object findAll(Sort by);
	public List<User>findByRolesOrRoles(String name, String name2,Sort by);
}

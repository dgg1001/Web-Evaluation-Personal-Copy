package edu.sru.group3.WebBasedEvaluations;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.RETURNS_DEFAULTS;
import static org.mockito.Mockito.RETURNS_MOCKS;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.sru.group3.WebBasedEvaluations.domain.EvalRole;
import edu.sru.group3.WebBasedEvaluations.domain.Evaluator;
import edu.sru.group3.WebBasedEvaluations.domain.Group;
import edu.sru.group3.WebBasedEvaluations.domain.Reviewee;
import edu.sru.group3.WebBasedEvaluations.domain.User;
import edu.sru.group3.WebBasedEvaluations.repository.EvalRoleRepository;
import edu.sru.group3.WebBasedEvaluations.repository.EvaluatorRepository;
import edu.sru.group3.WebBasedEvaluations.repository.GroupRepository;
import edu.sru.group3.WebBasedEvaluations.repository.UserRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace=Replace.NONE)
public class EvaluationTests {
	private static User user = new User();
	private static User user2 = new User();
	private static User user3 = new User();
	  @Autowired
	  private UserRepository userRepository;
	  @Autowired
	  private EvalRoleRepository  evalRoleRepository ;
	  @Autowired
	  private GroupRepository  groupRepository ;
	  @Autowired
	  private EvaluatorRepository  evaluatorR ;
	  @BeforeEach
	  public void setup() {
			// User missing some details (Job Title & Date of Hire)
			user.setFirstName("Sam");
			user.setLastName("Thangiah");
			user.setCompanyName("Thangiah Inc");
			user.setDivisionBranch("Retroville");
			user.setRoles("USER");
			user.setSupervisor("Jimmy");
			user.setEmail("sam.thangiah@sru.edu");
			user.setEncryptedPassword("test");
			
			// User with all valid information
			user2.setFirstName("Dalton");
			user2.setLastName("Stenzel");
			user2.setEmail("daltonrstenzel@gmail.com");
			user2.setRoles("USER");
			user2.setEncryptedPassword("test");
			
			user2.setCompanyName("Thangiah Inc");
			user2.setDivisionBranch("Retroville");
			user2.setSupervisor("Jimmy");
			user2.setDateOfHire("10/15/2022");
			user2.setJobTitle("Assistant");
			
			// User with all information, but has errors (email has space)
			user3.setFirstName("Dalton");
			user3.setLastName("Stenzel");
			user3.setEmail("daltonrstenzel @gmail.com");
			user3.setRoles("USER");
			user3.setEncryptedPassword("test");
			
			user3.setCompanyName("Thangiah Inc");
			user3.setDivisionBranch("Retroville");
			user3.setSupervisor("Jimmy");
			user3.setDateOfHire("10/15/2022");
			user3.setJobTitle("Assistant");
			
			 userRepository.save(user);
			 userRepository.save(user2);
			 userRepository.save(user3);
			 evalRoleRepository.save(new EvalRole("level 1",1));
			 evalRoleRepository.save(new EvalRole("level 2",2));
			
	  }

	@Test
	public void creating_group()  {
		
		Group group =new Group();
		group.setId((long) 1);
		
		group.appendReviewee(new Reviewee(group, "sam", userRepository.findByFirstName("Sam")));
		ArrayList<Evaluator> eval= new ArrayList<Evaluator>();
		eval.add(new Evaluator(((List<User>) userRepository.findAll()).get(1), group, evalRoleRepository.findById(1).orElse(null)));
		eval.add(new Evaluator(((List<User>) userRepository.findAll()).get(2), group, evalRoleRepository.findById(1).orElse(null)));
		group.setEvaluator(eval);
		groupRepository.save(group);
		//evaluatorR.saveAll(eval);
	    System.out.print(groupRepository.findById(1).getId());
		assertEquals(groupRepository.findById(1).getId(),1);
		long eval1 = evaluatorR.count();
		assertEquals(eval1 ,2);//groupRepository.findById(1).getEvaluator().get(0).getUser().getFirstName());
		//ArrayList<Evaluator> eval2 = (ArrayList<Evaluator>) evaluatorR.findAll();
		String n = groupRepository.findById(1).getEvaluator().get(0).getLevel().getName();
		assertEquals( n,"level 1");
	 }
	@Test
	public void creating_findeval()  {
		
		Group group =new Group();
		group.setId((long) 1);
		
		group.appendReviewee(new Reviewee(group, "sam", userRepository.findByFirstName("Sam")));
		ArrayList<Evaluator> eval= new ArrayList<Evaluator>();
		eval.add(new Evaluator(((List<User>) userRepository.findAll()).get(1), group, evalRoleRepository.findById(1).orElse(null)));
		eval.add(new Evaluator(((List<User>) userRepository.findAll()).get(2), group, evalRoleRepository.findById(1).orElse(null)));
		group.setEvaluator(eval);
		groupRepository.save(group);
	    System.out.print(groupRepository.findById(1).getId());
		assertEquals(groupRepository.findById(1).getId(),1);
		long eval1 = evaluatorR.count();
		assertEquals(eval1 ,2);
		String n = groupRepository.findById(1).getEvaluator().get(0).getLevel().getName();
		assertEquals( n,"level 1");
	 }

}

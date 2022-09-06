package edu.sru.group3.WebBasedEvaluations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import edu.sru.group3.WebBasedEvaluations.domain.Evaluator;
import edu.sru.group3.WebBasedEvaluations.domain.Group;
import edu.sru.group3.WebBasedEvaluations.domain.Reviewee;
import edu.sru.group3.WebBasedEvaluations.domain.User;
import edu.sru.group3.WebBasedEvaluations.repository.EvaluatorRepository;
import edu.sru.group3.WebBasedEvaluations.repository.GroupRepository;
import edu.sru.group3.WebBasedEvaluations.repository.UserRepository;

@SpringBootApplication
//@EnableJpaRepositories(basePackageClasses = UserRepository.class)
public class WebBasedEvaluationsApplication {

	public static void main(String[] args) {
		System.setProperty("spring.devtools.restart.enabled", "false");
		
		ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run(WebBasedEvaluationsApplication.class, args);
		
//		UserRepository usersr=configurableApplicationContext.getBean(UserRepository.class);
//		User use1 = new User("admin","fname","lname","admin@gmail.com","$2y$12$nUFl.ypDL1ZAeqIR9Uq5SOZmBoOTlCmcgRm2tK2.B4dZIWEdrx4u6","ADMIN", 999991, "N/A", "N/A", "N/A", "N/A", "N/A");
//		User use2 = new User("eval_admin","fname","lname","evaladmin@gmail.com","$2y$12$nUFl.ypDL1ZAeqIR9Uq5SOZmBoOTlCmcgRm2tK2.B4dZIWEdrx4u6","EVAL_ADMIN", 999992, "N/A", "N/A", "N/A", "N/A", "N/A");
//		usersr.save(use1);
//		usersr.save(use2);

	}
	
}
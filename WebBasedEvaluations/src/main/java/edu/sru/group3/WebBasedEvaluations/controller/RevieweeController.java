package edu.sru.group3.WebBasedEvaluations.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.sru.group3.WebBasedEvaluations.domain.EvalRole;
import edu.sru.group3.WebBasedEvaluations.domain.Evaluator;
import edu.sru.group3.WebBasedEvaluations.domain.Group;
import edu.sru.group3.WebBasedEvaluations.domain.MyUserDetails;
import edu.sru.group3.WebBasedEvaluations.domain.Reviewee;
import edu.sru.group3.WebBasedEvaluations.domain.User;
import edu.sru.group3.WebBasedEvaluations.repository.EvalRoleRepository;
import edu.sru.group3.WebBasedEvaluations.repository.EvaluationLogRepository;
import edu.sru.group3.WebBasedEvaluations.repository.EvaluationRepository;
import edu.sru.group3.WebBasedEvaluations.repository.EvaluatorRepository;
import edu.sru.group3.WebBasedEvaluations.repository.GroupRepository;
import edu.sru.group3.WebBasedEvaluations.repository.RevieweeRepository;
import edu.sru.group3.WebBasedEvaluations.repository.UserRepository;
/**
 * Controls the  Reviewee behavior  of the application 
 *
 */
@Controller
public class RevieweeController {	
	
	private EvaluatorRepository evaluatorRepository;
	private UserRepository userRepository;
	private EvaluationLogRepository evaluationLogRepository;
	private RevieweeRepository revieweeRepository;
	private EvaluationRepository evalFormRepo;
	private EvalRoleRepository roleRepository;
	private GroupRepository groupRepository;
	//create an UserRepository instance - instantiation (new) is done by Spring
    public RevieweeController (GroupRepository groupRepository,EvaluatorRepository evaluatorRepository,UserRepository userRepository ,EvaluationLogRepository evaluationLogRepository,RevieweeRepository revieweeRepository,EvaluationRepository evalFormRepoprivate, EvalRoleRepository roleRepository,EvaluationRepository  evalFormRepo) {
    	this.revieweeRepository= revieweeRepository;
		this.evaluatorRepository  = evaluatorRepository;
		this.userRepository  = userRepository;
		this.evaluationLogRepository= evaluationLogRepository;
		this.evalFormRepo =evalFormRepo;
		this.roleRepository =roleRepository;
		this.groupRepository = groupRepository;
	}
	
  
  	/**
	 * gets all the evaluation made on the selected reviewee and display for the reviewee
	 * @param model
	 * model:
	 * groups:list of groups assciated with the reviewee
	 * reviewee is the reviewee associated with the user 
	 * @param authentication user details
	 * @return myeval page
	 */
    @GetMapping("/myeval")
    public Object getreviewee(Model model, Authentication authentication) {
    	MyUserDetails userD = (MyUserDetails) authentication.getPrincipal();
    	Long id = userD.getID() ;
    	List<Reviewee> reviewee = revieweeRepository.findByuser_Id(id);
    	List<Group> grouplist = (List<Group>) groupRepository.findByevaluatorUserId(userD.getID());
    	model.addAttribute("groups", grouplist);

    	List<EvalRole>roles = (List<EvalRole>) roleRepository.findAll();
    	

    	model.addAttribute("myRole", userD.getRoles());
    	model.addAttribute("role", roles);
    	model.addAttribute("id", userD.getID());
    	model.addAttribute("groups", grouplist);
    	model.addAttribute("reviewee", reviewee);
    	return "myeval";

    }

	
	/**
	 * gets all the evaluation made on the selected reviewee and display them for the admin
	 * @param model
	 * model:
	 * reviewee: list of reviewee associated with the id 
	 * @param id the reviewee id 
	 * @return admin_eval page
	 */
	@GetMapping("/admineval/{id}")
	public Object getrevieweegroup(Model model,@PathVariable("id") long id) {
		
		 List<Reviewee> reviewee = revieweeRepository.findByuser_Id(id);
		
			
			List<EvalRole>roles = (List<EvalRole>) roleRepository.findAll();
			
			
		model.addAttribute("role", roles);
		model.addAttribute("id",id);
		
		model.addAttribute("reviewee", reviewee);
		return "admin_eval";
		
	}


}

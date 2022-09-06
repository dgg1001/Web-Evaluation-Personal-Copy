package edu.sru.group3.WebBasedEvaluations.controller;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.SerializationUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import edu.sru.group3.WebBasedEvaluations.domain.Archive;
import edu.sru.group3.WebBasedEvaluations.domain.EvalRole;
import edu.sru.group3.WebBasedEvaluations.domain.Group;
import edu.sru.group3.WebBasedEvaluations.domain.MyUserDetails;
import edu.sru.group3.WebBasedEvaluations.domain.User;
import edu.sru.group3.WebBasedEvaluations.evalform.Evaluation;
import edu.sru.group3.WebBasedEvaluations.repository.ArchiveRepository;
import edu.sru.group3.WebBasedEvaluations.repository.EvalRoleRepository;
import edu.sru.group3.WebBasedEvaluations.repository.EvaluationLogRepository;
import edu.sru.group3.WebBasedEvaluations.repository.EvaluationRepository;
import edu.sru.group3.WebBasedEvaluations.repository.EvaluatorRepository;
import edu.sru.group3.WebBasedEvaluations.repository.GroupRepository;
import edu.sru.group3.WebBasedEvaluations.repository.RevieweeRepository;
import edu.sru.group3.WebBasedEvaluations.repository.UserRepository;
/**
 * Controls the Archive behavior  of the application 
 *
 */
@Controller
public class ArchiveController {
	
	private GroupRepository groupRepository;

	private UserRepository userRepository;

	private EvaluatorRepository evaluatorRepository;
	private EvaluationLogRepository evaluationLogRepository;
	private RevieweeRepository revieweeRepository;
	private EvalRoleRepository roleRepository;
	private EvaluationRepository evaluationRepository;
	private EvaluationRepository evalFormRepo;
	private ArchiveRepository archiveRepository ;
	public ArchiveController(ArchiveRepository archiveRepository,GroupRepository groupRepository, UserRepository userRepository,
			EvaluatorRepository evaluatorRepository, RevieweeRepository revieweeRepository,
			EvaluationLogRepository evaluationLogRepository, EvalRoleRepository roleRepository,
			EvaluationRepository evaluationRepository, EvaluationRepository evalFormRepo) {
		this.evaluatorRepository = evaluatorRepository;
		this.groupRepository = groupRepository;
		this.userRepository = userRepository;
		this.revieweeRepository = revieweeRepository;
		this.evaluationLogRepository = evaluationLogRepository;
		this.roleRepository = roleRepository;
		this.evaluationRepository = evaluationRepository;
		this.evalFormRepo = evalFormRepo;
		this.archiveRepository=archiveRepository ;
	}


	/**
	 * Display all the achieved evaluation to the the admin 
	 * @param model
	 * Models:
	 * archive: list of archive evaluation
	 * @param authentication user details
	 * @return admin_archive page 
	 */
	@GetMapping("/Archivegroups")
	public String evalGroups(Model model, Authentication authentication) {
		
		boolean groupButton = false;
		MyUserDetails userD = (MyUserDetails) authentication.getPrincipal();
		User user = userRepository.findByid(userD.getID());
		List<Archive> Archivelist = (List<Archive>) archiveRepository.findAll();
		System.out.print(Archivelist.size());
		
		if (evaluationRepository == null || evaluationRepository.count() == 0) {
			groupButton = false;
		} else {
			groupButton = true;
		}

		model.addAttribute("archive", Archivelist);
		
		List<EvalRole> roles = (List<EvalRole>) roleRepository.findAll();
		model.addAttribute("groupButton", groupButton);

		model.addAttribute("role", roles);
		model.addAttribute("id", userD.getID());
		model.addAttribute("evalu", user);
		return "admin_archive";
	}
	/**
	 * @param id of the archive
	 * @param model
	 * @param authentication user details
	 * @return preview_eval and displays selected evaluation 
	 */
	@GetMapping("/ViewArchive/{id}")
	public String ViewViewArchive(@PathVariable("id") long id, Model model, Authentication authentication) {

		MyUserDetails userD = (MyUserDetails) authentication.getPrincipal();
		User user = userRepository.findByid(userD.getID());
		Archive	archive= archiveRepository.findById(id).orElse(null);
		 Evaluation evall;
		 evall = (Evaluation) SerializationUtils.deserialize(archive.getPath());

		
		
		List<EvalRole> roles = (List<EvalRole>) roleRepository.findAll();
	 model.addAttribute("eval", evall);
		model.addAttribute("role", roles);
		model.addAttribute("id", userD.getID());
		model.addAttribute("evalu", user);
		model.addAttribute("address", "/Archivegroups");
		return "preview_eval";
	}
	
	
}

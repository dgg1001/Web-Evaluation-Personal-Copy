package edu.sru.group3.WebBasedEvaluations.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import edu.sru.group3.WebBasedEvaluations.domain.EvalTemplates;
import edu.sru.group3.WebBasedEvaluations.domain.Evaluator;
import org.springframework.web.bind.annotation.GetMapping;
import edu.sru.group3.WebBasedEvaluations.domain.MyUserDetails;
import edu.sru.group3.WebBasedEvaluations.domain.User;
import edu.sru.group3.WebBasedEvaluations.repository.EvaluationRepository;
import edu.sru.group3.WebBasedEvaluations.repository.EvaluatorRepository;
import edu.sru.group3.WebBasedEvaluations.repository.UserRepository;
import edu.sru.group3.WebBasedEvaluations.service.UserService;


/**Class for controlling the starting sequence of users logging and taking them to the home page
 * @author Dalton Stenzel
 *
 */
@Controller
public class HomePage {
	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	private UserRepository userRepository;
	private EvaluatorRepository evaluatorRepository;

	private EvaluationRepository evaluationRepository;
	private Logger log = LoggerFactory.getLogger(HomePage.class);



	public HomePage(UserRepository userRepository, EvaluatorRepository evaluatorRepository,
			EvaluationRepository evaluationRepository) {
		this.userRepository = userRepository;
		this.evaluationRepository = evaluationRepository;
	}

	/*
	// Maps to the evaluator_home.html when called.
	@GetMapping("/evaluator_home")
	public String EvalHome(Authentication authentication, Model model) {
		return "evaluator_home";
	}
	*/

	// Maps to the base.html when called or when localhost:8080 is called.
	@GetMapping("/")
	public String base() {
		return "redirect:/login";
	}

	// Maps to the login.html when called.
	@GetMapping("/login")
	public String login() {
		return "login";
	}

	
	/**Method for logging users who have logged in.
	 * @param auth is an Authentication object used to identify who has logged in.
	 * @return home mapping.
	 */
	@GetMapping("/logging")
	public String loginLoging(Authentication auth) {
		MyUserDetails user = (MyUserDetails) auth.getPrincipal();
		User user2 = userRepository.findByid(user.getID());
		log.info("User logged in- ID:" + user2.getId() + " | First Name: " + user2.getFirstName() + " | Last Name: " +user2.getLastName() );

		return "redirect:/home";
	}	


	/**Method called upon being logged in and logged. Add's user attributes to show particular things based of user and evaluation form information.
	 * @param auth is an Authentication object used for identifying who is logged in.
	 * @param user3 is a User object needed in order to prevent an "IllegalStateException" error from occurring.
	 * @param model is a Model object used to add attributes to a webpage.
	 * @return home html webpage or redirect to firstReset mapping
	 */
	@GetMapping("/home")
	public String home(Authentication auth, User user3, /*BindingResult result,*/ Model model) {



		MyUserDetails user = (MyUserDetails) auth.getPrincipal();
		User user2 = userRepository.findByid(user.getID());
		


		boolean groupButton = false;
		/*
		if (user.getRoles().equals("ADMIN")) {
			return "redirect:/admin_users/?keyword=&perPage=0&sort=id&currPage=1&sortOr=1";
		}
		*/
		if (evaluationRepository == null || evaluationRepository.count() == 0) {
			groupButton = false;
		} else {
			groupButton = true;
		}
		/*
		if (user.getRoles().equals("EVAL_ADMIN")) {
			// System.out.println("EVAL_ADMIN");
			//System.out.println(groupButton);
			//System.out.println("test");


			if (evaluationRepository == null || evaluationRepository.count() == 0) {
				groupButton = false;
			} else {
				groupButton = true;
			}

			//System.out.println(groupButton);

		}*/

		if (user2.getReset() == true) {
			return "redirect:/firstReset";
		} 
		else {
			// model.addAttribute("role", user.getRoles());
			model.addAttribute("groupButton", groupButton);

			model.addAttribute("role", user.getRoles());
			model.addAttribute("user", user.getUsername());
			model.addAttribute("id", user.getID());
			return "home";
		}
	}

	
	/**
	 * Processes the request to download the log.txt file.
	 * 
	 * @return ResponseEntity containing the download resource
	 * @throws Exception
	 */
	@GetMapping("/download_log_txt")
	public ResponseEntity<Resource> downloadEvalExcel() throws Exception {
		
		// Name of download file
		final String FILE_NAME = "log.txt";
		
		log.info("File '" + FILE_NAME + "' requested for download.");
		
		//Download the file
		FileSystemResource resource = new FileSystemResource(FILE_NAME);
		MediaType mediaType = MediaTypeFactory
				.getMediaType(resource)
				.orElse(MediaType.APPLICATION_OCTET_STREAM);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(mediaType);
		ContentDisposition disposition = ContentDisposition
				.attachment()
				.filename(resource.getFilename())
				.build();
		headers.setContentDisposition(disposition);
		
		return new ResponseEntity<>(resource, headers, HttpStatus.OK);
	}
}

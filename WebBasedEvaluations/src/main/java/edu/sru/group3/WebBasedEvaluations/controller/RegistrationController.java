package edu.sru.group3.WebBasedEvaluations.controller;
/*
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.sru.group3.WebBasedEvaluations.domain.User;
//import edu.sru.group3.WebBasedEvaluations.event.RegistrationCompleteEvent;
//import edu.sru.group3.WebBasedEvaluations.model.UserModel;
import edu.sru.group3.WebBasedEvaluations.repository.UserRepository;
//import edu.sru.group3.WebBasedEvaluations.service.UserService2;




Class for registering users if ever need be. Needs model and service methods/classes uncommented


@Controller
public class RegistrationController {
	
	//private UserService2 userService;
	private UserRepository userRepository;
	
    public RegistrationController(UserRepository userRepository ) {
		this.userRepository = userRepository;
		
	}
	@Autowired
	private ApplicationEventPublisher publisher;
	
    @RequestMapping({"/register"})
    public String register(User user) {
        return "register";
    }
	
	@PostMapping("/registerUser")
	public String registerUser(@Validated User user, BindingResult result, Model model) {
        model.addAttribute("users", user);
    	user.setEncryptedPassword(user.getPassword());
        userRepository.save(user);
        System.out.println("Tetst");
		//publisher.publishEvent(new RegistrationCompleteEvent(user,applicationUrl(request) ));
		return "redirect:/";
		
	}

	/*
	private String applicationUrl(HttpServletRequest request) {
		
		return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
		// TODO Auto-generated method stub
	}

	*/
/*
}
*/

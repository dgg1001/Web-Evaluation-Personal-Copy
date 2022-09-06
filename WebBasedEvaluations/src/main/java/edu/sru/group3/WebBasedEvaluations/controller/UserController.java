package edu.sru.group3.WebBasedEvaluations.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import edu.sru.group3.WebBasedEvaluations.domain.Evaluator;
import edu.sru.group3.WebBasedEvaluations.domain.MyUserDetails;
import edu.sru.group3.WebBasedEvaluations.domain.User;
import edu.sru.group3.WebBasedEvaluations.repository.EvaluationRepository;
import edu.sru.group3.WebBasedEvaluations.repository.EvaluatorRepository;
import edu.sru.group3.WebBasedEvaluations.repository.UserRepository;
import edu.sru.group3.WebBasedEvaluations.service.AdminMethodsService;
import edu.sru.group3.WebBasedEvaluations.service.UserService;

/**
 * Class for handling user related changes other than adding users. Houses the
 * admin_user webpage.
 * 
 * @author Dalton Stenzel
 *
 */
@Controller
public class UserController {
	// set up a UserRepositoty variable
	private UserRepository userRepository;
	private EvaluatorRepository evaluatorRepository;
	private EvaluationRepository evaluationRepository;

	private AddUserController addUserController;

	private Logger log = LoggerFactory.getLogger(UserController.class);

	private static final String ADMIN = "ADMIN";
	private static final String EVALUATOR_EVAL = "EVALUATOR_EVAL";
	private static final String EVAL_ADMIN = "EVAL_ADMIN";
	private static final String EVALUATOR = "EVALUATOR";
	private static final String USER = "USER";

	@Autowired
	private AdminMethodsService adminMethodsService;

	@Autowired
	private UserService service;

	// create an UserRepository instance - instantiation (new) is done by Spring
	public UserController(UserRepository userRepository, EvaluatorRepository evaluatorRepository,
			EvaluationRepository evaluationRepository, AddUserController addUserController) {
		this.userRepository = userRepository;
		this.evaluatorRepository = evaluatorRepository;
		this.evaluationRepository = evaluationRepository;
		this.addUserController = addUserController;

	}

	/**
	 * Method called upon loading the admin user page in order to
	 * add/edit/delete/view users. Calls several methods from the
	 * AdminMethodsService for performing sorting and the amount of pages needed to
	 * be viewable.
	 * 
	 * @param user     is a User object required for the webpage, or else an
	 *                 "IllegalStateException error occurs.
	 * @param model    is a Model object used to add attributes to a webpage.
	 * @param keyword  is a String used to hold a particular search term entered by
	 *                 the user.
	 * @param perPage  is a Integer used to store the value of how many users should
	 *                 be displayed on a page at once.
	 * @param sort     is a String used to hold the type of sort to be used on the
	 *                 users to be displayed.
	 * @param currPage is an Integer used to store the current page the user is on
	 *                 for viewing users.
	 * @param sortOr   is an Integer value used to determine the order in which the
	 *                 list of users will be displayed as: ascending(1) or
	 *                 descending(0).
	 * @return the admin_user html page.
	 */
	@RequestMapping(path = { "/admin_users/", "/search" })
	public String home(User user, Model model, String keyword, @RequestParam("perPage") Integer perPage,
			@RequestParam("sort") String sort, @RequestParam("currPage") Integer currPage,
			@RequestParam("sortOr") Integer sortOr) {
		List<User> listTotal = service.getAllUsers();
		List<User> list;
		// log.error("Loaded page");
		// log.info("Loaded page info");

		// No keyword
		if (keyword == null || keyword.equals("")/* && count !=null */) {

			list = adminMethodsService.sortCheck(sort, listTotal, sortOr, model);
			list = adminMethodsService.pageCalc(list, currPage, perPage, sort, keyword, model);



		}
		// Has keyword
		else {
			List<User> listKeyword = service.getByKeyword(keyword);

			// If showing all users
			if (perPage <= 0) {
				list = adminMethodsService.sortCheck(sort, listKeyword, sortOr, model);
				list = adminMethodsService.pageCalc(list, currPage, perPage, sort, keyword, model);

				// If not showing all users
			} else {
				// sort list with parameters
				list = adminMethodsService.sortCheck(sort, service.getByKeyword(keyword), sortOr, model);
				// display current page + other page buttons
				list = adminMethodsService.pageCalc(list, currPage, perPage, sort, keyword, model);

			}

		}
		model.addAttribute("keyword", keyword);

		model.addAttribute("list", list);

		model.addAttribute("perPage", perPage);
		model.addAttribute("sortOr", sortOr);

		model.addAttribute("sort", sort);

		return "admin_users";
	}

	/**
	 * Method called when a user decides to edit their own account with the "My
	 * Account" button on most pages.
	 * 
	 * @param authentication is an Authentication object used in this instance to
	 *                       get the current user logged in.
	 * @param model          is a Model object used to add attributes to a webpage.
	 * @return user_settings html webpage.
	 */
	@RequestMapping({ "/user_settings/" })
	public String editSettings(Authentication authentication, Model model) {

		boolean groupButton = false;
		MyUserDetails userD = (MyUserDetails) authentication.getPrincipal();

		Long id = userD.getID();

		User user = userRepository.findById(id).orElse(null);

		if (evaluationRepository == null || evaluationRepository.count() == 0) {
			groupButton = false;
		} else {
			groupButton = true;
		}

		model.addAttribute("role", user.getRoles());
		model.addAttribute("id", user.getId());
		model.addAttribute("groupButton", groupButton);

		model.addAttribute("user", user);

		return "user_settings";
	}

	/*
	 * Maps to the admin_user_update.html when called, where an user is selected
	 * from the admin_users.html page and that id is then sent and used in order to
	 * make changes to the user with the id selected.
	 */

	/**
	 * Method for editing other users from an edit button on the admin users page.
	 * 
	 * @param id       is a long that contains the id value of the user selected to
	 *                 be edited.
	 * @param user     is a User object that gets used to find the specific user
	 *                 being edited.
	 * @param model    is a Model object used to add attributes to a webpage.
	 * @param keyword  is a String used to hold a particular search term entered by
	 *                 the user.
	 * @param perPage  is a Integer used to store the value of how many users should
	 *                 be displayed on a page at once.
	 * @param sort     is a String used to hold the type of sort to be used on the
	 *                 users to be displayed.
	 * @param currPage is an Integer used to store the current page the user is on
	 *                 for viewing users.
	 * @param sortOr   is an Integer value used to determine the order in which the
	 *                 list of users will be displayed as: ascending or descending.
	 * @return admin_user_update html webpage.
	 */
	@GetMapping("/edit/{id}/")
	public String showUpdateForm(@PathVariable("id") long id, @RequestParam("perPage") Integer perPage,
			@RequestParam("sort") String sort, @RequestParam("keyword") String keyword,
			@RequestParam("currPage") Integer currPage, @RequestParam("sortOr") Integer sortOr, User user,
			Model model) {
		model.addAttribute("perPage", perPage);
		String ansr = null;
		String mess = null;

		// Likely has redunant code in this method

		// MyUserDetails userD = (MyUserDetails) authentication.getPrincipal();
		Long userId = id;
		User userCheck = userRepository.findById(userId).orElse(null);

		user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
		model.addAttribute("user", user);
		model.addAttribute("id", id);
		adminMethodsService.adminUserPageItems(ansr, keyword, mess, perPage, model, sort, currPage, sortOr);
		return "admin_user_update";
	}

	/*
	 * Maps to the admin_users.html when called, which continues what the
	 * showUpdateForm(or /edit/{id}) function started where the changeable
	 * information is either changed or not and sent here to update the user
	 * repository in order to save the changes made. This function prevents a user
	 * having their email manually changed to same email as another user without
	 * preventing the user to retain the same email as they previously did.
	 */

	/**
	 * @param id       is a long used to hold the value of the user that is in the
	 *                 process of having their information updated.
	 * @param perPage  is a Integer used to store the value of how many users should
	 *                 be displayed on a page at once.
	 * @param sort     is a String used to hold the type of sort to be used on the
	 *                 users to be displayed.
	 * @param keyword  is a String used to hold a particular search term entered by
	 *                 the user.
	 * @param currPage is an Integer used to store the current page the user is on
	 *                 for viewing users.
	 * @param sortOr   is an Integer value used to determine the order in which the
	 *                 list of users will be displayed as: ascending or descending.
	 * @param user     is a User object.
	 * @param model    is a Model object used to add attributes to a webpage.
	 * @return admin_user_update html webpage.
	 */
	@PostMapping("/update/{id}/")
	public String updateUser(@PathVariable("id") long id, @RequestParam("perPage") Integer perPage,
			@RequestParam("sort") String sort, @RequestParam("keyword") String keyword,
			@RequestParam("currPage") Integer currPage, @RequestParam("sortOr") Integer sortOr, @Validated User user,
			/* BindingResult result, */ Model model) {

		String ansr = null;
		String mess = null;
		model.addAttribute("perPage", perPage);

		User user2 = userRepository.findByid(id);

		adminMethodsService.adminUserPageItems(ansr, keyword, mess, perPage, model, sort, currPage, sortOr);

		// Performs comparison between old and new user values for changes
		User user3 = adminMethodsService.comparingMethod(id, user, user2, model);

		// Checks if email already used by another user, if not then the user selected
		// will be updated.
		if ((userRepository.findByEmail(user.getEmail()) == null)
				|| (userRepository.findByEmail(user.getEmail())) == userRepository.findByid(id)) {

			user3.setFirstName(adminMethodsService.capitalize(user3.getFirstName()));
			user3.setLastName(adminMethodsService.capitalize(user3.getLastName()));

			userRepository.save(user3);
			return "admin_user_update";
		} else {
			return "admin_user_update";

		}
	}

	/**
	 * Method for deleting users when a "Delete User" button is pressed on the admin
	 * user page. Administrative users are prevented from deleting other users with
	 * the same role.
	 *
	 * 
	 * @param id          is a long used to store the id of the user being deleted.
	 * @param keyword     is a String used to hold the set of characters provided to
	 *                    be searched for.
	 * @param perPage     is a Integer used to store the value of how many users
	 *                    should be displayed on a page at once.
	 * @param model       is a Model object used to add attributes to a webpage.
	 * @param sort        is a String used to hold the type of sort to be used on
	 *                    the users to be displayed.
	 * @param currPage    is an Integer used to store the current page the user is
	 *                    on for viewing users.
	 * @param sortOr      is an Integer value used to determine the order in which
	 *                    the list of users will be displayed as: ascending or
	 *                    descending.
	 * @param redir       is a RedirectAttributes object used to send attributes to
	 *                    a webpage, similar to the model included.
	 * @param deletedUser is a User object that is needed for some reason, error
	 *                    "java.lang.IllegalStateException: Neither BindingResult
	 *                    nor plain target object for bean name 'user' available as
	 *                    request attribute" returns without a user.
	 * @return admin_users html webpage.
	 */
	@GetMapping("/delete/{id}/")
	public Object deleteUser(@PathVariable("id") long id, @RequestParam("keyword") String keyword,
			@RequestParam("perPage") Integer perPage, Model model, @RequestParam("sort") String sort,
			@RequestParam("currPage") Integer currPage, @RequestParam("sortOr") Integer sortOr, User deletedUser,
			RedirectAttributes redir) {
		String ansr = null;
		String mess = null;

		User user = userRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
		List<Evaluator> eval = evaluatorRepository.findByUser(user);
		//If user is in a group
		if (eval.size() > 0) {
			ansr = "addFail";
			model.addAttribute("ansr", ansr);

			redir.addFlashAttribute("mess", "User is currently in a group, you must remove them first! ");
			RedirectView redirectView = new RedirectView("/admin_users/", true);
			return redirectView;
		} else {
			//if user to be deleted is an admin
			if (user.getRoles().equals(ADMIN)) {
				// System.out.println("Detected Admin");

				ansr = "addFail";
				mess = "Can't delete administrative users";
				model.addAttribute("ansr", ansr);

				model.addAttribute("mess", mess);
				// model.addAttribute("user", user);

			} else {

				log.info("User Deleted- Id:" + user.getId() + " | First Name:" + user.getFirstName() + " | Last Name:"
						+ user.getLastName() + " | Role:" + user.getRoles());

				userRepository.delete(user);
				ansr = "addPass";
				mess = "User '" + user.getFirstName() + " " + user.getLastName() + "' has been deleted";
				model.addAttribute("ansr", ansr);
				model.addAttribute("mess", mess);
			}

			adminMethodsService.adminUserPageItems(ansr, keyword, mess, perPage, model, sort, currPage, sortOr);

			return "admin_users";
			// return "redirect:/admin_users/?keyword=" + keyword + "&perPage=" +
			// perPage.toString() + "&sort=" + sort;
		}
	}

	/**
	 * Method for attempting to apply changes that a user has applied for on their
	 * "My Account" page. The method calls the comparingMethod from an instance of
	 * AdminMethodsService to do some checking and add messages to the webpage.
	 * 
	 * @param id    is a long used to hold the id of the user having the changes
	 *              applied to.
	 * @param user  is a User object used to hold the changes attempting to be
	 *              applied in the database.
	 * @param model is a Model object used to add attributes to a webpage.
	 * @return user_settings html webpage.
	 */
	@PostMapping("/change/{id}")
	public String changeUser(@PathVariable("id") long id, @Validated User user,
			/* BindingResult result, */ Model model) {
		User user2 = userRepository.findByid(id);

		User user3 = adminMethodsService.comparingMethod(id, user, user2, model);

		model.addAttribute("role", user2.getRoles());
		model.addAttribute("id", user2.getId());

		model.addAttribute("user", user2);

		user3.setFirstName(adminMethodsService.capitalize(user3.getFirstName()));
		user3.setLastName(adminMethodsService.capitalize(user3.getLastName()));

		userRepository.save(user3);

		return "user_settings";
	}

}

package edu.sru.group3.WebBasedEvaluations.service;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import edu.sru.group3.WebBasedEvaluations.controller.HomePage;
import edu.sru.group3.WebBasedEvaluations.domain.User;
import edu.sru.group3.WebBasedEvaluations.repository.UserRepository;

/**
 * This class provides all of the tools for the UserController and AddUserController classes that don't require the use of a html file/link tag

 * @author Dalton Stenzel
 * 
 *
 */
@Service
public class AdminMethodsService {

	private UserRepository userRepository;
	private Logger log = LoggerFactory.getLogger(AdminMethodsService.class);
	private static final int PASSWORDSIZE = 5;
	private static final String ADMIN = "ADMIN";
	private static final String EVALUATOR_EVAL = "EVALUATOR_EVAL";
	private static final String EVAL_ADMIN = "EVAL_ADMIN";
	private static final String EVALUATOR = "EVALUATOR";
	private static final String USER = "USER";

	/**
	 * @param userRepository is a user repository, contains list of users
	 */
	public AdminMethodsService(UserRepository userRepository) {
		this.userRepository = userRepository;

	}

	@Autowired
	private UserService service;

	/**
	 * Method for for checking the sort type and then sends the value to an instance
	 * of the UserService in order to obtain a sorted list when given a sort, list,
	 * sort order, and model. A sorted list can be returned if a valid sort type is
	 * presented.
	 * 
	 * The check is performed here and in the actual sorting method, so there is
	 * likely some redundancy here.
	 * 
	 * @param sort      is the type of sort: firstName, lastName, email, and role.
	 * @param listTotal is the list sent to be sorted.
	 * @param sortOr    is the order the sort should be: ascending(1) or descending(0).
	 * @param model     is a Model object with no user here, likely left over from
	 *                  something else.
	 * @return a list that is likely sorted by a specific type and order.
	 */
	public List<User> sortCheck(String sort, List<User> listTotal, Integer sortOr, Model model) {
		log.info("sortCheck items- " + "sort:" + sort + " listTotal:" + listTotal + " sortOr:" + sortOr);

		List<User> list;
		if (sort.equals("firstName")) {
			list = service.sorting(listTotal, sort, sortOr);
		} else if (sort.equals("lastName")) {
			list = service.sorting(listTotal, sort, sortOr);
		} else if (sort.equals("email")) {
			list = service.sorting(listTotal, sort, sortOr);
		} else if (sort.equals("role")) {
			list = service.sorting(listTotal, sort, sortOr);
		} else if (sort.equals("id")) {
			list = service.sorting(listTotal, sort, sortOr);
		} else {
			list = service.sorting(listTotal, sort, sortOr);
		}
		return list;

	}

	/**
	 * Method for taking a String in order to capitalize the first character of the
	 * String.
	 * 
	 * @param word is the String value to have its first character capitalized
	 * @return a String that is the "word" variable with the first character
	 *         capitalized.
	 */
	public String capitalize(String word) {
		String str1 = new String();
		str1 = word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
		return str1;
	}

	/**
	 * Method for checking if a given String has at least a single space.
	 * 
	 * @param str is the String value given to be checked if it has a space in it or
	 *            not.
	 * @return either true or false to confirm if the String has a space in it or
	 *         not.
	 */
	public boolean hasSpace(String str) {
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == ' ' /* || str.charAt(i)==' '&&str.charAt(i+1)!=' ' */) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Method that checks the user information provided when adding users in order
	 * to check if they're empty, null, least than the length of 4 chars, or contain
	 * spaces when they shouldn't. It logs user information that caused the rejection.
	 * 
	 * @param user is the user object that is attempting to be added to the data.
	 * @return true or false depending on if the user should be added.
	 */
	public boolean checkAndUpdate(User user) {
		if (user.getPassword() == null || user.getPassword().equals("") || hasSpace(user.getPassword())
				|| user.getPassword().length() < PASSWORDSIZE) {

			log.info("Manual User Add- " + "Password: failed");


			return false;

		} else if (user.getFirstName() == null || user.getFirstName().equals("") || hasSpace(user.getFirstName())) {

			log.info("Manual User Add- " + "First Name failed: " + user.getFirstName());

			return false;

		} else if (user.getLastName() == null || user.getLastName().equals("") || hasSpace(user.getLastName())) {

			log.info("Manual User Add- " + "Last Name failed: " + user.getLastName());

			return false;

		} else if (user.getEmail() == null || user.getEmail().equals("") || hasSpace(user.getEmail())) {

			log.info("Manual User Add- " + "Email failed: " + user.getEmail());

			return false;

		} else if (user.getCompanyName() == null
				|| user.getCompanyName().equals("") /* || hasSpace(user.getEmail()) */) {

			log.info("Manual User Add- " + "Company Name failed: " + user.getCompanyName());

			return false;

		} else if (user.getDivisionBranch() == null
				|| user.getDivisionBranch().equals("") /* || hasSpace(user.getEmail()) */) {

			log.info("Manual User Add- " + "Division Branch failed: " + user.getDivisionBranch());

			return false;

		} else if (user.getJobTitle() == null || user.getJobTitle().equals("") /* || hasSpace(user.getEmail()) */) {

			log.info("Manual User Add- " + "Job Title failed: " + user.getJobTitle());

			return false;

		} else if (user.getSupervisor() == null || user.getSupervisor().equals("") /* || hasSpace(user.getEmail()) */) {

			log.info("Manual User Add- " + "Supervisor failed: " + user.getSupervisor());

			return false;

		} else if (user.getDateOfHire() == null || user.getDateOfHire().equals("") /* || hasSpace(user.getEmail()) */) {


			log.info("Manual User Add- " + "Date of Hire failed: " + user.getDateOfHire());

			return false;

		}  else {

			return true;

		}
		// return "admin_users";

	}

	/**
	 * Method for adding everything required in order to provide the admin user page
	 * with the content needed to be useful.
	 * 
	 * @param ansr     is a String value used for the attributes on the admin user
	 *                 page in order to provide an error/success message.
	 * @param keyword  is a String value that is provided by a user in order to
	 *                 display a list of users that have a relation to the word.
	 * @param mess     is a String that used for an attribute on the admin user page
	 *                 along side the ansr variable. "mess" contains the message.
	 * @param perPage  is an Integer value used to keep track of how many users will
	 *                 display per page.
	 * @param model    is a Model object that is needed to add attributes to a web
	 *                 page.
	 * @param sort     is a String that is used to determine what the sort type will
	 *                 be.
	 * @param currPage is an Integer value used to keep track of what page the user
	 *                 is on.
	 * @param sortOr   is an Integer value used to determine whether the sort order will be ascending or descending, 1 and 0 respectively.
	 */
	public void adminUserPageItems(String ansr, String keyword, String mess, Integer perPage, Model model, String sort,
			Integer currPage, Integer sortOr) {
		List<User> listTotal = service.getAllUsers();

		int startVal = 0;
		model.addAttribute("users", userRepository.findAll());
		model.addAttribute("mess", mess);
		model.addAttribute("perPage", perPage);
		model.addAttribute("sort", sort);
		model.addAttribute("currPage", currPage);
		model.addAttribute("sortOr", sortOr);

		model.addAttribute("ansr", ansr);

		if (keyword == "" || keyword != null) {
			if (perPage <= 0) {
				List<User> list = listTotal;

				model.addAttribute("list", list);
			} else {
				List<User> list = sortCheck(sort, listTotal, sortOr, model);
				pageCalc(list, currPage, perPage, sort, keyword, model);
				// getTables(listTotal, perPage, model);
				model.addAttribute("list", list);
			}

		}

		else {
			List<User> list = sortCheck(sort, service.getByKeyword(keyword), sortOr, model);
			model.addAttribute("list", list);
		}

	}

	/**
	 * Method for gathering the users that will display on the current page by
	 * making a new list, using a provided one as reference.
	 * 
	 * @param userList is a list of users provided that will be used as reference
	 *                 for making a smaller list to be returned.
	 * @param page     is an Integer that holds the current page the user is on.
	 * @param perPage  is an Integer that holds the amount of users being displayed
	 *                 per page.
	 * @param sort     is a String that holds what type of sort will be used on the
	 *                 list.
	 * @param keyword  is a String that holds the specific set of characters
	 *                 searched for.
	 * @param model    is a Model object used to add attributes to the website.
	 * @return a list which contains the users to be displayed on the webpage.
	 */
	public List<User> pageCalc(List<User> userList, Integer page, Integer perPage, String sort, String keyword,
			Model model) {
		Integer defaultPage = 1;
		Integer defaultPerPage = 0;
		String defaultSort = "id";
		String defaultKeyword = "";

		try {

			int pageNum = page;

			pageNum -= 1;

			int page2 = pageNum;
			int setVal = perPage;
			double maxPages;
			int remain;
			int endVal;
			int startVal = perPage * pageNum;

			int size = userList.size();
			// If all users are showing, or if the user list has 0 values
			if (setVal == 0 && page == 1 || size == 0) {
				model.addAttribute("currPage", page);

				return userList;

			}

			else {
				//
				remain = size % setVal;
				maxPages = Math.ceil((double) size / setVal);

				List pagesList = new ArrayList();

				// adds to array of pagesList the value of each page available from the amount
				// of users in the list
				for (int i = 1; i <= maxPages; i++) {

					pagesList.add(i);
				}
				// if current page is the last page
				if (page2 + 1 >= maxPages) {
					// if the amount of users evenly splits amongst the users per page value set,
					// then the stopping value to be shown is the last user calculated using the
					// users per page value
					if (remain == 0) {
						endVal = startVal + perPage;

					}
					// then the stopping value to be shown is the last user calculated using what
					// the first user index to be displayed plus the remaining amount of users,
					// which the total value is less the the users per page value
					else {
						endVal = startVal + remain;

					}

				}
				// current page isn't the last page, the stopping index value will be the
				// initial value plus the users per page value
				else {

					endVal = startVal + perPage;

				}
				// if the starting user index value is greater than the size of the list (occurs
				// going from a high page number with a low users per page value to switching
				// the users per page value. The page number stays the same even though that
				// page no longer exists when swapping as the maxPage count will be lower than
				// before)
				if (startVal > size) {
					startVal = 0;
					endVal = perPage;
					page = 1;
				}

				List list2 = service.getBySetCount(userList, startVal, endVal);

				model.addAttribute("pages2", pagesList);
				model.addAttribute("currPage", page);

				return list2;

			}
		} catch (Exception e) {
			log.error("PageCalc() error:", e);
			model.addAttribute("currPage", defaultPage);
			model.addAttribute("perPage", defaultPerPage);
			model.addAttribute("keyword", defaultKeyword);
			model.addAttribute("sort", defaultSort);

			return userList;
		}

	}

	/**
	 * Method for comparing changes between old and new changes for a user from the
	 * admin user edit page and add webpage attributes depending on if the user had
	 * changes applied or not. It logs the pre, attempted, and post changes of a user.
	 * 
	 * @param id    is a long used to hold the id of the user that's attempting to
	 *              be updated.
	 * @param user  is a User object, specifically the user post-changes.
	 * @param user2 is a User object, specifically the user pre-changes.
	 * @param model is a Model object that is used to add attributes to the webpage.
	 * @return user2 with either user or user2 changes or a mix of both depending on
	 *         what types of changes were attempted.
	 */
	public User comparingMethod(long id, User user, User user2, Model model) {
		boolean check = false;

		log.info("User Pre Changes- Id:" + user2.getId() + " | First Name:" + user2.getFirstName() + " | Last Name:"
				+ user2.getLastName() + " | Suffix Name:" + user2.getSuffixName() + " | Email:" + user2.getEmail()
				+ " | Role:" + user2.getRoles() + " | Company Name:" + user2.getCompanyName() + " | Division Branch:"
				+ user2.getDivisionBranch() + " | Job Title:" + user2.getJobTitle() + " | Supervisor:"
				+ user2.getSupervisor() + " | Date of Hire:" + user2.getDateOfHire());

		log.info("User Attempted Changes- Id:" + user.getId() + " | First Name:" + user.getFirstName() + " | Last Name:"
				+ user.getLastName() + " | Suffix Name:" + user.getSuffixName() + " | Email:" + user.getEmail()
				+ " | Role:" + user.getRoles() + " | Company Name:" + user.getCompanyName() + " | Division Branch:"
				+ user.getDivisionBranch() + " | Job Title:" + user.getJobTitle() + " | Supervisor:"
				+ user.getSupervisor() + " | Date of Hire:" + user.getDateOfHire());
		if (true) {
			
			if (user.getPassword() == "" || user.getPassword() == null || user.getPassword().length() < PASSWORDSIZE) {

				user.setPassword(user2.getPassword());
			} else if (hasSpace(user.getPassword())) {
				user.setPassword(user2.getPassword());

			}

			else if (user.getPassword().equals(user2.getPassword())) {
				user2.setPassword(user.getPassword());

			} else if (hasSpace(user.getPassword())) {

			}

			else {

				user2.setEncryptedPassword(user.getPassword());
				check = true;
			}

		}

		if (true) {

			if ((user.getSuffixName() == "" || user.getSuffixName() == null)) {
				user.setSuffixName(null);

				if (user.getSuffixName() == user2.getSuffixName()) {
					user2.setSuffixName(user.getSuffixName());

				} else {
					user2.setSuffixName(null);
					check = true;
				}

			} else if (hasSpace(user.getSuffixName())) {
				user.setSuffixName(user2.getSuffixName());

			} else if (user.getSuffixName().equals(user2.getSuffixName())) {
				user2.setSuffixName(user.getSuffixName());

			} else {
				user2.setSuffixName(user.getSuffixName());
				check = true;
			}

		}

		if (true) {
			if (user.getLastName() == "" || user.getLastName() == null) {

				user.setLastName(user2.getLastName());
			}

			else if (user.getLastName().equals(user2.getLastName())) {
				user2.setLastName(user.getLastName());
				user2.setName(user2.getFirstName() + " " + user2.getLastName());

			} else if (hasSpace(user.getLastName())) {
				user.setLastName(user2.getLastName());
			} else {
				user2.setLastName(user.getLastName());
				user2.setName(user2.getFirstName() + " " + user2.getLastName());

				check = true;
			}

		}

		if (true) {
			if (user.getFirstName() == "" || user.getFirstName() == null) {

				user.setFirstName(user2.getFirstName());
			} else if (hasSpace(user.getFirstName())) {
				user.setFirstName(user2.getFirstName());

			}

			else if (user.getFirstName().equals(user2.getFirstName())) {
				user2.setFirstName(user.getFirstName());
				user2.setName(user2.getFirstName() + " " + user2.getLastName());

			} else {
				user2.setFirstName(user.getFirstName());
				user2.setName(user2.getFirstName() + " " + user2.getLastName());

				check = true;
			}

		}

		if (true) {
			if (user.getEmail() == "" || user.getEmail() == null) {

				user.setEmail(user2.getEmail());
			} else if (hasSpace(user.getEmail())) {
				user.setEmail(user2.getEmail());

			} else if ((userRepository.findByEmail(user.getEmail()) != null)
					&& (userRepository.findByEmail(user.getEmail())) != userRepository.findByid(id)) {
				user.setEmail(user2.getEmail());

			} else if (user.getEmail().equals(user2.getEmail())) {
				user2.setEmail(user.getEmail());

			} else {
				user2.setEmail(user.getEmail());
				check = true;

			}

		}
		if (true) {
			/*
			System.out.println(user.getId());
			System.out.println(id);
			System.out.println(user.getRoles());
			*/

			if (/*user.getId() == id ||*/ user.getRoles() == ADMIN) {
				user.setRoles(user2.getRoles());

			} else {
				if (user.getRoles() == null) {
					user.setRoles(user2.getRoles());
				}

				else if (user.getRoles().equals(user2.getRoles())) {
					user2.setRoles(user.getRoles());

				} else {
					user2.setRoles(user.getRoles());

					check = true;
				}
			}

		}

		if (true) {
			if (user.getCompanyName() == "" || user.getCompanyName() == null) {

				user.setCompanyName(user2.getCompanyName());

			} /*
				 * else if (hasSpace(user.getFirstName())) {
				 * user.setFirstName(user2.getFirstName());
				 * 
				 * }
				 */

			else if (user.getCompanyName().equals(user2.getCompanyName())) {
				user2.setCompanyName(user.getCompanyName());

			} else {
				user2.setCompanyName(user.getCompanyName());

				check = true;
			}

		}

		if (true) {
			if (user.getSupervisor() == "" || user.getSupervisor() == null) {

				user.setSupervisor(user2.getSupervisor());

			} /*
				 * else if (hasSpace(user.getFirstName())) {
				 * user.setFirstName(user2.getFirstName());
				 * 
				 * }
				 */

			else if (user.getSupervisor().equals(user2.getSupervisor())) {
				user2.setSupervisor(user.getSupervisor());

			} else {
				user2.setSupervisor(user.getSupervisor());

				check = true;
			}

		}
		if (true) {
			if (user.getDivisionBranch() == "" || user.getDivisionBranch() == null) {

				user.setDivisionBranch(user2.getDivisionBranch());

			} /*
				 * else if (hasSpace(user.getFirstName())) {
				 * user.setFirstName(user2.getFirstName());
				 * 
				 * }
				 */

			else if (user.getDivisionBranch().equals(user2.getDivisionBranch())) {
				user2.setDivisionBranch(user.getDivisionBranch());

			} else {
				user2.setDivisionBranch(user.getDivisionBranch());

				check = true;
			}

		}

		if (true) {
			if (user.getJobTitle() == "" || user.getJobTitle() == null) {

				user.setJobTitle(user2.getJobTitle());

			} /*
				 * else if (hasSpace(user.getFirstName())) {
				 * user.setFirstName(user2.getFirstName());
				 * 
				 * }
				 */

			else if (user.getJobTitle().equals(user2.getJobTitle())) {
				user2.setJobTitle(user.getJobTitle());

			} else {
				user2.setJobTitle(user.getJobTitle());

				check = true;
			}

		}
		if (true) {
			if (user.getDateOfHire() == "" || user.getDateOfHire() == null) {

				user.setDateOfHire(user2.getDateOfHire());

			} /*
				 * else if (hasSpace(user.getFirstName())) {
				 * user.setFirstName(user2.getFirstName());
				 * 
				 * }
				 */

			else if (user.getDateOfHire().equals(user2.getDateOfHire())) {
				user2.setDateOfHire(user.getDateOfHire());

			} else {
				user2.setDateOfHire(user.getDateOfHire());

				check = true;
			}

		}
		if (user2.getSuffixName() == null) {

			user2.setName(user2.getFirstName() + " " + user2.getLastName());

		} else {
			user2.setName(user2.getFirstName() + " " + user2.getLastName() + " " + user2.getSuffixName());

		}

		if (check) {
			String ansr = "pass";
			String mess = "Changes have been made!";

			model.addAttribute("ansr", ansr);
			model.addAttribute("mess", mess);

		}
		if (!check) {

			String ansr = "fail";
			String mess = "Changes were not been made!";

			model.addAttribute("ansr", ansr);
			model.addAttribute("mess", mess);

		}

		if (user.getRoles() != null) {
			user2.setRoles(user.getRoles());
		}
		log.info("User Post Changes- Id:" + user2.getId() + " | First Name:" + user2.getFirstName() + " | Last Name:"
				+ user2.getLastName() + " | Suffix Name:" + user2.getSuffixName() + " | Email:" + user2.getEmail()
				+ " | Role:" + user2.getRoles() + " | Company Name:" + user2.getCompanyName() + " | Division Branch:"
				+ user2.getDivisionBranch() + " | Job Title:" + user2.getJobTitle() + " | Supervisor:"
				+ user2.getSupervisor() + " | Date of Hire:" + user2.getDateOfHire());

		return user2;
	}

}

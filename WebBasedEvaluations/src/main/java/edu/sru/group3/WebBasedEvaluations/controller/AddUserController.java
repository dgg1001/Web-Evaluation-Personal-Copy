package edu.sru.group3.WebBasedEvaluations.controller;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import edu.sru.group3.WebBasedEvaluations.domain.MyUserDetails;
import edu.sru.group3.WebBasedEvaluations.domain.User;
import edu.sru.group3.WebBasedEvaluations.excel.ExcelRead_group;
import edu.sru.group3.WebBasedEvaluations.repository.UserRepository;
import edu.sru.group3.WebBasedEvaluations.service.AdminMethodsService;
import edu.sru.group3.WebBasedEvaluations.service.UserService;

/**
 * Class for housing the methods for controlling how to add users via
 * manually/uploading
 * 
 * @author Dalton Stenzel
 *
 */
@Controller
public class AddUserController {
	// set up a UserRepositoty variable
	private UserRepository userRepository;

	@Autowired
	private AdminMethodsService adminMethodsService;

	private Logger log = LoggerFactory.getLogger(AdminMethodsService.class);

	public AddUserController(UserRepository userRepository) {
		this.userRepository = userRepository;

	}

	/**
	 * Method for manually adding users from the admin user page. It calls a few
	 * methods from the AdminMethodsService class for checking for any changes,
	 * capital letters, spaces, problems, etc,. This method is called when the "Add
	 * User" button is pressed.
	 * 
	 * @param user     is a User object used that holds the information submitted
	 *                 from the "Add User" button
	 * @param result   is a BindResult object used in conjunction with "@Validated"
	 *                 tag in order to bind the submission to an object.
	 * @param model    is a Model object used for adding attributes to a webpage,
	 *                 mostly used for adding messages and lists to the page.
	 * @param keyword  is a String used to hold a particular set of characters being
	 *                 sought after in a list of users.
	 * @param perPage  is an Integer value used to store the amount of users to be
	 *                 displayed per page.
	 * @param sort     is a String value user to contain the type of sorting to be
	 *                 used on a list of users such as: first name, last name,
	 *                 email, role.
	 * @param currPage is an Integer value used to store the current page number the
	 *                 user was on.
	 * @param sortOr   is an Integer value used to determine the order in which the
	 *                 sort will take place: ascending(1) or descending(0).
	 * @return admin_users html webpage.
	 */
	@RequestMapping({ "/adduser/" })
	public String addUser(@Validated User user, BindingResult result, Model model, Authentication auth,
			/* @RequestParam("keyword") */ String keyword, @RequestParam("perPage") Integer perPage,
			@RequestParam("sort") String sort, @RequestParam("currPage") Integer currPage,
			@RequestParam("sortOr") Integer sortOr) {
		String ansr = null;
		String mess = null;
		User adminUser = user;
		boolean check = false;
		if (auth == null) {
		} else {
			MyUserDetails userD = (MyUserDetails) auth.getPrincipal();
			adminUser = userRepository.findByid(userD.getID());
		}

		if (userRepository.findByEmail(user.getEmail()) == null) {

			check = adminMethodsService.checkAndUpdate(user);
			if (check == true) {

				user.setFirstName(adminMethodsService.capitalize(user.getFirstName()));
				user.setLastName(adminMethodsService.capitalize(user.getLastName()));
				if (user.getSuffixName() == " " || user.getSuffixName() == null) {

					user.setName(user.getFirstName() + " " + user.getLastName());

				} else {

					user.setName(user.getFirstName() + " " + user.getLastName() + " " + user.getSuffixName());

				}

				user.setEncryptedPassword(user.getPassword());
				user.setReset(true);
				userRepository.save(user);
				if (auth == null) {
				} else {
					log.info("ADMIN User - ID:" + adminUser.getId() + " First Name: " + adminUser.getFirstName()
							+ " Last Name: " + " added a " + user.getRoles() + " user");
				}
				mess = "User successfully added!";
				ansr = "addPass";
				// adminMethodsService.adminUserPageItems(ansr, keyword, mess, perPage, model,
				// sort);
			} else {
				ansr = "addFail";
				mess = "User not added! A field was blank, contained spaces or the password wasn't at least 5 characters!";
				// adminUserPageItems(ansr, keyword, mess, perPage, model, sort);
			}
			adminMethodsService.adminUserPageItems(ansr, keyword, mess, perPage, model, sort, currPage, sortOr);
			return "admin_users";
		}

		if (result.hasErrors()) {

			model.addAttribute("users", userRepository.findAll());

			return "admin_users";
		}

		else {
			ansr = "addFail";
			mess = "User email already taken!";

			adminMethodsService.adminUserPageItems(ansr, keyword, mess, perPage, model, sort, currPage, sortOr);
			return "admin_users";

		}

	}

	/**
	 * Method called when the "Upload Users" button is pressed. It will attempt
	 * check the file uploaded, if there is one in the first place, and will log
	 * information about users unable to be added. It checks if there was a file
	 * selected and also adds messages about when users were or were not
	 * successfully added.
	 * 
	 * @param reapExcelDataFile is a MultipartFile object
	 * @param user              is a User object that is required for the page,
	 *                          other an IllegalStateException error will occur.
	 * 
	 * @param model             is a Model object used for adding attributes to a
	 *                          webpage, mostly used for adding messages and lists
	 *                          to the page.
	 * @param keyword           is a String used to hold a particular set of
	 *                          characters being sought after in a list of users.
	 * @param perPage           is an Interger value used to store the amount of
	 *                          users to be displayed per page.
	 * @param sort              is a String value user to contain the type of
	 *                          sorting to be used on a list of users such as: first
	 *                          name, last name, email, role.
	 * @param currPage          is an Integer value used to store the current page
	 *                          number the user was on.
	 * @param sortOr            is an Integer value used to determine the order in
	 *                          which the sort will take place: ascending(1) or
	 *                          descending(0).
	 * @return admin_users html webpage.
	 */
	@RequestMapping(value = "/uploaduser2", method = RequestMethod.POST)
	public String uploaduser2(@RequestParam("file") MultipartFile reapExcelDataFile,
			@RequestParam("perPage") Integer perPage, @Validated User users, /* BindingResult result, */ Model model,
			String keyword, @RequestParam("sort") String sort, @RequestParam("currPage") Integer currPage,
			@RequestParam("sortOr") Integer sortOr, Authentication auth) {

		String ansr;
		String mess;
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();
		User adminUser = userRepository.findByid(userD.getID());
		boolean check = false;
		XSSFSheet sheet = null;
		try {
			sheet = ExcelRead_group.loadFile(reapExcelDataFile).getSheetAt(0);

		} catch (Exception e) {
			if (e instanceof Exception) {

				mess = "No file selected!";
				ansr = "addFail";
				adminMethodsService.adminUserPageItems(ansr, keyword, mess, perPage, model, sort, currPage, sortOr);
				return "admin_users";

			}
		}

		if (ExcelRead_group.checkStringType(sheet.getRow(0).getCell(1)).equals(null)
				|| !ExcelRead_group.checkStringType(sheet.getRow(0).getCell(1)).equals("User Upload")) {

		}

		else if (ExcelRead_group.checkStringType(sheet.getRow(0).getCell(1)).equals("User Upload")) {

			for (int i = 2; sheet.getRow(i) != null; i++) {
				try {
					User user2 = new User();
					User tempUser = userRepository
							.findByEmail(ExcelRead_group.checkStringType(sheet.getRow(i).getCell(3)));
					if (tempUser == null) {
						if (ExcelRead_group.checkStringType(sheet.getRow(i).getCell(2)) == " "
								|| ExcelRead_group.checkStringType(sheet.getRow(i).getCell(2)) == null) {

							String str1 = adminMethodsService
									.capitalize(ExcelRead_group.checkStringType(sheet.getRow(i).getCell(0)));
							String str2 = adminMethodsService
									.capitalize(ExcelRead_group.checkStringType(sheet.getRow(i).getCell(1)));

							user2.setName(str1 + " " + str2);
							user2.setFirstName(str1);
							user2.setLastName(str2);

						} else {
							String str1 = adminMethodsService
									.capitalize(ExcelRead_group.checkStringType(sheet.getRow(i).getCell(0)));
							String str2 = adminMethodsService
									.capitalize(ExcelRead_group.checkStringType(sheet.getRow(i).getCell(1)));
							String str3 = adminMethodsService
									.capitalize(ExcelRead_group.checkStringType(sheet.getRow(i).getCell(2)));

							user2.setName(str1 + " " + str2 + " " + str3);
							user2.setFirstName(str1);
							user2.setLastName(str2);
							user2.setSuffixName(str3);

						}
						user2.setEmail(ExcelRead_group.checkStringType(sheet.getRow(i).getCell(3)));
						user2.setPassword(ExcelRead_group.checkStringType(sheet.getRow(i).getCell(4)));
						user2.setRoles(ExcelRead_group.checkStringType(sheet.getRow(i).getCell(5)));
						user2.setReset(true);

						// preload
						// user2.setEmployeeId(ExcelRead_group.checkIntType(sheet.getRow(i).getCell(7)));
						user2.setDateOfHire(ExcelRead_group.checkStringType(sheet.getRow(i).getCell(7)));
						user2.setJobTitle(ExcelRead_group.checkStringType(sheet.getRow(i).getCell(8)));
						user2.setSupervisor(ExcelRead_group.checkStringType(sheet.getRow(i).getCell(9)));
						user2.setCompanyName(ExcelRead_group.checkStringType(sheet.getRow(i).getCell(10)));
						user2.setDivisionBranch(ExcelRead_group.checkStringType(sheet.getRow(i).getCell(11)));

						userRepository.save(user2);
						check = true;

					}

				}

				catch (Exception e) {

					log.error("Could not add user in row: " + (sheet.getRow(i).getRowNum() + 1) + " from "
							+ reapExcelDataFile.getOriginalFilename()
							+ ". Either null, email already taken, or incorrect information!");
					model.addAttribute("log", "error");
				}
			}
		}

		if (check) {
			log.info("ADMIN User - ID:" + adminUser.getId() + " First Name: " + adminUser.getFirstName()
					+ " Last Name: " + " uploaded a file: " + reapExcelDataFile.getOriginalFilename());

			mess = "File uploaded! User(s) successfully added!";
			ansr = "addPass";
			adminMethodsService.adminUserPageItems(ansr, keyword, mess, perPage, model, sort, currPage, sortOr);

			return "admin_users";

		} else {

			mess = "File failed to be uploaded!";
			ansr = "addFail";
			adminMethodsService.adminUserPageItems(ansr, keyword, mess, perPage, model, sort, currPage, sortOr);
			return "admin_users";

		}

	}
	/*
	 * @GetMapping("/uploadingUser") public String uploadgroup(Model model) {
	 * 
	 * return "upload_user"; }
	 */

}

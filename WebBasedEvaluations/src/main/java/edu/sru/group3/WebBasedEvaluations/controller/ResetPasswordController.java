package edu.sru.group3.WebBasedEvaluations.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.sru.group3.WebBasedEvaluations.domain.MyUserDetails;
import edu.sru.group3.WebBasedEvaluations.domain.PasswordResetToken;
import edu.sru.group3.WebBasedEvaluations.domain.ResetPassword;
import edu.sru.group3.WebBasedEvaluations.domain.User;
//import edu.sru.group3.WebBasedEvaluations.domain.VerificationToken;
import edu.sru.group3.WebBasedEvaluations.repository.PasswordTokenRepository;
import edu.sru.group3.WebBasedEvaluations.repository.UserRepository;
import edu.sru.group3.WebBasedEvaluations.service.EmailSenderService;

import edu.sru.group3.WebBasedEvaluations.service.AdminMethodsService;

/**
 * Class for controlling attempts at password resets such as recovery and first
 * time logins
 * 
 * @author Dalton Stenzel
 *
 */
@Controller
public class ResetPasswordController {

	@Autowired
	private EmailSenderService service;
	@Autowired
	private PasswordTokenRepository passwordTokenRepository;
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AdminMethodsService adminMethodsService;

	private static final int PASSWORDSIZE = 5;

	/*
	 * @Autowired private PasswordResetToken passwordResetToken;
	 */
	/**
	 * Method called when the "Reset Password" button is pressed on the login page.
	 * 
	 * @param model is a Model object for adding a resetPassword object to the page.
	 * @return
	 */
	@GetMapping({ "/resetting" })
	public String resetPassword(Model model) {
		model.addAttribute("reset", new ResetPassword());
		// System.out.println(reset.getEmail());

		return "reset";
	}

	/**
	 * Method called when a user logins in for the first time, based off a user's
	 * attribute (resetP).
	 * 
	 * @param auth  is an Authentication object used for identifying the person
	 *              logged in.
	 * @param reset is a ResetPassword object that is added to the webpage as an
	 *              attribute.
	 * @param model is a Model object for adding attributes to a webpage.
	 * @return recover_password_reset html webpage.
	 */
	@GetMapping({ "/firstReset" })
	public String firstResetPassword(Authentication auth, @ModelAttribute ResetPassword reset,
			/* User user3, BindingResult result, */ Model model) {
		MyUserDetails user = (MyUserDetails) auth.getPrincipal();
		User user2 = userRepository.findByid(user.getID());
		model.addAttribute("reset", reset);

		model.addAttribute("user", user2);
		model.addAttribute("id", user2.getId());

		return "recover_password_reset";
	}

	/**
	 * Method called upon clicking the "Submit" button on the recover password page
	 * with an email. The method puts together some Strings for a unique email with
	 * a particular token linked to the user with that email address in order only a
	 * person with that link to be able to reset the password. If the email does not
	 * exist in the database, then a message will display saying so.
	 * 
	 * @param reset is a ResetPassword object that is grabbed from an attribute
	 *              submitted by the page for evaluation.
	 * @param model is a Model object for adding attributes to a webpage.
	 * @return reset html webpage
	 */
	@PostMapping({ "/reset" })
	public String resetSent(@ModelAttribute ResetPassword reset, Model model) {
		String token = UUID.randomUUID().toString();
		String path = "http://localhost:8080";
		String url = path + "/changePassword?token=" + token;
		String ansr;
		String mess;
		System.out.println(reset.getEmail());
		System.out.println(reset.getPassword());

		// LocalDate localDate = LocalDate.now();
		model.addAttribute("reset", reset);
		try {
			ansr = "pass";
			mess = "Email sent";

			User user = userRepository.findByEmail(reset.getEmail());

			PasswordResetToken passwordResetToken = new PasswordResetToken(token, user);

			passwordResetToken.setUserIdReset(user.getId());

			passwordTokenRepository.save(passwordResetToken);

			service.sendSimpleEmail(reset.getEmail(),
					"The reset token associated with this url will expire in 10 minutes. \n" + url, "Password Reset");

			model.addAttribute("ansr", ansr);
			model.addAttribute("mess", mess);

			emailSentConfirm(model);

		} catch (Exception e) {
			mess = "Email failed to be sent";

			ansr = "fail";

		}
		model.addAttribute("mess", mess);

		model.addAttribute("ansr", ansr);

		return "reset";
	}

	/**
	 * Method that is called on when the unique link that was generated from /reset
	 * is pressed. It checks if the token has expired or not. If the token has
	 * expired, then the user will be redirected to the login screen, otherwise
	 * another webpage will be displayed for the user to reset their password.
	 * 
	 * @param model is a Model object used for adding attributes to a webpage.
	 *              Mostly used for messages in this scenario
	 * @param reset is a ResetPassword object that gets added to the webpage
	 * @param token is a String that is unique to the email/url that is tied to a
	 *              user's email in order to reset their password
	 * @return recover_password_reset html webpage or redirect to login
	 */
	@GetMapping({ "/changePassword" })
	public String showChangePasswordPage(/* Locale locale, */ Model model, ResetPassword reset,
			@RequestParam("token") String token) {

		LocalTime localTime = LocalTime.now();
		LocalDate localDate = LocalDate.now();

		PasswordResetToken passwordResetToken = passwordTokenRepository.findByToken(token);
		User user = userRepository.findByid(passwordResetToken.getUserIdReset());

		if ((passwordResetToken.getExpiredDate().equals(localDate))
				&& localTime.isBefore(passwordResetToken.getExpireTime())) {
			model.addAttribute("user", user);
			model.addAttribute("reset", reset);

			model.addAttribute("id", user.getId());
			// model.addAttribute("token", token);

			return "recover_password_reset";
		} else {
			System.out.println("Out of time");
			return "redirect:/";
		}

	}

	/**
	 * Method that gets called upon when pressing "Submit" after entering a password
	 * twice when in the act of changing the password post email recovery link. The
	 * password will only update if the two password boxes match, have no spaces,
	 * and the size of the password is 5 characters or greater.
	 * 
	 * @param id is a long that holds the id value of the user having their password changed.
	 * @param user is a User object used to hold the user with the previously mentioned id for changes to occur.
	 * @param reset is a ResetPassword object  used to hold the data of the two password boxes on the page.
	 * @param token is a String value that holds a unique set of characters that is associated to a user's email.
	 * @param model is a Model object used for adding attributes to a webpage, most for messages in this instance.
	 * @return recover_password_reset html webpage.
	 */
	@PostMapping({ "/recoverChanges/{id}" })
	public String updateRecovery(@PathVariable("id") long id, @ModelAttribute("user") User user,
			@ModelAttribute("reset") ResetPassword reset, @ModelAttribute("token") String token, Model model) {
		String pass = reset.getPassword();
		String passCheck = reset.getPasswordCheck();
		String ansr;
		String mess;
		if (adminMethodsService.hasSpace(pass) | pass.length() < PASSWORDSIZE) {
			ansr = "fail";
			mess = "Password either has a space in it or isn't long enough (at least 5 characters long)!";

			model.addAttribute("ansr", ansr);
			model.addAttribute("mess", mess);
		} else if (pass.equals(passCheck) && (!pass.equals(null) && !pass.equals(""))) {
			// System.out.println(pass);

			ansr = "pass";
			mess = "Password has been updated";

			user = userRepository.findByid(id);
			user.setEncryptedPassword(pass);

			if (user.getReset() == true) {
				user.setReset(false);

			}

			userRepository.save(user);

			model.addAttribute("ansr", ansr);
			model.addAttribute("mess", mess);
			return "redirect:/login";

		} else {
			ansr = "fail";
			mess = "Password failed to update, the passwords were null or different";

			model.addAttribute("ansr", ansr);
			model.addAttribute("mess", mess);
		}

		return "recover_password_reset";

	}

	/**Method for sending messages to the webpage to confirm that an email was sent
	 * @param model is Model object used for adding attributes to a webpage. It's used for adding messages in this case.
	 */
	@GetMapping("/email_sent")
	public void emailSentConfirm(Model model) {
		String ansr = "Email has been sent";
		String succ = "test";

		model.addAttribute("test", succ);
		model.addAttribute("mess", ansr);

		model.addAttribute("ansr", ansr);
	}
}

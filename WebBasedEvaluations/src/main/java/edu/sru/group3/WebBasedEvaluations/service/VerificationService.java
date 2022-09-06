package edu.sru.group3.WebBasedEvaluations.service;

import edu.sru.group3.WebBasedEvaluations.domain.User;
//import edu.sru.group3.WebBasedEvaluations.model.UserModel;

/**
 * Service interface class for saving a verification token.
 * Date 4/21/2022
 * @author Dalton Stenzel
 *
 */
public interface VerificationService {

	//User registerUser(UserModel userModel);

	void saveVerificationTokenForUser(String token, User user);

}

package edu.sru.group3.WebBasedEvaluations.service;

import edu.sru.group3.WebBasedEvaluations.domain.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import edu.sru.group3.WebBasedEvaluations.domain.User;
import edu.sru.group3.WebBasedEvaluations.repository.UserRepository;

/**
 * Service class for user lists and the sorting for them as well.
 * Date: 4/20/2022
 * @author Dalton Stenzel
 *
 */
@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;

	/**Method that collects a list of all users in the database from an instance of a userRepository.
	 * @return list of users .
	 */
	public List<User> getAllUsers() {
		List<User> list = (List<User>) userRepository.findAll();
		return list;
	}

	/**Method that collects a list of all users that contain particular keyword provided by the user.
	 * @param keyword is a set characters provided by a user.
	 * @return list of users that contains the keyword provided by a user.
	 */
	public List<User> getByKeyword(String keyword) {
		List<User> list = userRepository.findByKeyword(keyword);
		return list;
	}

	// For settings how many to display on a page.
	/**Method that chops up given list into a smaller list from a certain range provided for admin user table pages.
	 * @param list is the list referenced when making a smaller list.
	 * @param start is the value used as the beginning of the range.
	 * @param end is the value used as the ending of the range.
	 * @return another list made from the given list and start/end range values.
	 */
	public List<User> getBySetCount(List<User> list, int start, int end) {
		List<User> list2 = new ArrayList<>();

		for (int i = start; i < end; i++) {
			list2.add(list.get(i));

		}
		return list2;
	}

	/**Method that sorts a given list with the given type of sort and sort order.
	 * @param list is the list of users that is provided to be arranged.
	 * @param type is the type of sort type: firstName, lastName, id, email, and role.
	 * @param sortOr is the order in which the sorted list is provided: ascending(1) and descending(0).
	 * @return list that is either sorted in a particular fashion and order or uses the getAllUsers method if a provided sort parameter is unknown.
	 */
	public List<User> sorting(List<User> list, String type, Integer sortOr) {

		if (type.equals("firstName")) {

			Collections.sort(list, new Comparator<User>() {
				@Override
				public int compare(final User object1, final User object2) {

					return object1.getFirstName().compareTo(object2.getFirstName());
				}
			});

		}

		else if (type.equals("lastName")) {
			Collections.sort(list, new Comparator<User>() {
				@Override
				public int compare(final User object1, final User object2) {

					return object1.getLastName().compareTo(object2.getLastName());
				}
			});
		}

		else if (type.equals("id")) {
			Collections.sort(list, new Comparator<User>() {
				@Override
				public int compare(final User object1, final User object2) {

					return object1.getId().compareTo(object2.getId());
				}
			});
		} else if (type.equals("email")) {
			Collections.sort(list, new Comparator<User>() {
				@Override
				public int compare(final User object1, final User object2) {

					return object1.getEmail().compareTo(object2.getEmail());
				}
			});
		}

		else if (type.equals("role")) {
			Collections.sort(list, new Comparator<User>() {
				@Override
				public int compare(final User object1, final User object2) {

					return object1.getRoles().compareTo(object2.getRoles());
				}
			});
		}

		else {
			list = getAllUsers();

		}

		if (sortOr == 1) {

		} else {

			Collections.reverse(list);
		}
		return list;

	}

}

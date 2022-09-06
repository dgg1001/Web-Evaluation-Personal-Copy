package edu.sru.group3.WebBasedEvaluations.domain;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.lang.NonNull;
import org.springframework.security.crypto.bcrypt.BCrypt;

/**Class for methods of a user object, almost exclusively made out of getters and setters.
 * @author Tanuj Rane, Dalton Stenzel, Logan Racer
 *
 */
@Entity
@Table(name = "User")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NonNull
	private String name;

	@NonNull
	private String firstName;

	@NonNull
	private String lastName;

	private String suffixName;

	@NonNull
	private String email;
	@NonNull
	private String password;
	@NonNull
	private String roles;

	@NonNull
	private boolean resetP;

	@OneToMany(mappedBy = "user")
	private List<Evaluator> evaluator = new ArrayList<>();

	// Preload
	//private long employeeId;
	private String dateOfHire;
	private String jobTitle;
	private String supervisor;
	private String companyName;
	private String divisionBranch;

	public User() {

	}

	public User(String name, String firstName, String lastName, String email, String password, String role,
			int employeeId, String dateOfHire, String jobTitle, String supervisor, String companyName,
			String divisionBranch) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.name = name;
		this.email = email;
		this.password = password;
		this.roles = role;
		this.resetP = true;

		// Preload
		//this.employeeId = employeeId;
		this.dateOfHire = dateOfHire;
		this.jobTitle = jobTitle;
		this.supervisor = supervisor;
		this.companyName = companyName;
		this.divisionBranch = divisionBranch;

	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {

		// String encodPass = BCrypt.hashpw(password, BCrypt.gensalt());
		this.password = password/* .replaceAll("\\s", "") */;
	}

	public void setEncryptedPassword(String password) {
		String encodPass = BCrypt.hashpw(password/* .replaceAll("\\s", "") */, BCrypt.gensalt());
		this.setPassword(encodPass);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email/*.replaceAll("\\s", "")*/;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {

		this.roles = roles;
	}

	public List<Evaluator> getEvaluator() {
		return evaluator;
	}

	public void setEvaluator(List<Evaluator> evaluator) {
		this.evaluator = evaluator;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName/* .replaceAll("\\s", "") */;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName/* .replaceAll("\\s", "") */;
	}

	public String getSuffixName() {
		return suffixName;
	}

	public void setSuffixName(String suffixName) {
		if (suffixName == null) {
			this.suffixName = suffixName;
		} else if (suffixName.replaceAll("\\s", "") == "") {
			this.suffixName = null;
		}

		else {

			this.suffixName = suffixName.replaceAll("\\s", "");
		}
	}

	public boolean getReset() {
		return resetP;
	}

	public void setReset(boolean reset) {
		this.resetP = reset;
	}

	public boolean isResetP() {
		return resetP;
	}

	public void setResetP(boolean resetP) {
		this.resetP = resetP;

	}

	// Preload =======================================
	/*
	public long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(long employeeId) {
		this.employeeId = employeeId;
	}
	*/
	public String getDateOfHire() {
		return dateOfHire;
	}

	public void setDateOfHire(String dateOfHire) {
		if(dateOfHire.matches("[0-9]+")) {
			dateOfHire = LocalDate.of( 1899 , Month.DECEMBER , 30 ).plusDays((long) Double.parseDouble(dateOfHire)).toString();
		}
		this.dateOfHire = dateOfHire;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getDivisionBranch() {
		return divisionBranch;
	}

	public void setDivisionBranch(String divisionBranch) {
		this.divisionBranch = divisionBranch;
	}

}
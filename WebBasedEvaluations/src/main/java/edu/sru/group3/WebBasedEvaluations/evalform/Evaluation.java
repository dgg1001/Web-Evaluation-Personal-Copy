package edu.sru.group3.WebBasedEvaluations.evalform;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.sru.group3.WebBasedEvaluations.domain.Group;
import edu.sru.group3.WebBasedEvaluations.domain.User;

/**
 * Contains all data, methods, and formatting information relating to an Evaluation.
 * Can be serialized for storage in a database.
 * 
 * @author Logan Racer
 */
public class Evaluation implements Serializable{

	private static final long serialVersionUID = 1L;

	private String evalID;
	private String title;
	private String description;

	private List <Section> sectionList;
	private List <String> warnings;
	private List <String> errors;
	private List <String> groupsList;
	private List <ComputeRange> computeRanges;

	private boolean completed;
	private boolean computeTotals;

	//Constructor ===============================================================
	public Evaluation() {
		evalID = "";
		title = "";
		description = "";

		sectionList = new ArrayList<Section>();
		warnings = new ArrayList<String>();
		errors = new ArrayList<String>();
		groupsList = new ArrayList<String>();
		computeRanges = new ArrayList<ComputeRange>();

		completed = false;
		computeTotals = false;
	}

	// Setters and Getters ====================================================
	public String getEvalID() {
		return this.evalID;
	}

	public void setEvalID(String id) {
		this.evalID = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean getCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public boolean getComputeTotals() {
		return this.computeTotals;
	}

	public void setComputeTotals(boolean comp) {
		this.computeTotals = comp;
	}

	// Groups list ===============================
	// Get List
	public List<String> getGroupsList() {
		return this.groupsList;
	}

	// Add
	public void addGroup(String group) {
		this.groupsList.add(group);
	}

	// Clear
	public void clearGroupsList() {
		this.groupsList.clear();
	}

	// ERRORS METHODS ==============================
	// Add
	public void addError(String error) {
		errors.add(error);
	}

	// Get
	public String getError(int index) {
		return this.errors.get(index);
	}

	// Count
	public int getErrorCount() {
		return this.errors.size();
	}

	// Get List
	public List<String> getErrors() {
		return errors;
	}

	// WARNINGS METHODS ==============================
	// Add
	public void addWarning(String warning) {
		warnings.add(warning);
	}

	// Get
	public String getWarning(int index) {
		return this.warnings.get(index);
	}

	// Count
	public int getWarningCount() {
		return this.warnings.size();
	}

	// Get List
	public List<String> getWarnings() {
		return warnings;
	}


	// SECTIONS METHODS ==============================
	// Add
	public void addSection(Section section) {
		sectionList.add(section);
	}

	// Get
	public Section getSection(int secIndex) {
		return this.sectionList.get(secIndex);
	}

	// Count
	public int getSectionCount() {
		return this.sectionList.size();
	}

	// Get List
	public List<Section> getSections() {
		return sectionList;
	}

	// COMPUTE RANGE METHODS ==============================
	// Set
	public void setComputeRanges(List<ComputeRange> ranges) {
		this.computeRanges = ranges;
	}

	// Add
	public void addComputeRange(ComputeRange range) {
		this.computeRanges.add(range);
	}

	// Get
	public ComputeRange getComputeRange(int rangeIndex) {
		return this.computeRanges.get(rangeIndex);
	}

	// Count
	public int getComputeRangeCount() {
		return this.computeRanges.size();
	}

	// Get List
	public List<ComputeRange> getComputeRanges() {
		return computeRanges;
	}

	
	/**
	 * Prints out information about the Evaluation to the Console.
	 */
	public void printEvaluation() {

		System.out.println("EVALUATION TITLE      : " + this.title);
		System.out.println("EVALUATION DESCRIPTION: " +this.description);

		for(int i = 0; i < this.getSectionCount(); i++) {

			System.out.println("\n\tSECTION #" + i + " ===================================================================");
			System.out.println("\tSECTION   : " + this.getSection(i).getSecName());
			System.out.println("\tS DESCRIPT: " + this.getSection(i).getSecDescription());
			System.out.println("\tS TOOL TIP: " + this.getSection(i).getSecToolTip());
			System.out.println("\tS TT VALUE: " + this.getSection(i).getSecToolTipVal());

			for(int j = 0; j < this.getSection(i).getQuestionCount(); j++) {

				System.out.println("\n\t\tQUESTION #" + j + " ==========================================================");
				System.out.println("\t\tQUESTION ID: " + this.getSection(i).getQuestion(j).getQuestID());
				System.out.println("\t\tQUESTION   : " + this.getSection(i).getQuestion(j).getQText());
				System.out.println("\t\tQ DESCRIPT : " + this.getSection(i).getQuestion(j).getQDescription());
				System.out.println("\t\tQ TOOL TIP : " + this.getSection(i).getQuestion(j).getQToolTip());
				System.out.println("\t\tQ TT VALUE : " + this.getSection(i).getQuestion(j).getQToolTipVal());
				System.out.println("\t\tQ RESP TYPE: " + this.getSection(i).getQuestion(j).getQResponseType());
				System.out.println("\t\tQ RESPONSE : " + this.getSection(i).getQuestion(j).getQResponse());
				System.out.println("\t\tQ REQUIRED : " + this.getSection(i).getQuestion(j).getRequired());

				for(int k = 0; k < this.getSection(i).getQuestion(j).getResponseCount(); k++) {

					System.out.println("\t\t\tRESPONSE: " + this.getSection(i).getQuestion(j).getResponse(k));
				}

				System.out.println("\t\tQ ROW      : " + this.getSection(i).getQuestion(j).getRow());
				System.out.println("\t\tQ COL      : " + this.getSection(i).getQuestion(j).getCol());

				for(int k = 0; k < this.getSection(i).getQuestion(j).getOptionCount(); k++) {

					System.out.println("\t\t\tOPTION: " + this.getSection(i).getQuestion(j).getOption(k));
				}
			}
		}
	}

	
	
	/**
	 * Returns the Question object having the ID passed to the method.
	 * 
	 * @param id ID of the Question
	 * @return Question
	 */
	public Question getQuestionById(int id) {
		int sec = 0;
		int quest = 0;

		for(int i = 0; i < this.getSectionCount(); i++) {
			for(int j = 0; j < this.getSection(i).getQuestionCount(); j++) {
				if (this.getSection(i).getQuestion(j).getQuestID() == id) {
					sec = i;
					quest = j;
				}
			}
		}

		return this.getSection(sec).getQuestion(quest);
	}

	
	
	/**
	 * Returns the Section object having the name passed to the method.
	 * 
	 * @param name Name of the Section
	 * @return Section
	 */
	public Section getSectionByName(String name) {
		if (!this.sectionList.isEmpty()) {
			boolean found = false;
			int index = 0;

			while (found == false) {
				if (this.getSection(index).getSecName().equals(name)) {
					found = true;
				} else {
					if (index == this.sectionList.size()-1) {
						found = true;
					} else {
						index++;
					}
				}
			}

			return this.getSection(index);
		} else {
			return new Section();
		}
	}



	/**
	 * Updates Question responses in the Evaluation object with a Array of given responses.
	 * 
	 * @param response - Array of responses
	 */
	public void saveResponses(String[] response) {
		int questID = 0;
		int length = 0;

		if(response != null) {
			length = response.length;
		}

		for (int i = 0; i < length; i++) {
			if (response[i].startsWith("ID") && response[i].endsWith("ID")) {
				questID = Integer.parseInt(response[i].replaceAll("\\D+",""));

				// If question is a CHECK BOX
				if (this.getQuestionById(questID).getQResponseType().equals("CHECK BOX")) {
					this.getQuestionById(questID).clearResponseList();
				}
			} else {
				
				// If question is a CHECK BOX
				if (this.getQuestionById(questID).getQResponseType().equals("CHECK BOX")) {

					this.getQuestionById(questID).addResponse(response[i]);

					// If question is a RADIO BUTTON
				} else if (this.getQuestionById(questID).getQResponseType().equals("RADIO BUTTON")){

					if (response[i].equals("~")) {
						this.getQuestionById(questID).clearAllResponses();
					} else {
						this.getQuestionById(questID).setQResponse(response[i]);
					}
					
					// All other questions
				} else {
					this.getQuestionById(questID).setQResponse(response[i]);
				}
			}
		}
	}


	/**
	 * Checks all Questions marked as 'required' in the Evaluation for completion.
	 * If Evaluation is found to be complete, complete will be set to true and returned list will be empty.
	 * If Evaluation is found to be incomplete, complete will be set to false.
	 * 
	 * @return incompQuests - Int List of Question ID's which are incomplete
	 */
	public List <Integer> verifyCompleted () {

		List <Integer> incompQuests = new ArrayList<Integer>();

		for (int i = 0; i < this.sectionList.size(); i++) {
			for (int j = 0; j < this.getSection(i).getQuestionCount(); j++) {

				// If question is a CHECK BOX type
				if (this.getSection(i).getQuestion(j).getQResponseType().equals("CHECK BOX")) {

					// If question is marked REQUIRED and response list is empty
					if (this.getSection(i).getQuestion(j).getRequired() && this.getSection(i).getQuestion(j).getResponses().isEmpty()) {
						incompQuests.add(this.getSection(i).getQuestion(j).getQuestID());
					}

					// if Question is not a CHECK BOX type
				} else {

					// If question is marked REQUIRED and response is blank
					if (this.getSection(i).getQuestion(j).getRequired() && this.getSection(i).getQuestion(j).getQResponse().isBlank()) {
						incompQuests.add(this.getSection(i).getQuestion(j).getQuestID());
					}
				}
			}
		}

		if (incompQuests.isEmpty()) {

			this.completed = true;
		}

		return incompQuests;
	}

	
	
	/**
	 * Calculates all 'COMPUTE' sections in the evaluation based on the responses to 'DROPDOWN' questions.
	 */
	public void updateCompute() {
		for (int i = 0; i < this.sectionList.size(); i++) {
			if (this.getSection(i).getSecName().startsWith("OVERALL:")) {
				for (int j = 0; j<this.getSection(i).getQuestions().size(); j++) {
					if (this.getSection(i).getQuestion(j).getQResponseType().equals("COMPUTE")) {
						String computeStr = "";

						Section computeSec = this.getSection(i);
						String sourceSecName = computeSec.getSecName().replace("OVERALL: ", "");
						Section sourceSec = this.getSectionByName(sourceSecName);

						double pointsEarned = sourceSec.getSectionEarnedPoints();
						double pointsMax = sourceSec.getSectionMaxPoints();
						double score;
						String computeCategory;
						
						
						if(pointsEarned > pointsMax) {
							computeStr = "<span>Points Earned: " + pointsEarned + "/" + pointsMax + "</span><br><span> SCORING ERROR: More Points earned than possbile </span>";
						} else {
							score = sourceSec.getSectionScore();
							computeCategory = computeSec.getQuestion(j).computeResultString(score);
							computeStr = "<span>Points Earned: " + pointsEarned + "/" + pointsMax + "</span><br><span> Resulting Grade: " + score + " - " + computeCategory + "</span>";
						}
						
						computeSec.getQuestion(j).setQResponse(computeStr);
					}
				}
			}
		}
	}

	/**
	 * Calls methods in each Section to update the appropriate strings with HTML formatting
	 * information for displaying tooltips on the Evaluation.
	 * Adds warning messages to the Evaluation if the process encounters any incorrect formatting.
	 */
	public void processToolTips() {
		for (int i = 0; i < this.getSectionCount(); i++) {

			this.warnings.addAll(getSection(i).processSecToolTip());

		}
	}
	
	
	
	/**
	 * Checks for errors or warnings that exist in the Evaluation data. Any errors are saved to the Evaluation Error List or Warning List.
	 * Note: Other error checking is performed prior when reading Evaluation data from an excel file.
	 */
	public void checkErrors () {

		//ID?
		if (this.evalID.isBlank()) {
			this.errors.add("Evaluation ID is blank. Evaluations must have exactly one keyword '<code>ID</code>' and corresponding unique value.");
		}
		//Title?
		if (this.title.isBlank()) {
			this.errors.add("Evaluation Title is blank. Evaluations must have exactly one keyword '<code>TITLE</code>' and corresponding value.");
		}
		//TitleDescription?
		if (this.description.isBlank()) {
			this.warnings.add("Evaluation Description is blank. It is recommended to have one one keyword '<code>TITLE DESCRIPTION</code>' and corresponding value.");
		}
		//Section(s)?
		if (this.sectionList.isEmpty()) {
			this.errors.add("Evaluation has no Sections. Evaluations must have one or more Sections defined using the keyword '<code>SECTION</code>' and corresponding Questions.");
		}

		// Check that multiple select options have at least two options (dropdown, check, radio)
		// Check Sections
		for (int i = 0; i < this.getSectionCount(); i++) {
			if (this.getSection(i).getQuestionCount() == 0) {
				this.errors.add("Section <u>'" + this.getSection(i).getSecName() + "'</u> has no questions.");
			}

			// Check Questions
			for (int j = 0; j < this.getSection(i).getQuestionCount(); j++) {

				if (	this.getSection(i).getQuestion(j).getQResponseType().equals("DROPDOWN") ||
						this.getSection(i).getQuestion(j).getQResponseType().equals("CHECK BOX") ||
						this.getSection(i).getQuestion(j).getQResponseType().equals("RADIO BUTTON")) {
					if (this.getSection(i).getQuestion(j).getOptionCount() < 2) {
						this.errors.add("Question <u>'" + this.getSection(i).getQuestion(j).getQText() + "'</u> in section <u>'" + this.getSection(i).getSecName() + "'</u> has less than two responses defined for a multiple choice question.");
					}
				}
			}
		}
	}

	
	
	/**
	 * Loads appropriate Pre-Load data into Section 0 of the Evaluation.
	 * 
	 * @param reviewee - User object of the reviewee
	 * @param group - Group object for which the reviewee belongs
	 */
	public void populatePreload(User reviewee, Group group) {
		Section preloadSection = this.getSection(0);

		for (int i = 0; i<preloadSection.getQuestionCount(); i++) {

			switch(preloadSection.getQuestion(i).getQText()) {
			case "EMPLOYEE NAME":
				preloadSection.getQuestion(i).setQResponse(reviewee.getFirstName() + " " + reviewee.getLastName());
				break;
			case "EMPLOYEE ID":
				preloadSection.getQuestion(i).setQResponse(Long.toString(reviewee.getId()));
				break;
			case "JOB TITLE":
				preloadSection.getQuestion(i).setQResponse(reviewee.getJobTitle());
				break;
			case "DATE OF HIRE":
				preloadSection.getQuestion(i).setQResponse(reviewee.getDateOfHire());
				break;
			case "EMAIL ADDRESS":
				preloadSection.getQuestion(i).setQResponse(reviewee.getEmail());
				break;
			case "SUPERVISOR":
				preloadSection.getQuestion(i).setQResponse(reviewee.getSupervisor());
				break;
			case "COMPANY NAME":
				preloadSection.getQuestion(i).setQResponse(reviewee.getCompanyName());
				break;
			case "DIVISION/BRANCH":
				preloadSection.getQuestion(i).setQResponse(reviewee.getDivisionBranch());
				break;
			case "GROUP NO.":
				preloadSection.getQuestion(i).setQResponse(Long.toString(group.getId()));
				break;
			default:
				preloadSection.getQuestion(i).setQResponse("DATA NOT FOUND");
			}
		}
	}
}
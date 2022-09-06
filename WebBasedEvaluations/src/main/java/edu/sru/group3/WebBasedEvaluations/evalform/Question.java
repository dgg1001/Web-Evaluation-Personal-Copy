package edu.sru.group3.WebBasedEvaluations.evalform;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains all data, methods, and formatting information of a
 * Question within an Evaluation.
 * Can be serialized for storage in a database.
 * 
 * @author Logan Racer
 */
public class Question implements Serializable{

	private static final long serialVersionUID = 1L;

	private String qText;
	private String qDescription;
	private String qToolTip;
	private String qToolTipVal;
	private String qToolTipMarker;
	private String qResponseType;
	private String qResponse;
	private String contolledByStr;

	private List<String> responseOpt;
	private List<String> responseList;
	private List<ComputeRange> computeRanges;

	private int row;
	private int col;
	private int questID;
	private Integer visControlledBy;
	private Integer visControls;

	private boolean required = false;

	//Constructor ===============================================================
	public Question() {
		qText = "";
		qDescription = "";
		qToolTip = "";
		qToolTipVal = "";
		qToolTipMarker = "";
		qResponseType = "";
		qResponse = "";
		contolledByStr = "";

		responseOpt = new ArrayList<String>();
		responseList = new ArrayList<String>();
		computeRanges = new ArrayList<ComputeRange>();

		row = 0;
		col = 0;
		questID = 0;
		visControlledBy = null;
		visControls = null;

		required = false;
	}

	// Getters and setters ======================================================
	public String getQText() {
		return qText;
	}

	public void setQText(String qText) {
		this.qText = qText;
	}

	public String getQDescription() {
		return qDescription;
	}

	public void setQDescription(String qDescription) {
		this.qDescription = qDescription;
	}

	public String getQToolTip() {
		return qToolTip;
	}

	public void setQToolTip(String qToolTip) {
		this.qToolTip = qToolTip;
	}

	public String getQToolTipVal() {
		return qToolTipVal;
	}

	public void setQToolTipVal(String qToolTipVal) {
		this.qToolTipVal = qToolTipVal;
	}

	public String getQToolTipMarker() {
		return qToolTipMarker;
	}

	public void setQToolTipMarker(String marker) {
		this.qToolTipMarker = marker;
	}

	public String getQResponseType() {
		return qResponseType;
	}

	public void setQResponseType(String qResponseType) {
		this.qResponseType = qResponseType;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public int getQuestID() {
		return questID;
	}

	public void setQuestID(int questID) {
		this.questID = questID;
	}

	public String getQResponse() {
		return qResponse;
	}

	public void setQResponse(String qResponse) {
		this.qResponse = qResponse;
	}

	public boolean getRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public Integer getVisControlledBy() {
		return visControlledBy;
	}

	public void setVisControlledBy(int visControlledBy) {
		this.visControlledBy = visControlledBy;
		this.contolledByStr = "controlled-by-ID" + visControlledBy + "ID";
	}

	public Integer getVisControls() {
		return visControls;
	}

	public void setVisControls(Integer visControls) {
		this.visControls = visControls;
	}

	public String getContolledByStr() {
		return contolledByStr;
	}

	public void setContolledByStr(String contolledByStr) {
		this.contolledByStr = contolledByStr;
	}

	// RESPONSE OPTIONS METHODS ===================================
	// Add
	public void addOption(String option) {
		responseOpt.add(option);
	}

	// Set
	public void setOptions(List<String> opts) {
		this.responseOpt = opts;
	}

	// Get
	public String getOption(int roIndex) {
		return this.responseOpt.get(roIndex);
	}
	
	// Count
	public int getOptionCount() {
		return  this.responseOpt.size();
	}
	
	// Get List
	public List<String> getOptions() {
		return responseOpt;
	}

	// RESPONSES METHODS =========================================
	// Add
	public void addResponse(String response) {
		responseList.add(response);
	}

	// Get
	public String getResponse(int index) {
		return this.responseList.get(index);
	}
	
	// Get List
	public List<String> getResponses() {
		return responseList;
	}

	// Clear
	public void clearResponseList() {
		this.responseList.clear();
	}

	// Clear all responses
	public void clearAllResponses() {
		this.responseList.clear();
		this.qResponse = "";
	}

	// Count
	public int getResponseCount() {
		return  this.responseList.size();
	}

	// Is Empty
	public boolean responseListIsEmpty() {
		if(this.responseList.size() == 0) {
			return true;
		} else {
			return false;
		}
	}

	// Check Membership
	public boolean presentInResponses(String string) {
		if (this.responseList.contains(string)) {
			return true;
		} else {
			return false;
		}
	}

	// COMPUTE RANGE METHODS ====================================
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
	 * Checks for the max earnable points possible for 'DROPDOWN' type questions.
	 * Non-dropdown questions will return 0. Questions where the response 
	 * is 'N/A' will also return 0.
	 * 
	 * 
	 * @return maxPoints - The max score possible for a question.
	 */
	public int getQuestionMaxPointsNaSensitive() {
		int max = 0;
		if (this.qResponseType.equals("DROPDOWN") && this.qResponse.matches(".*[0-9].*")) {
			max = getQuestionMaxPoints();
		}
		return max;
	}

	
	
	/**
	 * Checks for the max earnable points possible for 'DROPDOWN' type questions.
	 * Non-dropdown questions will return 0. This method will return the true max
	 * points of a question, regardless of 'N/A' responses.
	 * 
	 * 
	 * @return maxPoints - The max score possible for a question.
	 */
	public int getQuestionMaxPoints() {
		int max = 0;
		if (this.qResponseType.equals("DROPDOWN")) {
			for (int i = 0; i < this.responseOpt.size(); i++) {
				if (this.responseOpt.get(i).matches(".*[0-9].*")) {
					int value = Integer.parseInt(this.responseOpt.get(i).replaceAll("\\D+",""));
					if (value > max) {
						max = value;
					}
				}
			}
		}
		return max;
	}

	
	
	/**
	 * Checks for the earned points for 'DROPDOWN' type questions.
	 * Non-dropdown questions will return 0. 'N/A' responses will return 0.
	 * 
	 * @return earnedPoints - Value of the points earned in the Question response
	 */
	public int getQuestionEarnedPoints() {
		int earned = 0;
		if(this.getQuestionMaxPointsNaSensitive() != 0) {
			if (this.qResponseType.equals("DROPDOWN") && this.qResponse.matches(".*[0-9].*")) {
				earned = Integer.parseInt(this.qResponse.replaceAll("\\D+",""));
			}
		}

		return earned;
	}

	
	
	/**
	 * Returns the appropriate string associated with the score provided.
	 * Intended to be used for 'COMPUTE' questions to display the proper grading text.
	 * Grading scale and text is provided by the List of ComputeRanges.
	 * 
	 * @param score
	 * @return resultString - Written grade earned from the provided score
	 */
	public String computeResultString(double score) {
		String result = "NO SCORE AVAILABLE";

		// If response list is not empty AND question is a COMPUTE...
		if (this.qResponseType.equals("COMPUTE")) {
			for (int i = 0; i < this.computeRanges.size(); i++) {
				if (score <= this.computeRanges.get(i).getRangeValMax() && score >= this.computeRanges.get(i).getRangeValMin()) {
					result = this.computeRanges.get(i).getRangeName();
				}
			}
		}
		return result;
	}

	
	
	/**
	 * Updates appropriate strings in the Question object with HTML formatting
	 * information for displaying tooltips on the Evaluation.
	 * 
	 * @param secName - Name of the parent section of the Question. Used for generating accurate warning messages.
	 * @return warnings - List of Warnings encountered while processing the tooltips.
	 */
	public List<String> processQuestionToolTip (String secName) {

		List<String> warning = new ArrayList<String>();
		String toolTipFormatted = this.qToolTip;

		// if question has a tool tip value defined
		if (!this.qToolTipVal.isBlank()) {

			// if section tool tip not defined
			if (this.qToolTipMarker.isBlank()) {
				warning.add("Question '<u>" + this.qText + "</u>' in Section '<u>" + secName + "</u>' has blank or missing keyword '<code>QUESTION TOOL TIP MARKER</code>' and will default to '<code>DASHED UNDERLINE</code>'.");
				this.qToolTipMarker = "DASHED UNDERLINE";
			}

			// if question tool tip not defined
			if (this.qToolTip.isBlank()) {

				warning.add("Question '<u>" + this.qText + "</u>' in Section '<u>" + secName + "</u>' has blank or missing keyword '<code>QUESTION TOOL TIP</code>'. Tool Tip Value will be applied to entire Question Name.");

				if (this.qToolTipMarker.equals("DASHED UNDERLINE")) {
					toolTipFormatted = "<span class=\"dashed-underline\" title=\"" + this.qToolTipVal + "\">" + this.qText + "</span>";
				} else if (this.qToolTipMarker.equals("SOLID UNDERLINE")) {
					toolTipFormatted = "<span class=\"solid-underline\" title=\"" + this.qToolTipVal + "\">" + this.qText + "</span>";
				} else {
					toolTipFormatted = "<span title=\"" + this.qToolTipVal + "\">" + this.qText + "<sup>&#9432;</sup></span>";
				}

				this.qText = toolTipFormatted;

				// if question tool tip defined
			} else {

				if (this.qText.contains(this.qToolTip)) {

					if (this.qToolTipMarker.equals("DASHED UNDERLINE")) {
						toolTipFormatted = "<span class=\"dashed-underline\" title=\"" + this.qToolTipVal + "\">" + this.qToolTip + "</span>";
					} else if (this.qToolTipMarker.equals("SOLID UNDERLINE")) {
						toolTipFormatted = "<span class=\"solid-underline\" title=\"" + this.qToolTipVal + "\">" + this.qToolTip + "</span>";
					} else {
						toolTipFormatted = "<span title=\"" + this.qToolTipVal + "\">" + this.qToolTip + "<sup>&#9432;</sup></span>";
					}

					this.qText = this.qText.replace(this.qToolTip, toolTipFormatted);

				} else {

					warning.add("Question '<u>" + this.qText + "</u>' in Section '<u>" + secName + "</u>' has value for keyword '<code>QUESTION TOOL TIP</code>' which is not a substring of Question Name. Tool Tip Value will be applied to entire Question Name.");

					if (this.qToolTipMarker.equals("DASHED UNDERLINE")) {
						toolTipFormatted = "<span class=\"dashed-underline\" title=\"" + this.qToolTipVal + "\">" + this.qText + "</span>";
					} else if (this.qToolTipMarker.equals("SOLID UNDERLINE")) {
						toolTipFormatted = "<span class=\"solid-underline\" title=\"" + this.qToolTipVal + "\">" + this.qText + "</span>";
					} else {
						toolTipFormatted = "<span title=\"" + this.qToolTipVal + "\">" + this.qText + "<sup>&#9432;</sup></span>";
					}

					this.qText = toolTipFormatted;
				}
			}

			// if question has no tool tip value defined
		} else {

			//and has info in tool tip and tool tip marker
			if (!this.qToolTip.isBlank()) {
				warning.add("Question '<u>" + this.qText + "</u>' in Section '<u>" + secName + "</u>' has keyword '<code>QUESTION TOOL TIP</code>' defined but no value for keyword '<code>QUESTION TOOL TIP VALUE</code>'. Tool Tip will not be displayed.");
			}
			if (!this.qToolTipMarker.isBlank()) {
				warning.add("Question '<u>" + this.qText + "</u>' in Section '<u>" + secName + "</u>' has keyword '<code>QUESTION TOOL TIP MARKER</code>' defined but no value for keyword '<code>QUESTION TOOL TIP VALUE</code>'. Tool Tip will not be displayed.");
			}
		}
		return warning;
	}
}
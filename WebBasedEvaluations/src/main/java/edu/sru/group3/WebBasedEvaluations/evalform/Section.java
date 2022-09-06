package edu.sru.group3.WebBasedEvaluations.evalform;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains all data, methods, and formatting information of
 * a Section within an Evaluation.
 * Can be serialized for storage in a database.
 * 
 * @author Logan Racer
 */
public class Section implements Serializable{

	private static final long serialVersionUID = 1L;

	private String secName;
	private String secDescription;
	private String secToolTip;
	private String secToolTipVal;
	private String secToolTipMarker;

	private List<Question> questionList;

	// Constructor ====================================================
	public Section() {
		secName = "";
		secDescription = "";
		secToolTip = "";
		secToolTipVal = "";
		secToolTipMarker = "";

		questionList = new ArrayList<Question>();
	}

	// Setters and getters ===========================================
	public String getSecName() {
		return secName;
	}

	public void setSecName(String secName) {
		this.secName = secName;
	}

	public String getSecDescription() {
		return secDescription;
	}

	public void setSecDescription(String secDescription) {
		this.secDescription = secDescription;
	}

	public String getSecToolTip() {
		return secToolTip;
	}

	public void setSecToolTip(String secToolTip) {
		this.secToolTip = secToolTip;
	}

	public String getSecToolTipVal() {
		return secToolTipVal;
	}

	public void setSecToolTipVal(String secToolTipVal) {
		this.secToolTipVal = secToolTipVal;
	}

	public String getSecToolTipMarker() {
		return secToolTipMarker;
	}

	public void setSecToolTipMarker(String marker) {
		this.secToolTipMarker = marker;
	}

	// QUESTION METHODS ========================================
	// Add
	public void addQuestion(Question question) {
		questionList.add(question);
	}

	// Get
	public Question getQuestion(int qIndex) {
		return this.questionList.get(qIndex);
	}

	// Count
	public int getQuestionCount() {
		return this.questionList.size();
	}

	// Get List
	public List<Question> getQuestions() {
		return questionList;
	}



	/**
	 * Returns the number of columns for Questions that should appear in the Section.
	 * 
	 * @return maxColumns - Max number of question columns in the Section.
	 */
	public int getMaxColumns() {
		int cols = 0;
		for (int i = 0; i < this.getQuestionCount(); i++) {
			if (this.getQuestion(i).getCol() > cols) {
				cols = this.getQuestion(i).getCol();
			}
		}
		return cols;
	}



	/**
	 * Returns the appropriate class name for column formatting using HTML Bootstrap.
	 * Determines the column layout of the Section.
	 * 
	 * @return classString - String which can be used as an HTML element class attribute.
	 */
	public String getRowClass () {
		String classString = "";
		int cols = this.getMaxColumns();

		if (cols==0 || cols==1) {
			classString = "col-12";
		} else if (cols==2) {
			classString = "col-6";
		} else if (cols==3) {
			classString = "col-4";
		} else if (cols==4) {
			classString = "col-3";
		} else {
			classString = "col";
		}

		return classString;
	}



	/**
	 * Checks all Questions in the Section for 'DROPDOWN' type.
	 * 
	 * @return boolean - True if Section has any 'DROPDOWN' questions.
	 */
	public boolean hasDropdownQuestions() {
		int dropQuestCount = 0;
		for (int i = 0; i < this.getQuestionCount(); i++) {
			if (this.getQuestion(i).getQResponseType().equals("DROPDOWN")) {
				dropQuestCount++;
			}
		}
		if (dropQuestCount > 0) {
			return true;
		} else {
			return false;
		}
	}

	
	
	/**
	 * Checks all score-able Questions in the Section and calculates the
	 * maximum possible points for the Section.
	 * 
	 * @return maxPoints - Max score possible for the Section
	 */
	public int getSectionMaxPoints() {
		int max = 0;

		// If there are questions
		if (!this.questionList.isEmpty()) {
			for (int i = 0; i < questionList.size(); i++) {
				if (this.getQuestion(i).getQResponseType().equals("DROPDOWN")) {
					max = max + this.getQuestion(i).getQuestionMaxPointsNaSensitive();
				}
			}
		}
		return max;
	}

	
	
	/**
	 * Checks all score-able Questions in the Section and calculates the
	 * total points earned in the Section.
	 * 
	 * @return earnedPoints - Total earned points for the Section
	 */
	public int getSectionEarnedPoints() {
		int earned = 0;

		// If there are questions
		if (!this.questionList.isEmpty()) {
			for (int i = 0; i < questionList.size(); i++) {
				if (this.getQuestion(i).getQResponseType().equals("DROPDOWN")) {
					earned = earned + this.getQuestion(i).getQuestionEarnedPoints();
				}
			}
		}
		return earned;
	}

	
	
	/**
	 * Calculates the score earned in each Section.
	 * 
	 * @return sectionScore - Score of the section
	 */
	public double getSectionScore() {
		double score = 0.0;
		if (!this.questionList.isEmpty()) {
			int max = this.getSectionMaxPoints();
			int earned = this.getSectionEarnedPoints();
			
			if (earned > max) {
				return 0.0;
			}
			
			int scale = this.getQuestion(0).getQuestionMaxPoints();
			score = ((double)earned / (double)max) * (double)scale;
			score = Math.round(score*100.00)/100.00;
		}
		return score;
	}

	
	
	/**
	 * Updates appropriate strings in the Section and Questions with HTML formatting
	 * information for displaying tooltips on the Evaluation.
	 * 
	 * @return warnings - List of Warnings encountered while processing the tooltips.
	 */
	public List<String> processSecToolTip () {

		List<String> warning = new ArrayList<String>();
		String toolTipFormatted = this.secToolTip;

		// if section has a tool tip value defined
		if (!this.secToolTipVal.isBlank()) {

			// if section tool tip not defined
			if (this.secToolTipMarker.isBlank()) {
				warning.add("Section '<u>" + this.secName + "</u>' keyword '<code>SECTION TOOL TIP MARKER</code>' is blank or keyword is missing and will default to '<code>DASHED UNDERLINE</code>'.");
				this.secToolTipMarker = "DASHED UNDERLINE";
			}

			// if section tool tip not defined
			if (this.secToolTip.isBlank()) {
				warning.add("Section '<u>" + this.secName + "</u>' keyword '<code>SECTION TOOL TIP</code>' is blank. Tool Tip Value will be applied to entire Section Description.");

				if (this.secToolTipMarker.equals("DASHED UNDERLINE")) {
					toolTipFormatted = "<span class=\"dashed-underline\" title=\"" + this.secToolTipVal + "\">" + this.secDescription + "</span>";
				} else if (this.secToolTipMarker.equals("SOLID UNDERLINE")) {
					toolTipFormatted = "<span class=\"solid-underline\" title=\"" + this.secToolTipVal + "\">" + this.secDescription + "</span>";
				} else {
					toolTipFormatted = "<span title=\"" + this.secToolTipVal + "\">" + this.secDescription + "<sup>&#9432;</sup></span>";
				}

				this.secDescription = toolTipFormatted;

				// if section tool tip defined
			} else {
				if (this.secDescription.contains(this.secToolTip)) {
					if (this.secToolTipMarker.equals("DASHED UNDERLINE")) {
						toolTipFormatted = "<span class=\"dashed-underline\" title=\"" + this.secToolTipVal + "\">" + this.secToolTip + "</span>";
					} else if (this.secToolTipMarker.equals("SOLID UNDERLINE")) {
						toolTipFormatted = "<span class=\"solid-underline\" title=\"" + this.secToolTipVal + "\">" + this.secToolTip + "</span>";
					} else {
						toolTipFormatted = "<span title=\"" + this.secToolTipVal + "\">" + this.secToolTip + "<sup>&#9432;</sup></span>";
					}

					this.secDescription = this.secDescription.replace(this.secToolTip, toolTipFormatted);

				} else {

					warning.add("Section '<u>" + this.secName + "</u>' keyword '<code>SECTION TOOL TIP</code>' is not a substring of '<code>SECTION DESCRIPTION</code>'. Tool Tip Value will be applied to entire Section Description.");

					if (this.secToolTipMarker.equals("DASHED UNDERLINE")) {
						toolTipFormatted = "<span class=\"dashed-underline\" title=\"" + this.secToolTipVal + "\">" + this.secDescription + "</span>";
					} else if (this.secToolTipMarker.equals("SOLID UNDERLINE")) {
						toolTipFormatted = "<span class=\"solid-underline\" title=\"" + this.secToolTipVal + "\">" + this.secDescription + "</span>";
					} else {
						toolTipFormatted = "<span title=\"" + this.secToolTipVal + "\">" + this.secDescription + "<sup>&#9432;</sup></span>";
					}
					this.secDescription = toolTipFormatted;
				}
			}

			// if section has no tool tip value defined
		} else {

			//and has info in tool tip and tool tip marker
			if (!this.secToolTip.isBlank()) {
				warning.add("Section '<u>" + this.secName + "</u>' has keyword '<code>SECTION TOOL TIP</code>' defined but no value for keyword '<code>SECTION TOOL TIP VALUE</code>'. Tool Tip will not be displayed.");
			}
			if (!this.secToolTipMarker.isBlank()) {
				warning.add("Section '<u>" + this.secName + "</u>' has keyword '<code>SECTION TOOL TIP MARKER</code>' defined but no value for keyword '<code>SECTION TOOL TIP VALUE</code>'. Tool Tip will not be displayed.");
			}
		}

		for (int i = 0; i < this.getQuestionCount(); i++) {
			warning.addAll(this.getQuestion(i).processQuestionToolTip(this.secName));
		}

		return warning;
	}
}
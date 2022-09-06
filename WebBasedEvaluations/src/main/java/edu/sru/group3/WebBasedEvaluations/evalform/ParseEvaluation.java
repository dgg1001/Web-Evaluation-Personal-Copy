package edu.sru.group3.WebBasedEvaluations.evalform;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Contains the methods for retrieving Evaluation data from an XML file.
 * 
 * @author Logan Racer
 */
public class ParseEvaluation {
	
	/**
	 * Static methods which reads all data from an XML file and assigns the appropriate values to the Evaluation object.
	 * Will create and populate Sections and Questions as defined in the XML file.
	 * Automatically generates a list of Errors and Warnings which will be saved to the Evaluation
	 * object if the format of the XML file is incorrect.
	 * 
	 * @param eval - Evaluation object that the Data should be loaded into.
	 * @param path - String containing the path where the XML file is located.
	 * @return eval - Evaluation object populated with the data
	 */
	public static Evaluation parseEvaluation(Evaluation eval, String path) {
		
		try {
			SAXParserFactory fact = SAXParserFactory.newInstance();
			SAXParser saxParser = fact.newSAXParser();

			DefaultHandler handle = new DefaultHandler() {
				
				boolean data = false;
				
				int sCount = 0;
				int qCount = 0;
				int row = 0;
				int col = 0;
				int questID = 0;
				int dropValCount = 0;
				int rangeMinCount = 0;
				int rangeMaxCount = 0;
				int controllingQID = -1;
				int dropdownErrors = 0;
				int computeErrors = 0;
				
				boolean preLoad = false;
				boolean hasFileType = false;
				boolean hasDropdownReq = false;
				boolean hasComputeReq = false;
				
				String prevElem = "";
				
				List <String> dropdownOptions = new ArrayList<String>();
				List <ComputeRange> computeRanges = new ArrayList<ComputeRange>();

				public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException{
					if(qName.equals("Data")) data = true;
				}

				public void endElement(String uri, String localName, String qName) throws SAXException{}

				public void characters(char[] ch, int start, int length) throws SAXException {
					if(data) {
						String content = new String(ch, start, length);
						//System.out.println("Data :" + content);
						data = false;

						if (content.startsWith("PRE-LOAD ROW")) {
							preLoad = true;
							//row++;
						}
						if (content.startsWith("COL")) {
							col = Character.getNumericValue(content.charAt(content.length()-1));
							content = "COL";
						}

						// Storing xml data in Evaluation object

						// Store prev element name
						switch (content) {

						case "ID": 
							prevElem = content;
							break;
							
						case "FILE TYPE": 
							prevElem = content;
							hasFileType = true;
							break;
							
						case "TITLE": 
							prevElem = content;
							break;
							
						case "TITLE DESCRIPTION":
							prevElem = content;
							break;
							
						// DROPDOWN
						case "DROPDOWN OPTIONS": 
							prevElem = content;
							break;
							
						case "DROPDOWN VALUE":
							prevElem = content;
							break;
							
						case "DROPDOWN INCLUDE N/A":
							prevElem = content;
							break;
							
						//COMPUTE
						case "COMPUTE RANGE NAME": 
							prevElem = content;
							break;
							
						case "COMPUTE RANGE SCORE MIN":
							prevElem = content;
							break;
							
						case "COMPUTE RANGE SCORE MAX":
							prevElem = content;
							break;
							
						case "COMPUTE TOTALS":
							prevElem = content;
							break;
							
						case "SECTION": 
							prevElem = content;
							sCount++;
							qCount = 0;
							row = 0;
							preLoad = false;
							eval.addSection(new Section());
							break;
							
						case "SECTION DESCRIPTION": 
							prevElem = content;
							break;
							
						case "SECTION TOOL TIP": 
							prevElem = content;
							break;
							
						case "SECTION TOOL TIP VALUE": 
							prevElem = content;
							break;
							
						case "SECTION TOOL TIP MARKER":
							prevElem = content;
							break;
							
						case "QUESTION":
							prevElem = content;
							break;
							
						case "REQUIRED":
							if (eval.getSection(sCount-1).getQuestion(qCount-1).getVisControlledBy() != null) {
								eval.addError("Question <u>" + eval.getSection(sCount-1).getQuestion(qCount-1).getQText() + "</u> in section <u>" + eval.getSection(sCount-1).getSecName() + "</u> cannot be both <code>'REQUIRED'</code> and <code>'CONTROLLED'</code>.");
							} else {
								eval.getSection(sCount-1).getQuestion(qCount-1).setRequired(true);
							}
							break;
							
						case "COL": 
							prevElem = content;
							qCount++;
							questID++;
							eval.getSection(sCount-1).addQuestion(new Question());
							break;
							
						case "CONTROLS QUESTION":
					
							boolean invalid = true;
							if(eval.getSection(sCount-1).getQuestion(qCount-1).getQResponseType().equals("RADIO BUTTON")) {
								if(eval.getSection(sCount-1).getQuestion(qCount-1).getOptionCount() == 2) {

									if(		eval.getSection(sCount-1).getQuestion(qCount-1).getOptions().get(0).toUpperCase().equals("YES") &&
											eval.getSection(sCount-1).getQuestion(qCount-1).getOptions().get(1).toUpperCase().equals("NO")) {
										controllingQID = questID;
										invalid = false;
									}
								}
							}
							
							if (invalid) {
								eval.addError("Question <u>" + eval.getSection(sCount-1).getQuestion(qCount-1).getQText() + "</u> in section <u>" + eval.getSection(sCount-1).getSecName() + "</u> is not permitted to contol visibility of other questions. Controlling questions must be of type <code>'RADIO BUTTON'</code> and <code>'BOOLEAN Y/N'</code>.");
							}
							prevElem = "";
							break;
							
						case "CONTROLLED":
							if (eval.getSection(sCount-1).getQuestion(qCount-1).getRequired()) {
								eval.addError("Question <u>" + eval.getSection(sCount-1).getQuestion(qCount-1).getQText() + "</u> in section <u>" + eval.getSection(sCount-1).getSecName() + "</u> cannot be both <code>'REQUIRED'</code> and <code>'CONTROLLED'</code>.");
							} else if (eval.getSection(sCount-1).getQuestion(qCount-1).getQResponseType().equals("CHECK BOX")) {
								eval.addError("Question <u>" + eval.getSection(sCount-1).getQuestion(qCount-1).getQText() + "</u> in section <u>" + eval.getSection(sCount-1).getSecName() + "</u> cannot be <code>'CONTROLLED'</code> beacuse it is of type <code>'CHECK BOX'</code>.");
							} else if (eval.getSection(sCount-1).getQuestion(qCount-1).getQResponseType().equals("COMPUTE")) {
								eval.addError("Question <u>" + eval.getSection(sCount-1).getQuestion(qCount-1).getQText() + "</u> in section <u>" + eval.getSection(sCount-1).getSecName() + "</u> cannot be <code>'CONTROLLED'</code> beacuse it is of type <code>'COMPUTE'</code>.");
							} else if (eval.getSection(sCount-1).getQuestion(qCount-1).getQResponseType().equals("PRE-LOAD")) {
								eval.addError("Question <u>" + eval.getSection(sCount-1).getQuestion(qCount-1).getQText() + "</u> in section <u>" + eval.getSection(sCount-1).getSecName() + "</u> cannot be <code>'CONTROLLED'</code> beacuse it is of type <code>'PRE-LOAD'</code>.");
							} else if (controllingQID == -1) {
								eval.addError("Question <u>" + eval.getSection(sCount-1).getQuestion(qCount-1).getQText() + "</u> in section <u>" + eval.getSection(sCount-1).getSecName() + "</u> cannot be <code>'CONTROLLED'</code> beacuse the controlling question was never defined using keyword <code>'CONTROLS QUESTION'</code>.");
							} else {
								eval.getSection(sCount-1).getQuestion(qCount-1).setVisControlledBy(controllingQID);
								eval.getQuestionById(controllingQID).setVisControls(questID);
								controllingQID = -1;
							}
							prevElem = "";
							break;
							
						case "SINGLE LINE TEXT": 
							eval.getSection(sCount-1).getQuestion(qCount-1).setQResponseType("SINGLE LINE TEXT");
							break;
							
						case "PARAGRAPH TEXT": 
							eval.getSection(sCount-1).getQuestion(qCount-1).setQResponseType("PARAGRAPH TEXT");
							break;
							
						case "RADIO BUTTON": 
							eval.getSection(sCount-1).getQuestion(qCount-1).setQResponseType("RADIO BUTTON");
							prevElem = content;
							break;
							
						case "CHECK BOX": 
							eval.getSection(sCount-1).getQuestion(qCount-1).setQResponseType("CHECK BOX");
							prevElem = content;
							break;
							
						case "DROPDOWN": 
							eval.getSection(sCount-1).getQuestion(qCount-1).setQResponseType("DROPDOWN");
							if (!dropdownOptions.isEmpty() && hasDropdownReq) {
								eval.getSection(sCount-1).getQuestion(qCount-1).setOptions(dropdownOptions);
							} else {
								if (dropdownErrors == 0) {
									eval.addError("Dropdown Options were not applied successfully. Ensure all required keywords are defined at the top of the document in the following order <code>'DROPDOWN OPTIONS'</code> ,<code>'DROPDOWN VALUE'</code> ,<code>'DROPDOWN INCLUDE N/A'</code>.");
									dropdownErrors++;
								}
							}
							prevElem = content;
							break;
							
						case "COMPUTE":
							eval.getSection(sCount-1).getQuestion(qCount-1).setQResponseType("COMPUTE");
							eval.getSection(sCount-1).getQuestion(qCount-1).setQText("SECTION SCORE:");
							eval.getSection(sCount-1).getQuestion(qCount-1).setQResponse("<span>Scoring Information Not Available</span>");
							
							if (!computeRanges.isEmpty() && hasComputeReq) {
								eval.getSection(sCount-1).getQuestion(qCount-1).setComputeRanges(computeRanges);
								eval.setComputeRanges(computeRanges);
							} else {
								if (computeErrors == 0) {
									eval.addError("Compute Options were not applied successfully. Ensure all required keywords are defined at the top of the document in the following order <code>'COMPUTE RANGE NAME'</code> ,<code>'COMPUTE RANGE SCORE MIN'</code> ,<code>'COMPUTE RANGE SCORE MAX'</code> ,<code>'COMPUTE TOTALS'</code>.");
									computeErrors++;
								}
							}
							
							prevElem = content;
							break;
							
						case "QUESTION DESCRIPTION": 
							prevElem = content;
							break;
							
						case "QUESTION TOOL TIP":
							prevElem = content;
							break;
							
						case "QUESTION TOOL TIP VALUE":
							prevElem = content;
							break;
							
						case "QUESTION TOOL TIP MARKER":
							prevElem = content;
							break;
							
						default:
							// Store element content
							switch (prevElem) {
							case "FILE TYPE":
								if (!content.equals("EVALUATION")) {
									eval.addError("Value for keyword '<code>FILE TYPE</code>' is not '<code>EVALUATION</code>'. Check that the correct file has been uploaded.");
								}
								prevElem = "";
								break;
								
							case "ID":
								eval.setEvalID(content);
								prevElem = "";
								break;
								
							case "TITLE":
								eval.setTitle(content);
								prevElem = "";
								break;
								
							case "TITLE DESCRIPTION": 
								eval.setDescription(content);
								prevElem = "";
								break;
								
							case "DROPDOWN OPTIONS":
								dropdownOptions.add(content);
								break;
								
							case "DROPDOWN VALUE":
								if (!dropdownOptions.isEmpty()) {
									
									if (content.matches(".*[0-9].*")) {
	
										String tempStr = content;
										if (content.contains(".")) {
											tempStr = StringUtils.substringBefore(tempStr, ".");
										}
										int value = Integer.parseInt(tempStr.replaceAll("\\D+",""));
										
										for (int i = 0; i<dropdownOptions.size(); i++) {
											if (i == dropValCount) {
												String formatted = dropdownOptions.get(i);
												formatted = value + " - " + formatted;
												
												dropdownOptions.set(i, formatted);
											}
										}
	
										dropValCount++;
	
									} else {
										eval.addError("All values entered for <code>'DROPDOWN VALUE'</code> must be integers.");
									}
								} else {
									eval.addError("Cannot add value <u>" + content + "</u> for <code>'DROPDOWN VALUE'</code>. <code>'DROPDOWN OPTIONS'</code> must be defined first. ");
								}
								
								break;
								
							case "DROPDOWN INCLUDE N/A":
								if (dropValCount != dropdownOptions.size()) {
									eval.addError("Incorrect number of values provided for keyword <code>'DROPDOWN VALUE'</code>.");
								}
								
								if (	content.toUpperCase().equals("TRUE") || content.toUpperCase().equals("T") || content.equals("1") ||
										content.toUpperCase().equals("FALSE") || content.toUpperCase().equals("F") || content.equals("0")) {
									
									if (content.toUpperCase().equals("TRUE") || content.toUpperCase().equals("T") || content.equals("1")) {
										dropdownOptions.add("N/A");
										
									}
									
									hasDropdownReq = true;
								
								} else {
									eval.addError("Keyword '<u>DROPDOWN INCLUDE N/A</u>' requires either TRUE or FALSE.");
								}
								prevElem = "";
								break;
								
							case "COMPUTE RANGE NAME": 
								ComputeRange range = new ComputeRange();
								range.setRangeName(content);
								computeRanges.add(range);
								break;
								
							case "COMPUTE RANGE SCORE MIN":
								if (!computeRanges.isEmpty()) {
									
									if (content.matches(".*[0-9].*")) {

										double value = Double.parseDouble(content);

										
										for (int i = 0; i<computeRanges.size(); i++) {
											if (i == rangeMinCount) {
												ComputeRange range1 = computeRanges.get(i);
												range1.setRangeValMin(value);
												computeRanges.set(i, range1);
											}
										}
										rangeMinCount++;

									} else {
										eval.addError("All values entered for keyword <code>'COMPUTE RANGE SCORE MIN'</code> must be numbers.");
									}
								} else {
									eval.addError("Cannot add value <u>" + content + "</u> for <code>'COMPUTE RANGE SCORE MIN'</code> because no range names were defined. <code>'COMPUTE RANGE NAME' </code>(s) must be defined first.");
								}
								
								break;
								
							case "COMPUTE RANGE SCORE MAX":
								if (!computeRanges.isEmpty()) {
									
									if (content.matches(".*[0-9].*")) {

										double value = Double.parseDouble(content);
										
										for (int i = 0; i<computeRanges.size(); i++) {
											if (i == rangeMaxCount) {
												ComputeRange range2 = computeRanges.get(i);
												range2.setRangeValMax(value);
												computeRanges.set(i, range2);
											}
										}
										rangeMaxCount++;

									} else {
										eval.addError("All values entered for keyword <code>'COMPUTE RANGE SCORE MAX'</code> must be numbers.");
									}
								} else {
									eval.addError("Cannot add value <u>" + content + "</u> for <code>'COMPUTE RANGE SCORE MAX'</code> because no range names were defined. <code>'COMPUTE RANGE NAME' </code>(s) must be defined first.");
								}
								
								break;
								
							case "COMPUTE TOTALS":
								for (int i = 0; i < computeRanges.size(); i++) {
									if(computeRanges.get(i).getRangeValMin() > computeRanges.get(i).getRangeValMax()) {
										eval.addError("<code>'COMPUTE RANGE SCORE MIN'</code> value <u>" + computeRanges.get(i).getRangeValMin() + "</u> must be smaller than <code>'COMPUTE RANGE SCORE MAX'</code> value <u>" + computeRanges.get(i).getRangeValMax() + "</u>.");
									}
								}
								if (rangeMinCount != computeRanges.size()) {
									eval.addError("Incorrect number of values provided for keyword <code>'COMPUTE RANGE SCORE MIN'</code>.");
								}
								if (rangeMaxCount != computeRanges.size()) {
									eval.addError("Incorrect number of values provided for keyword <code>'COMPUTE RANGE SCORE MAX'</code>.");
								}
								
								if (	content.toUpperCase().equals("TRUE") || content.toUpperCase().equals("T") || content.equals("1") ||
										content.toUpperCase().equals("FALSE") || content.toUpperCase().equals("F") || content.equals("0")) {
									
									if (content.toUpperCase().equals("TRUE") || content.toUpperCase().equals("T") || content.equals("1")) {
										eval.setComputeTotals(true);
									}
									
									hasComputeReq = true;
									
								} else {
									eval.addError("Keyword '<u>COMPUTE TOTALS</u>' requires either TRUE or FALSE.");
								}
								break;
								
							case "SECTION":
								eval.getSection(sCount-1).setSecName(content);
								prevElem = "";
								break;
								
							case "SECTION DESCRIPTION":
								eval.getSection(sCount-1).setSecDescription(content);
								prevElem = "";
								break;
								
							case "SECTION TOOL TIP":
								eval.getSection(sCount-1).setSecToolTip(content);
								prevElem = "";
								break;
								
							case "SECTION TOOL TIP VALUE":
								eval.getSection(sCount-1).setSecToolTipVal(content);
								prevElem = "";
								break;
								
							case "SECTION TOOL TIP MARKER":
								
								if (content.equals("DASHED UNDERLINE") ||
									content.equals("SOLID UNDERLINE") ||
									content.equals("INFO ICON")) {
									eval.getSection(sCount-1).setSecToolTipMarker(content);
									
								} else {
									eval.getSection(sCount-1).setSecToolTipMarker("DASHED UNDERLINE");
									eval.addWarning("Section '<u>" + eval.getSection(sCount-1).getSecName() + "</u>' has invalid value for keyword '<code>SECTION TOOL TIP MARKER</code>' and will default to '<code>DASHED UNDERLINE</code>'.");
								}

								prevElem = "";
								break;
							case "QUESTION":
								if (!content.contentEquals("COL")) {
									eval.getSection(sCount-1).addQuestion(new Question());
									questID++;
									qCount++;
									row++;
									eval.getSection(sCount-1).getQuestion(qCount-1).setQText(content);
									eval.getSection(sCount-1).getQuestion(qCount-1).setRow(row);
									eval.getSection(sCount-1).getQuestion(qCount-1).setQuestID(questID);
								}
								break;
								
							case "COL":
								if (col == 1) {
									row++;
								}
								eval.getSection(sCount-1).getQuestion(qCount-1).setCol(col);
								eval.getSection(sCount-1).getQuestion(qCount-1).setRow(row);
								eval.getSection(sCount-1).getQuestion(qCount-1).setQText(content);
								eval.getSection(sCount-1).getQuestion(qCount-1).setQuestID(questID);
								if (preLoad)eval.getSection(sCount-1).getQuestion(qCount-1).setQResponseType("PRE-LOAD");
								prevElem = "";
								break;
								
							case "RADIO BUTTON":
								if (content.contentEquals("BOOLEAN Yes / No")) {
									eval.getSection(sCount-1).getQuestion(qCount-1).addOption("Yes");
									eval.getSection(sCount-1).getQuestion(qCount-1).addOption("No");
								} else {
									eval.getSection(sCount-1).getQuestion(qCount-1).addOption(content);
								}
								break;
								
							case "CHECK BOX":
								eval.getSection(sCount-1).getQuestion(qCount-1).addOption(content);
								break;
								
							case "DROPDOWN":
								eval.addWarning("Additional content '<u>" + content + "</u>' provided for dropdown question '<u>" + eval.getSection(sCount-1).getQuestion(qCount-1).getQText() + "</u>' in section <u>'" + eval.getSection(sCount-1).getSecName() + "'</u> will be ignored. Dropdown options are defined at the top of the document.");
								break;
								
							case "COMPUTE":
								eval.addWarning("Additional content '<u>" + content + "</u>' provided for compute area '<u>" + eval.getSection(sCount-1).getQuestion(qCount-1).getQText() + "</u>' in section <u>'" + eval.getSection(sCount-1).getSecName() + "'</u> will be ignored. Compute options are defined at the top of the document.");
								prevElem = "";
								break;
								
							case "QUESTION DESCRIPTION":
								eval.getSection(sCount-1).getQuestion(qCount-1).setQDescription(content);
								prevElem = "";
								break;
								
							case "QUESTION TOOL TIP":
								eval.getSection(sCount-1).getQuestion(qCount-1).setQToolTip(content);
								prevElem = "";
								break;
								
							case "QUESTION TOOL TIP VALUE":
								eval.getSection(sCount-1).getQuestion(qCount-1).setQToolTipVal(content);
								prevElem = "";
								break;
								
							case "QUESTION TOOL TIP MARKER":
								if (content.equals("DASHED UNDERLINE") ||
									content.equals("SOLID UNDERLINE") ||
									content.equals("INFO ICON")) {
									
									eval.getSection(sCount-1).getQuestion(qCount-1).setQToolTipMarker(content);
										
								} else {
									
									eval.getSection(sCount-1).getQuestion(qCount-1).setQToolTipMarker("DASHED UNDERLINE");
									eval.addWarning("Question '<u>" + eval.getSection(sCount-1).getQuestion(qCount-1).getQText() + "</u>' in Section '<u>" + eval.getSection(sCount-1).getSecName() + "</u>' has no valid value for keyword '<code>SECTION TOOL TIP MARKER</code>' and will default to '<code>DASHED UNDERLINE</code>'.");
								}
								
								prevElem = "";
								break;
								
							default:
								break;
							}
						}
						
						String errorStr = "File type is not defined within the document. Evaluations must have exactly one keyword '<code>FILE TYPE</code>' with value '<code>EVALUATION</code>'.";
						if(!hasFileType && !eval.getErrors().contains(errorStr)) {
							eval.addError(errorStr);
						}
					}
				}
			};

			saxParser.parse(path, handle);

		} catch(Exception ex) {}

		return eval;
	}
}
package edu.sru.group3.WebBasedEvaluations.evalform;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.aspose.cells.BackgroundType;
import com.aspose.cells.BorderType;
import com.aspose.cells.CellBorderType;
import com.aspose.cells.Cells;
import com.aspose.cells.CellsHelper;
import com.aspose.cells.Color;
import com.aspose.cells.Font;
import com.aspose.cells.Style;
import com.aspose.cells.TextAlignmentType;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;

/**
 * Contains methods for generating an Excel file using Aspose.Cells based on a set of completed Evaluations.
 * 
 * @author Logan Racer
 */
public class GenerateEvalReport {
	
	/**
	 * Generate an evaluation Summary/Analysis Excel file (.xlsx) using Aspose.Cells from
	 * a set of completed Evaluations of a given Evaluation ID.
	 * 
	 * @param evalTemp - Blank Evaluation which was assigned to the group.
	 * @param completedEvals - List of completed Evaluations.
	 * @param filePath - String containing the path of where the excel file should be saved.
	 * @param fileName - String containing the desired name of the excel file. (must have .xlsx file extension)
	 * @throws Exception
	 */
	public static void generateReport(Evaluation evalTemp, List<Evaluation> completedEvals, String filePath, String fileName) throws Exception {
		
		// Create the workbook
		Workbook wb = new Workbook();
		
		// Load question headings from eval template
		String[] setupData = loadQuestions(wb, evalTemp).split("~");
		int preloadCols = Integer.parseInt(setupData[0]);
		int dataCols = Integer.parseInt(setupData[1]);
		//int totalRecords = completedEvals.size();
		
		//Average all evaluations for the same person
		List<Evaluation> averagedRecords = new ArrayList<Evaluation>();
		averagedRecords = averageScoresPerReviewee(completedEvals);
		int totalRecords = averagedRecords.size();
		
		// Add records to the file
		int startRow = 3;
		for (int i = 0; i < averagedRecords.size(); i++) {
			addRecord(wb, averagedRecords.get(i), startRow);
			startRow++;
		}
		
		// Add totals row
		if (evalTemp.getComputeTotals()) {
			addTotals(wb, preloadCols, dataCols, totalRecords);
		}

		// Save the file
		wb.save(filePath + fileName);
	}
	
	
	
	
	/**
	 * Formats all data columns in the analysis excel file. Data is retrieved from the Evaluation template object.
	 * 
	 * @param wb - Aspose.Cells excel Workbook object
	 * @param eval - Blank Evaluation template
	 * @return setupData - Array of setup data which is used to format the remainder of the Evaluations.
	 * @throws Exception
	 */
	private static String loadQuestions(Workbook wb, Evaluation eval) throws Exception {
		
		Worksheet sheet = wb.getWorksheets().get(0);
		Cells cells = sheet.getCells();

		//Get preload section, section 0
		Section preLoadSec = eval.getSection(0);

		int preLoadWidth = 0;

		//Store all preload questions in row 3
		for (int i = 0; i<preLoadSec.getQuestionCount(); i++) {
			String preLoadQuest = preLoadSec.getQuestion(i).getQText();
			cells.get(2,i).setValue(preLoadQuest);
			CellsHelper.getTextWidth((String) cells.get(2,i).getValue(), wb.getDefaultStyle().getFont(), 1);
			cells.setColumnWidth(i, CellsHelper.getTextWidth((String) cells.get(2,i).getValue(), wb.getDefaultStyle().getFont(), 0.45));

			Style style = cells.get(2,i).getStyle();
			style.getFont().setBold(true);
			style.setBorder(BorderType.TOP_BORDER, CellBorderType.THIN, Color.getBlack());
			style.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.THICK, Color.getBlack());
			style.setBorder(BorderType.LEFT_BORDER, CellBorderType.THIN, Color.getBlack());
			style.setBorder(BorderType.RIGHT_BORDER, CellBorderType.THIN, Color.getBlack());
			style.setHorizontalAlignment(TextAlignmentType.CENTER);
			cells.get(2,i).setStyle(style);

			if (i > preLoadWidth) {
				preLoadWidth = i;
			}
		}

		// Set merge cells for title cell
		cells.merge(0, 0, 2, preLoadWidth+1);

		// Set title cell
		cells.get(0, 0).setValue(eval.getTitle());
		cells.get(0, 0).getMergedRange().setOutlineBorders(CellBorderType.THIN, Color.getBlack());
		Style style = cells.get(0, 0).getStyle();
		Font font = style.getFont();
		font.setBold(true);
		font.setSize(20);
		cells.get(0,0).setStyle(style);

		int secCount = eval.getSectionCount();

		int count = 0;

		int colIndex = preLoadWidth + 1;
		int secStart = preLoadWidth + 1;

		while (count < secCount) {
			Section sec = eval.getSection(count);
			if (sec.getQuestionCount() > 0) {

				// Add questions of type "DROPDOWN" to excel sheet
				for (int i = 0; i<sec.getQuestionCount(); i++) {
					if (sec.getQuestion(i).getQResponseType().equals("DROPDOWN")) {

						String qStr = sec.getQuestion(i).getQText();

						if (qStr.contains("<span")) {

							String subStr = qStr.substring(0, qStr.indexOf(">") + 1);

							qStr = qStr.replace(subStr, "");

							subStr = qStr.substring(qStr.indexOf("<"), qStr.indexOf(">") + 1);

							qStr = qStr.replace(subStr, "");

						}

						cells.get(2, colIndex).setValue(qStr);

						style = cells.get(2, colIndex).getStyle();
						style.setRotationAngle(90);
						style.setBorder(BorderType.TOP_BORDER, CellBorderType.THIN, Color.getBlack());
						style.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.THICK, Color.getBlack());
						style.setBorder(BorderType.LEFT_BORDER, CellBorderType.THIN, Color.getBlack());
						style.setBorder(BorderType.RIGHT_BORDER, CellBorderType.THIN, Color.getBlack());

						font = style.getFont();
						font.setBold(true);

						cells.get(2, colIndex).setStyle(style);
						cells.setColumnWidth(colIndex, 4);

						colIndex++;

					}
					else if (sec.getQuestion(i).getQResponseType().equals("COMPUTE")) {

						String name = sec.getSecName();

						if (name.contains("OVERALL:")) {
							name = name.replace("OVERALL: ", "");

							Section parent = eval.getSectionByName(name);

							if (parent.hasDropdownQuestions()) {

								// Set section compute column

								cells.get(2, colIndex).setValue(sec.getSecName());

								style = cells.get(2, colIndex).getStyle();

								style.setPattern(BackgroundType.SOLID);
								style.setForegroundColor(Color.getYellow());
								style.setRotationAngle(90);
								style.setBorder(BorderType.TOP_BORDER, CellBorderType.THIN, Color.getBlack());
								style.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.THICK, Color.getBlack());
								style.setBorder(BorderType.LEFT_BORDER, CellBorderType.THIN, Color.getBlack());
								style.setBorder(BorderType.RIGHT_BORDER, CellBorderType.THIN, Color.getBlack());


								font = style.getFont();
								font.setBold(true);

								cells.get(2, colIndex).setStyle(style);
								cells.setColumnWidth(colIndex, 4);

								//Set section heading

								cells.merge(1, secStart, 1, colIndex - secStart + 1);

								cells.get(1, secStart).setValue(parent.getSecName());
								style = cells.get(1, secStart).getStyle();


								cells.get(1, secStart).getMergedRange().setOutlineBorders(CellBorderType.THIN, Color.getBlack());
								style.setBorder(BorderType.TOP_BORDER, CellBorderType.THIN, Color.getBlack());
								style.setBorder(BorderType.LEFT_BORDER, CellBorderType.THIN, Color.getBlack());
								style.setHorizontalAlignment(TextAlignmentType.CENTER);

								font = style.getFont();
								font.setBold(true);

								cells.get(1, secStart).setStyle(style);

								colIndex++;
								secStart = colIndex;
							}
						}
					}
				}
			}
			count++;
		}

		// Insert Evaluation Totals column
		cells.get(2, colIndex).setValue("Overall Evaluation Status");

		style = cells.get(2, colIndex).getStyle();

		style.setPattern(BackgroundType.SOLID);
		style.setForegroundColor(Color.getDarkGoldenrod());
		style.setRotationAngle(90);
		style.setBorder(BorderType.TOP_BORDER, CellBorderType.THIN, Color.getBlack());
		style.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.THICK, Color.getBlack());
		style.setBorder(BorderType.LEFT_BORDER, CellBorderType.THIN, Color.getBlack());
		style.setBorder(BorderType.RIGHT_BORDER, CellBorderType.THIN, Color.getBlack());

		font = style.getFont();
		font.setBold(true);

		cells.get(2, colIndex).setStyle(style);
		cells.setColumnWidth(colIndex, 4);

		// Insert Overall Rating column
		cells.get(2, colIndex+1).setValue("Overall Rating");
		style = cells.get(2, colIndex+1).getStyle();
		style.setBorder(BorderType.TOP_BORDER, CellBorderType.THIN, Color.getBlack());
		style.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.THICK, Color.getBlack());
		style.setBorder(BorderType.LEFT_BORDER, CellBorderType.THIN, Color.getBlack());
		style.setBorder(BorderType.RIGHT_BORDER, CellBorderType.THIN, Color.getBlack());
		style.setHorizontalAlignment(TextAlignmentType.CENTER);

		font = style.getFont();
		font.setBold(true);
		cells.get(2, colIndex+1).setStyle(style);
		cells.setColumnWidth(colIndex+1, 25);
		
		preLoadWidth++;
		int numOfDataCols = (colIndex + 1) - preLoadWidth;
		
		return preLoadWidth + "~" + numOfDataCols;
	}

	
	
	/**
	 * Takes Questions and Scores from each completed Evaluation and computes the
	 * averages for each reviewee.
	 * 
	 * @param allCompleteEvals - List of all completed Evaluation objects
	 * @return averagedEvaluations - List of Evaluations containing averaged scores. One Evaluation for each reviewee.
	 */
	private static List<Evaluation> averageScoresPerReviewee(List<Evaluation> allCompleteEvals) {
		
		List<String> revieweeEmailList = new ArrayList<String>();
		List<Evaluation> averagedRecords = new ArrayList<Evaluation>();
		
		// For all completed evaluations
		for (int i = 0; i < allCompleteEvals.size(); i++) {
			Evaluation completedEval = allCompleteEvals.get(i);
			String email = "";
			Section preload = completedEval.getSection(0);

			// Get the email of the reviewee
			for (int j = 0; j < preload.getQuestionCount(); j++) {
				if (preload.getQuestion(j).getQText().equals("EMAIL ADDRESS")) {
					email = preload.getQuestion(j).getQResponse();
				}
			}
			
			//Check to see if that user has a record in averagedRecord
			if (revieweeEmailList.contains(email)) {
				
				// Find that reviewee's record and add responses to it
				Evaluation existingRecord = null;
				for (int m = 0; m < averagedRecords.size(); m++) {
					Section recordPreload = averagedRecords.get(m).getSection(0);
					for (int n = 0; n < recordPreload.getQuestionCount(); n++) {
						if (recordPreload.getQuestion(n).getQText().equals("EMAIL ADDRESS") && recordPreload.getQuestion(n).getQResponse().equals(email)) {
							existingRecord = averagedRecords.get(m);
							averagedRecords.remove(m);
						}
					}
				}
				
				if (existingRecord == null) {
					System.out.println("Something went wrong.");
				}
				
				// Add responses to results eval
				for (int o = 0; o < existingRecord.getSectionCount(); o++) {
					for (int p = 0; p < existingRecord.getSection(o).getQuestionCount(); p++) {
						if (existingRecord.getSection(o).getQuestion(p).getQResponseType().equals("DROPDOWN")) {
							existingRecord.getSection(o).getQuestion(p).addResponse(completedEval.getQuestionById(existingRecord.getSection(o).getQuestion(p).getQuestID()).getQResponse());
							
						} else if (existingRecord.getSection(o).getQuestion(p).getQResponseType().equals("COMPUTE")) {
							existingRecord.getSection(o).getQuestion(p).addResponse(completedEval.getQuestionById(existingRecord.getSection(o).getQuestion(p).getQuestID()).getQResponse());
						}
					}
				}
				averagedRecords.add(existingRecord);
				
			} else {
				
				revieweeEmailList.add(email);
				
				// Create a new evaluation to hold the averages
				Evaluation averagedRecord = new Evaluation();
				
				// Copy the same preload section and compute information
				averagedRecord.addSection(preload);
				averagedRecord.setComputeRanges(completedEval.getComputeRanges());
				averagedRecord.setComputeTotals(completedEval.getComputeTotals());
				
				for (int k = 0; k < completedEval.getSectionCount(); k++) {
					//Skip preload section
					if (k != 0) {
						
						Section newSection = new Section();
						newSection.setSecName(completedEval.getSection(k).getSecName());
						
						// Add responses to results eval
						for (int l = 0; l < completedEval.getSection(k).getQuestionCount(); l++) {
							Question newQuestion = new Question();
							
							if (completedEval.getSection(k).getQuestion(l).getQResponseType().equals("DROPDOWN")) {
								newQuestion.setQText(completedEval.getSection(k).getQuestion(l).getQText());
								newQuestion.setQResponseType("DROPDOWN");
								newQuestion.addResponse(completedEval.getSection(k).getQuestion(l).getQResponse());
								newQuestion.setQuestID(completedEval.getSection(k).getQuestion(l).getQuestID());
								newSection.addQuestion(newQuestion);
								
							} else if (completedEval.getSection(k).getQuestion(l).getQResponseType().equals("COMPUTE")) {
								newQuestion.setQText(completedEval.getSection(k).getQuestion(l).getQText());
								newQuestion.setQResponseType("COMPUTE");
								newQuestion.addResponse(completedEval.getSection(k).getQuestion(l).getQResponse());
								newQuestion.setQuestID(completedEval.getSection(k).getQuestion(l).getQuestID());
								newSection.addQuestion(newQuestion);
							}
						}
						averagedRecord.addSection(newSection);
					}
				}
				averagedRecords.add(averagedRecord);
			}
		}
		
		// Once all scores are stored in one evaluation for each reviewee, compute the average for each reviewee
		for (int i = 0; i < averagedRecords.size(); i++) {
			for (int j = 0; j < averagedRecords.get(i).getSectionCount(); j++) {
				for (int k = 0; k < averagedRecords.get(i).getSection(j).getQuestionCount(); k++) {
					
					Question quest = averagedRecords.get(i).getSection(j).getQuestion(k);
					
					if(!quest.getQResponseType().equals("PRE-LOAD")) {
						List<String> responses = quest.getResponses();
						double score = 0.0;
						int skipCount = 0;
						
						if (quest.getQResponseType().equals("DROPDOWN")) {
							
							for (int l = 0; l<responses.size(); l++) {
								
								double value = 0.0;
								if (responses.get(l).matches(".*[0-9].*")) {
									value = Double.parseDouble(responses.get(l).replaceAll("[^\\d.]", ""));
								} else {
									skipCount++;
								}
								score += value;
							}
							
						} else if (quest.getQResponseType().equals("COMPUTE")) {
							
							for (int l = 0; l<responses.size(); l++) {
								
								double value = 0.0;
								if (responses.get(l).matches(".*[0-9].*")) {
									String response = responses.get(l); 
									response = StringUtils.splitByWholeSeparator(response, "Grade:", 2)[1];
									value = Double.parseDouble(response.replaceAll("[^\\d.]", ""));
									if (value == 0.0) {
										skipCount++;
									}
								} else {
									skipCount++;
								}
								score += value;
							}
						}
						
						if (responses.size() > 0 && responses.size() - skipCount > 0) {
							score = score / (responses.size() - skipCount);
							quest.setQResponse(String.valueOf(score));
						} else {
							quest.setQResponse("N/A");
						}
					}
				}
			}
		}
		return averagedRecords;
	}
	
	
	
	/**
	 * Adds one reviewees averaged scores to the evaluation analysis excel file.
	 * 
	 * @param wb - Aspose.Cells excel Workbook object
	 * @param eval - Evaluation object containing the averaged scores for one reviewees completed Evaluations. 
	 * @param rowIndex - Int representing the index of which row the record should be placed in the excel file.
	 * @throws Exception
	 */
	private static void addRecord(Workbook wb, Evaluation eval, int rowIndex) throws Exception {
		
		Worksheet sheet = wb.getWorksheets().get(0);
		Cells cells = sheet.getCells();
		Style style = cells.get(0, 0).getStyle();
		double totals = 0;
		double overallStatus = 0;
		int numOfScoreSec = 0;
		int preLoadWidth = 0;
		
		// Assign colors to use in the analysis file
		final Color COLOR_BAD_SCORE = Color.getPaleVioletRed();
		final Color COLOR_GOOD_SCORE = Color.getLightGreen();
		final Color COLOR_SECT_SCORE = Color.getYellow();
		final Color COLOR_BORDER = Color.getBlack();
		final Color COLOR_OVERALL_STATUS = Color.getDarkGoldenrod();
		
		// Preload section ==================================
		Section preLoadSec = eval.getSection(0);
		
		for (int i = 0; i<preLoadSec.getQuestionCount(); i++) {
			cells.get(rowIndex,i).setValue(preLoadSec.getQuestion(i).getQResponse());
			
			double currentWidth = cells.getColumnWidth(i);
			double newWidth = CellsHelper.getTextWidth((String) cells.get(rowIndex, i).getValue(), wb.getDefaultStyle().getFont(), 0.45);
			
			if (currentWidth < newWidth) {
				cells.setColumnWidth(i, newWidth);
			}
			
			style = cells.get(rowIndex,i).getStyle();
			style.setBorder(BorderType.TOP_BORDER, CellBorderType.THIN, COLOR_BORDER);
			style.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, COLOR_BORDER);
			style.setBorder(BorderType.LEFT_BORDER, CellBorderType.THIN, COLOR_BORDER);
			style.setBorder(BorderType.RIGHT_BORDER, CellBorderType.THIN, COLOR_BORDER);
			cells.get(rowIndex,i).setStyle(style);
			
			if (i > preLoadWidth) {
				preLoadWidth = i;
			}
		}
		
		//All other sections ===========================================
		int secCount = eval.getSectionCount();
		int count = 0;
		int colIndex = preLoadWidth + 1;

		while (count < secCount) {
			
			Section sec = eval.getSection(count);
			
			if (sec.getQuestionCount() > 0) {

				for (int i = 0; i < sec.getQuestionCount(); i++) {
					
					// Add questions of type "DROPDOWN" to excel sheet
					if (sec.getQuestion(i).getQResponseType().equals("DROPDOWN")) {
						
						String response = sec.getQuestion(i).getQResponse();
						
						style = cells.get(rowIndex,colIndex).getStyle();
						
						if (response.isBlank() || response.equals("N/A")) {
							
							response = "N/A";
							cells.get(rowIndex, colIndex).setValue(response);
							style.setPattern(BackgroundType.SOLID);
							style.setForegroundColor(COLOR_GOOD_SCORE);
							
						} else {
							
							cells.get(rowIndex, colIndex).setValue(Double.parseDouble(response));
							
							if (Double.parseDouble(response) > 3) {
								style.setPattern(BackgroundType.SOLID);
								style.setForegroundColor(COLOR_GOOD_SCORE);
							} else if (Double.parseDouble(response) < 3) {
								style.setPattern(BackgroundType.SOLID);
								style.setForegroundColor(COLOR_BAD_SCORE);
							}
							
						}
						
						style.setBorder(BorderType.TOP_BORDER, CellBorderType.THIN, COLOR_BORDER);
						style.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, COLOR_BORDER);
						style.setBorder(BorderType.LEFT_BORDER, CellBorderType.THIN, COLOR_BORDER);
						style.setBorder(BorderType.RIGHT_BORDER, CellBorderType.THIN, COLOR_BORDER);

						cells.get(rowIndex,colIndex).setStyle(style);

						colIndex++;
						
					// Add questions of type "COMPUTE" to excel sheet
					} else if (sec.getQuestion(i).getQResponseType().equals("COMPUTE")) {

						String name = sec.getSecName();

						if (name.contains("OVERALL:")) {
							name = name.replace("OVERALL: ", "");

							Section parent = eval.getSectionByName(name);

							if (parent.hasDropdownQuestions()) {

								// Set section compute column
								if(sec.getQuestion(i).getQResponse().matches(".*[0-9].*")) {
									cells.get(rowIndex, colIndex).setValue(Double.parseDouble(sec.getQuestion(i).getQResponse()));
								} else {
									cells.get(rowIndex, colIndex).setValue(sec.getQuestion(i).getQResponse());
								}
								
								
								style = cells.get(rowIndex,colIndex).getStyle();
								
								style.setBorder(BorderType.TOP_BORDER, CellBorderType.THIN, COLOR_BORDER);
								style.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, COLOR_BORDER);
								style.setBorder(BorderType.LEFT_BORDER, CellBorderType.THIN, COLOR_BORDER);
								style.setBorder(BorderType.RIGHT_BORDER, CellBorderType.THIN, COLOR_BORDER);
								
								style.setPattern(BackgroundType.SOLID);
								style.setForegroundColor(COLOR_SECT_SCORE);
								
								cells.get(rowIndex,colIndex).setStyle(style);
								
								if (sec.getQuestion(i).getQResponse().matches(".*[0-9].*")) {
									numOfScoreSec++;
									totals += Double.parseDouble(sec.getQuestion(i).getQResponse());
								}
									
								colIndex++;
							}
						}
					}
				}
			}
			count++;
		}
		
		// Overall evaluation status column ===============================================
		overallStatus = totals / numOfScoreSec;
		
		if (eval.getComputeTotals()) {
			cells.get(rowIndex, colIndex).setValue(overallStatus);
		}

		style = cells.get(rowIndex,colIndex).getStyle();
		
		style.setBorder(BorderType.TOP_BORDER, CellBorderType.THIN, COLOR_BORDER);
		style.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, COLOR_BORDER);
		style.setBorder(BorderType.LEFT_BORDER, CellBorderType.THIN, COLOR_BORDER);
		style.setBorder(BorderType.RIGHT_BORDER, CellBorderType.THIN, COLOR_BORDER);
		
		style.setPattern(BackgroundType.SOLID);
		style.setForegroundColor(COLOR_OVERALL_STATUS);
		
		cells.get(rowIndex,colIndex).setStyle(style);
		
		// Overall rating column ===============================================
		String overallRating = "NO SCORE AVAILABLE";
		List<ComputeRange> computeRanges = eval.getComputeRanges();
				
		for (int i = 0; i < computeRanges.size(); i++) {
			if (overallStatus <= computeRanges.get(i).getRangeValMax() && overallStatus >= computeRanges.get(i).getRangeValMin()) {
				overallRating = computeRanges.get(i).getRangeName();
			}
		}
		
		if (eval.getComputeTotals()) {
			cells.get(rowIndex, colIndex+1).setValue(overallRating);
		}
		
		style = cells.get(rowIndex,colIndex+1).getStyle();
		
		style.setBorder(BorderType.TOP_BORDER, CellBorderType.THIN, COLOR_BORDER);
		style.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, COLOR_BORDER);
		style.setBorder(BorderType.LEFT_BORDER, CellBorderType.THIN, COLOR_BORDER);
		style.setBorder(BorderType.RIGHT_BORDER, CellBorderType.THIN, COLOR_BORDER);
		
		cells.get(rowIndex,colIndex+1).setStyle(style);
		
		double currentWidth = cells.getColumnWidth(colIndex+1);
		double newWidth = CellsHelper.getTextWidth((String) cells.get(rowIndex, colIndex+1).getValue(), wb.getDefaultStyle().getFont(), 0.45);
		
		if (currentWidth < newWidth) {
			cells.setColumnWidth(colIndex+1, newWidth);
		}
		
	}

	/**
	 * Adds the totals columns and rows in the Workbook.
	 * 
	 * @param wb - Aspose.Cells excel Workbook object
	 * @param preloadCols - Int representing the number of columns used for the pre-load information
	 * @param dataCols - Int representing the number of columns used for the question and section scores
	 * @param totalRecords - Int representing the number of rows where reviewee records exist.
	 */
	private static void addTotals(Workbook wb, int preloadCols, int dataCols, int totalRecords) {
		
		Worksheet sheet = wb.getWorksheets().get(0);
		Cells cells = sheet.getCells();
		Style style = cells.get(0, 0).getStyle();
		
		// Assign colors to use in the analysis file
		final Color COLOR_SECT_SCORE = Color.getYellow();
		final Color COLOR_BORDER = Color.getBlack();
		final Color COLOR_OVERALL_STATUS = Color.getDarkGoldenrod();
		final Color COLOR_TOTAL_ROW = Color.getLightSkyBlue();
		
		cells.get(totalRecords + 3, 0).setValue("TOTALS:");
		
		style = cells.get(totalRecords + 3,0).getStyle();
		style.getFont().setBold(true);
		cells.get(totalRecords + 3, 0).setStyle(style);
		
		for(int i = 0; i < preloadCols; i++) {
			style = cells.get(totalRecords + 3, i).getStyle();
			style.setBorder(BorderType.TOP_BORDER, CellBorderType.THICK, COLOR_BORDER);
			style.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, COLOR_BORDER);
			style.setBorder(BorderType.LEFT_BORDER, CellBorderType.THIN, COLOR_BORDER);
			style.setBorder(BorderType.RIGHT_BORDER, CellBorderType.THIN, COLOR_BORDER);
			cells.get(totalRecords + 3, i).setStyle(style);
		}
		
		if (totalRecords>0) {
			for(int i = preloadCols; i < dataCols + preloadCols; i++) {
				
				String firstRecordCellName = cells.get(3, i).getName();
				String lastRecordCellName = cells.get(totalRecords + 2, i).getName();
				String averageFormula = "AVERAGE(" + firstRecordCellName + ":" + lastRecordCellName + ")";

				cells.get(totalRecords + 3, i).setFormula(averageFormula);
				
				style = cells.get(totalRecords + 3,i).getStyle();
				style.setBorder(BorderType.TOP_BORDER, CellBorderType.THICK, COLOR_BORDER);
				style.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, COLOR_BORDER);
				style.setBorder(BorderType.LEFT_BORDER, CellBorderType.THIN, COLOR_BORDER);
				style.setBorder(BorderType.RIGHT_BORDER, CellBorderType.THIN, COLOR_BORDER);
				
				style.setPattern(BackgroundType.SOLID);
				style.setForegroundColor(COLOR_TOTAL_ROW);
				
				cells.get(totalRecords + 3,i).setStyle(style);
			}
		}
		
		// Write color key to the bottom of the  file
		style = cells.get(totalRecords + 5, 0).getStyle();
		style.setPattern(BackgroundType.SOLID);
		style.setForegroundColor(COLOR_SECT_SCORE);
		cells.get(totalRecords + 5, 0).setStyle(style);
		
		style = cells.get(totalRecords + 6, 0).getStyle();
		style.setPattern(BackgroundType.SOLID);
		style.setForegroundColor(COLOR_OVERALL_STATUS);
		cells.get(totalRecords + 6, 0).setStyle(style);
		
		style = cells.get(totalRecords + 7, 0).getStyle();
		style.setPattern(BackgroundType.SOLID);
		style.setForegroundColor(COLOR_TOTAL_ROW);
		cells.get(totalRecords + 7, 0).setStyle(style);
		
		// VALUE
		cells.get(totalRecords + 5, 1).setValue("Average by section");
		cells.get(totalRecords + 6, 1).setValue("Average of all the sections");
		cells.get(totalRecords + 7, 1).setValue("Average by skill");
	}
}
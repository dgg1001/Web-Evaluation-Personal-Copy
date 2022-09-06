package edu.sru.group3.WebBasedEvaluations.evalform;

import java.io.File;
import java.io.FileOutputStream;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 * Contains methods for generating an Excel file using Apache POI based on a set of completed Evaluations.
 * 
 * @author Logan Racer
 */
public class GenerateEvalReportPoi {
	
	// Row indexes
	private final static int TITLE_ROW_INDEX = 0;
	private final static int SEC_HEAD_ROW_INDEX = 1;
	private final static int DATA_HEAD_ROW_INDEX = 2;
	private final static int RECORDS_START_ROW_INDEX = 3;
	
	// widths
	private final static int DATA_COL_WIDTH = 5;
	
	// fonts
	private static XSSFFont fontBold;
	private static XSSFFont fontNormal;
	private static XSSFFont fontTitle;
	
	// colors
	private final static short COLOR_YELLOW = IndexedColors.YELLOW.getIndex();
	private final static short COLOR_ORANGE = IndexedColors.ORANGE.getIndex();
	private final static short COLOR_BLUE = IndexedColors.LIGHT_BLUE.getIndex();
	private final static short COLOR_GREEN = IndexedColors.LIGHT_GREEN.getIndex();
	private final static short COLOR_RED = IndexedColors.ROSE.getIndex();
	
	// styles
	private static CellStyle styleHeadTitle;
	private static CellStyle styleHeadNormal;
	private static CellStyle styleHeadQuestion;
	private static CellStyle styleHeadComputeSec;
	private static CellStyle styleHeadSection;
	private static CellStyle styleHeadTotals;
	private static CellStyle styleData;
	private static CellStyle styleDataNoBorder;
	private static CellStyle styleDataGood;
	private static CellStyle styleDataBad;
	private static CellStyle styleDataCompute;
	private static CellStyle styleDataTotals;
	private static CellStyle styleDataRowTotals;
	private static CellStyle styleDataComputeKey;
	private static CellStyle styleDataTotalsKey;
	private static CellStyle styleDataRowTotalsKey;
	
	
	/**
	 * Generate an evaluation Summary/Analysis Excel file (.xlsx) using Apache POI from
	 * a set of completed Evaluations of a given Evaluation ID.
	 * 
	 * @param evalTemp - Blank Evaluation which was assigned to the group.
	 * @param completedEvals - List of completed Evaluations.
	 * @param filePath - String containing the path of where the excel file should be saved.
	 * @param fileName - String containing the desired name of the excel file. (must have .xlsx file extension)
	 * @throws Exception
	 */
	public static void generateReport(Evaluation evalTemp, List<Evaluation> completedEvals, final String TEMP_FILES_PATH, final String FILE_NAME) throws Exception {
		
		// Create the workbook
		XSSFWorkbook wb = new XSSFWorkbook();
		
		// Initialize the styles
		initializeStyle(wb);
		
		// Load question headings from eval template
		String returnStr = loadQuestions(wb, evalTemp);
		String[] setupData = returnStr.split("~");
		int preloadCols = Integer.parseInt(setupData[0]);
		int dataCols = Integer.parseInt(setupData[1]);

		//Average all evaluations for the same person
		List<Evaluation> averagedRecords = new ArrayList<Evaluation>();
		averagedRecords = averageScoresPerReviewee(completedEvals);
		int totalRecords = averagedRecords.size();
		
		// Add records to the file
		int startRow = RECORDS_START_ROW_INDEX;
		for (int i = 0; i < averagedRecords.size(); i++) {
			addRecord(wb, averagedRecords.get(i), startRow);
			startRow++;
		}
		
		// Add totals row
		if (evalTemp.getComputeTotals()) {
			addTotals(wb, preloadCols, dataCols, totalRecords, evalTemp);
		}

		// Save the file
		try
        {
            FileOutputStream out = new FileOutputStream(new File(TEMP_FILES_PATH, FILE_NAME));
            wb.write(out);
            out.close();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
	}
	
	
	
	/**
	 * Initializes the styles used in the creating the excel sheet.
	 * @param wb - The XFFSWorkbook
	 */
	private static void initializeStyle(XSSFWorkbook wb) {
		// Set fonts
		fontBold = wb.createFont();
		fontBold.setBold(true);
		fontBold.setFontName("Arial");
		fontBold.setFontHeightInPoints((short) 10);
		
		fontNormal = wb.createFont();
		fontNormal.setFontName("Arial");
		fontNormal.setFontHeightInPoints((short) 10);
		
		fontTitle = wb.createFont();
		fontTitle.setFontName("Arial");
		fontTitle.setBold(true);
		fontTitle.setFontHeightInPoints((short) 20);

		// Set styles
		styleHeadTitle = wb.createCellStyle();
		styleHeadTitle.setBorderTop(BorderStyle.THIN);
		styleHeadTitle.setBorderBottom(BorderStyle.THIN);
		styleHeadTitle.setBorderLeft(BorderStyle.THIN);
		styleHeadTitle.setBorderRight(BorderStyle.THIN);
		styleHeadTitle.setFont(fontTitle);
		styleHeadTitle.setAlignment(HorizontalAlignment.CENTER_SELECTION);
		
		styleHeadNormal = wb.createCellStyle();
		styleHeadNormal.setBorderTop(BorderStyle.THIN);
		styleHeadNormal.setBorderBottom(BorderStyle.THICK);
		styleHeadNormal.setBorderLeft(BorderStyle.THIN);
		styleHeadNormal.setBorderRight(BorderStyle.THIN);
		styleHeadNormal.setFont(fontBold);
		styleHeadNormal.setAlignment(HorizontalAlignment.CENTER_SELECTION);
		
		styleHeadQuestion = wb.createCellStyle();
		styleHeadQuestion.setBorderTop(BorderStyle.THIN);
		styleHeadQuestion.setBorderBottom(BorderStyle.THICK);
		styleHeadQuestion.setBorderLeft(BorderStyle.THIN);
		styleHeadQuestion.setBorderRight(BorderStyle.THIN);
		styleHeadQuestion.setRotation((short) 90);
		styleHeadQuestion.setFont(fontBold);
		styleHeadQuestion.setAlignment(HorizontalAlignment.CENTER_SELECTION);
		
		styleHeadComputeSec = wb.createCellStyle();
		styleHeadComputeSec.setBorderTop(BorderStyle.THIN);
		styleHeadComputeSec.setBorderBottom(BorderStyle.THICK);
		styleHeadComputeSec.setBorderLeft(BorderStyle.THIN);
		styleHeadComputeSec.setBorderRight(BorderStyle.THIN);
		styleHeadComputeSec.setRotation((short) 90);
		styleHeadComputeSec.setFillForegroundColor(COLOR_YELLOW);
		styleHeadComputeSec.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleHeadComputeSec.setFont(fontBold);
		styleHeadComputeSec.setAlignment(HorizontalAlignment.CENTER_SELECTION);
		
		styleHeadSection = wb.createCellStyle();
		styleHeadSection.setBorderTop(BorderStyle.THIN);
		styleHeadSection.setBorderBottom(BorderStyle.THIN);
		styleHeadSection.setBorderLeft(BorderStyle.THIN);
		styleHeadSection.setBorderRight(BorderStyle.THIN);
		styleHeadSection.setFont(fontBold);
		styleHeadSection.setAlignment(HorizontalAlignment.CENTER_SELECTION);
		
		styleHeadTotals = wb.createCellStyle();
		styleHeadTotals.setBorderTop(BorderStyle.THIN);
		styleHeadTotals.setBorderBottom(BorderStyle.THICK);
		styleHeadTotals.setBorderLeft(BorderStyle.THIN);
		styleHeadTotals.setBorderRight(BorderStyle.THIN);
		styleHeadTotals.setRotation((short) 90);
		styleHeadTotals.setFillForegroundColor(COLOR_ORANGE);
		styleHeadTotals.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleHeadTotals.setFont(fontBold);
		styleHeadTotals.setAlignment(HorizontalAlignment.CENTER_SELECTION);
		
		styleData = wb.createCellStyle();
		styleData.setBorderTop(BorderStyle.THIN);
		styleData.setBorderBottom(BorderStyle.THIN);
		styleData.setBorderLeft(BorderStyle.THIN);
		styleData.setBorderRight(BorderStyle.THIN);
		styleData.setFont(fontNormal);
		styleData.setAlignment(HorizontalAlignment.CENTER_SELECTION);
		
		styleDataNoBorder = wb.createCellStyle();
		styleDataNoBorder.setFont(fontNormal);
		styleDataNoBorder.setAlignment(HorizontalAlignment.LEFT);
		
		styleDataGood = wb.createCellStyle();
		styleDataGood.setBorderTop(BorderStyle.THIN);
		styleDataGood.setBorderBottom(BorderStyle.THIN);
		styleDataGood.setBorderLeft(BorderStyle.THIN);
		styleDataGood.setBorderRight(BorderStyle.THIN);
		styleDataGood.setFont(fontNormal);
		styleDataGood.setAlignment(HorizontalAlignment.CENTER_SELECTION);
		styleDataGood.setFillForegroundColor(COLOR_GREEN);
		styleDataGood.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		styleDataBad = wb.createCellStyle();
		styleDataBad.setBorderTop(BorderStyle.THIN);
		styleDataBad.setBorderBottom(BorderStyle.THIN);
		styleDataBad.setBorderLeft(BorderStyle.THIN);
		styleDataBad.setBorderRight(BorderStyle.THIN);
		styleDataBad.setFont(fontNormal);
		styleDataBad.setAlignment(HorizontalAlignment.CENTER_SELECTION);
		styleDataBad.setFillForegroundColor(COLOR_RED);
		styleDataBad.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		styleDataCompute = wb.createCellStyle();
		styleDataCompute.setBorderTop(BorderStyle.THIN);
		styleDataCompute.setBorderBottom(BorderStyle.THIN);
		styleDataCompute.setBorderLeft(BorderStyle.THIN);
		styleDataCompute.setBorderRight(BorderStyle.THIN);
		styleDataCompute.setFont(fontNormal);
		styleDataCompute.setAlignment(HorizontalAlignment.CENTER_SELECTION);
		styleDataCompute.setFillForegroundColor(COLOR_YELLOW);
		styleDataCompute.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		styleDataTotals = wb.createCellStyle();
		styleDataTotals.setBorderTop(BorderStyle.THIN);
		styleDataTotals.setBorderBottom(BorderStyle.THIN);
		styleDataTotals.setBorderLeft(BorderStyle.THIN);
		styleDataTotals.setBorderRight(BorderStyle.THIN);
		styleDataTotals.setFont(fontNormal);
		styleDataTotals.setAlignment(HorizontalAlignment.CENTER_SELECTION);
		styleDataTotals.setFillForegroundColor(COLOR_ORANGE);
		styleDataTotals.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		styleDataRowTotals = wb.createCellStyle();
		styleDataRowTotals.setBorderTop(BorderStyle.THICK);
		styleDataRowTotals.setBorderBottom(BorderStyle.THIN);
		styleDataRowTotals.setBorderLeft(BorderStyle.THIN);
		styleDataRowTotals.setBorderRight(BorderStyle.THIN);
		styleDataRowTotals.setFont(fontNormal);
		styleDataRowTotals.setAlignment(HorizontalAlignment.CENTER_SELECTION);
		styleDataRowTotals.setFillForegroundColor(COLOR_BLUE);
		styleDataRowTotals.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		//Key
		styleDataComputeKey = wb.createCellStyle();
		styleDataComputeKey.setFont(fontNormal);
		styleDataComputeKey.setFillForegroundColor(COLOR_YELLOW);
		styleDataComputeKey.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleDataComputeKey.setAlignment(HorizontalAlignment.LEFT);
		
		styleDataTotalsKey = wb.createCellStyle();
		styleDataTotalsKey.setFont(fontNormal);
		styleDataTotalsKey.setFillForegroundColor(COLOR_ORANGE);
		styleDataTotalsKey.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleDataTotalsKey.setAlignment(HorizontalAlignment.LEFT);
		
		styleDataRowTotalsKey = wb.createCellStyle();
		styleDataRowTotalsKey.setFont(fontNormal);
		styleDataRowTotalsKey.setFillForegroundColor(COLOR_BLUE);
		styleDataRowTotalsKey.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleDataRowTotalsKey.setAlignment(HorizontalAlignment.LEFT);
		
	}
	
	
	
	/**
	 * Remove SPAN HTML tags from the string.
	 * 
	 * @param str - The string to process
	 * @return str - The string with formatting removed
	 */
	private static String dropToolTipFormatting(String str) {
		
		if (str.contains("<span")) {
			String subStr = str.substring(0, str.indexOf(">") + 1);
			str = str.replace(subStr, "");
			subStr = str.substring(str.indexOf("<"), str.indexOf(">") + 1);
			str = str.replace(subStr, "");
		}
		
		return str;
	}
	
	
	
	/**
	 * Formats all data columns in the analysis excel file. Data is retrieved from the Evaluation template object.
	 * 
	 * @param wb - Apache POI XSSFWorkbook object
	 * @param eval - Blank Evaluation template
	 * @return setupData - Array of setup data which is used to format the remainder of the Evaluations.
	 * @throws Exception
	 */
	private static String loadQuestions(XSSFWorkbook wb, Evaluation eval) throws Exception {
		
		XSSFSheet sheet = wb.createSheet();
		XSSFRow titleRow = sheet.createRow(TITLE_ROW_INDEX);
		XSSFRow secHeadRow = sheet.createRow(SEC_HEAD_ROW_INDEX);
		XSSFRow dataHeadRow = sheet.createRow(DATA_HEAD_ROW_INDEX);
		
		XSSFCell titleCell = titleRow.createCell(0);
		XSSFCell cell = titleRow.createCell(0);
		
		// Preload Section =================================================================
		Section preLoadSec = eval.getSection(0);
		int preLoadWidth = 0;
		
		for (int i = 0; i<preLoadSec.getQuestionCount(); i++) {
			cell = dataHeadRow.createCell(i);
			cell.setCellValue(dropToolTipFormatting(preLoadSec.getQuestion(i).getQText()));
			cell.setCellStyle(styleHeadNormal);
			sheet.autoSizeColumn(i);
			
			cell = titleRow.createCell(i);
			cell.setCellStyle(styleHeadTitle);
			cell = secHeadRow.createCell(i);
			cell.setCellStyle(styleHeadTitle);
			preLoadWidth++;
		}
		
		// title ===========================================================================
			titleCell = titleRow.getCell(0);
			titleCell.setCellValue(eval.getTitle());
			CellRangeAddress titleRange = new CellRangeAddress(TITLE_ROW_INDEX, SEC_HEAD_ROW_INDEX, 0, preLoadWidth-1);
			sheet.addMergedRegion(titleRange);
			titleCell.setCellStyle(styleHeadTitle);
		
		// Other Sections ===================================================================
		List <Section> sections = new ArrayList <> ();
		sections = eval.getSections();
		sections.remove(0);
		
		int numOfDataCols = preLoadWidth;
		
		for (int i = 0; i < sections.size(); i++) {
			
			if (!sections.get(i).getSecName().startsWith("OVERALL:")) {
	
				int secStartColIndex = numOfDataCols;
				if (sections.get(i).hasDropdownQuestions()) {
					for (int j = 0; j < sections.get(i).getQuestionCount(); j++) {
						if (sections.get(i).getQuestion(j).getQResponseType().equals("DROPDOWN")) {
							// Question columns
							cell = dataHeadRow.createCell(numOfDataCols);
							cell.setCellValue(dropToolTipFormatting(sections.get(i).getQuestion(j).getQText()));
							cell.setCellStyle(styleHeadQuestion);
							sheet.setColumnWidth(numOfDataCols, DATA_COL_WIDTH * 256);
							
							// setting borders for section headings
							cell = secHeadRow.createCell(numOfDataCols);
							cell.setCellStyle(styleHeadSection);
							numOfDataCols++;
						}
					}
					// Section headings
					cell = secHeadRow.getCell(secStartColIndex);
					cell.setCellValue(dropToolTipFormatting(sections.get(i).getSecName()));
					cell.setCellStyle(styleHeadSection);
					CellRangeAddress range = new CellRangeAddress(SEC_HEAD_ROW_INDEX, SEC_HEAD_ROW_INDEX, secStartColIndex, numOfDataCols);
					sheet.addMergedRegion(range);
				}
				
			} else {
				// Compute section columns
				cell = dataHeadRow.createCell(numOfDataCols);
				cell.setCellValue(dropToolTipFormatting(sections.get(i).getSecName()));
				cell.setCellStyle(styleHeadComputeSec);
				sheet.setColumnWidth(numOfDataCols, DATA_COL_WIDTH * 256);
				
				// setting borders for section heading
				cell = secHeadRow.createCell(numOfDataCols);
				cell.setCellStyle(styleHeadSection);
				numOfDataCols++;
			}
		}
		
		// Insert Evaluation Totals column
		cell = dataHeadRow.createCell(numOfDataCols);
		cell.setCellValue("Overall Evaluation Status");
		cell.setCellStyle(styleHeadTotals);
		sheet.setColumnWidth(numOfDataCols, DATA_COL_WIDTH * 256);
		
		// Insert Overall Rating column
		cell = dataHeadRow.createCell(numOfDataCols + 1);
		cell.setCellValue("Overall Rating");
		cell.setCellStyle(styleHeadNormal);
		sheet.autoSizeColumn(numOfDataCols + 1);
		
		return preLoadWidth + "~" + (numOfDataCols + 1);
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
								newQuestion.setOptions(completedEval.getSection(k).getQuestion(l).getOptions());
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
	 * @param wb - Apache POI XSSFWorkbook object
	 * @param eval - Evaluation object containing the averaged scores for one reviewees completed Evaluations. 
	 * @param rowIndex - Int representing the index of which row the record should be placed in the excel file.
	 * @throws Exception
	 */
	private static void addRecord(XSSFWorkbook wb, Evaluation eval, int rowIndex) throws Exception {
		XSSFSheet sheet = wb.getSheetAt(0);
		XSSFRow row = sheet.createRow(rowIndex);
		XSSFCell cell = null;
		
		double totals = 0;
		double overallStatus = 0;
		int numOfScoreSec = 0;
		int preLoadWidth = 0;
		
		// Preload Section =================================================================
		Section preLoadSec = eval.getSection(0);
		
		for (int i = 0; i<preLoadSec.getQuestionCount(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(dropToolTipFormatting(preLoadSec.getQuestion(i).getQResponse()));
			cell.setCellStyle(styleData);
			sheet.autoSizeColumn(i);
			preLoadWidth++;
		}
		
		// Other Sections ===================================================================
		List <Section> sections = new ArrayList <> ();
		sections = eval.getSections();
		sections.remove(0);
		
		int numOfDataCols = preLoadWidth;
		
		for (int i = 0; i < sections.size(); i++) {
			if (!sections.get(i).getSecName().startsWith("OVERALL:")) {
				if (sections.get(i).hasDropdownQuestions()) {
					for (int j = 0; j < sections.get(i).getQuestionCount(); j++) {
						if (sections.get(i).getQuestion(j).getQResponseType().equals("DROPDOWN")) {
							// Question scores
							cell = row.createCell(numOfDataCols);
							if(sections.get(i).getQuestion(j).getQResponse().matches(".*[0-9].*")) {
								Double score = Double.parseDouble(sections.get(i).getQuestion(j).getQResponse());
								int max = sections.get(i).getQuestion(j).getQuestionMaxPoints();
								cell.setCellValue(score);
																
								// Determine score color formatting
								if (score > (max * 0.61)) {
									cell.setCellStyle(styleDataGood);
								} else if (score < (max * 0.41)) {
									cell.setCellStyle(styleDataBad);
								} else {
									cell.setCellStyle(styleData);
								}
							} else {
								cell.setCellValue(sections.get(i).getQuestion(j).getQResponse());
								cell.setCellStyle(styleDataGood);
							}
							numOfDataCols++;
						}
					}
				}
			} else {
				// Compute section scores
				cell = row.createCell(numOfDataCols);
				cell.setCellStyle(styleDataCompute);
				if(sections.get(i).getQuestion(0).getQResponse().matches(".*[0-9].*")) {
					Double score = Double.parseDouble(sections.get(i).getQuestion(0).getQResponse());
					cell.setCellValue(score);
					totals += score;
					numOfScoreSec++;
				} else {
					cell.setCellValue(sections.get(i).getQuestion(0).getQResponse());
				}
				numOfDataCols++;
			}
		}
		if (eval.getComputeTotals()) {
			// Evaluation Totals column
			overallStatus = totals / numOfScoreSec;
			cell = row.createCell(numOfDataCols);
			cell.setCellValue(overallStatus);
			cell.setCellStyle(styleDataTotals);
			
			// Overall Rating column
			String overallRating = "NO SCORE AVAILABLE";
			List<ComputeRange> computeRanges = eval.getComputeRanges();
			
			for (int i = 0; i < computeRanges.size(); i++) {
				if (overallStatus <= computeRanges.get(i).getRangeValMax() && overallStatus >= computeRanges.get(i).getRangeValMin()) {
					overallRating = computeRanges.get(i).getRangeName();
				}
			}
			
			cell = row.createCell(numOfDataCols + 1);
			cell.setCellValue(overallRating);
			cell.setCellStyle(styleData);
			sheet.autoSizeColumn(numOfDataCols + 1);
		}
	}

	
	
	/**
	 * Adds the totals columns and rows in the Workbook.
	 * 
	 * @param wb - Apache POI XSSFWorkbook object
	 * @param preloadCols - Int representing the number of columns used for the pre-load information
	 * @param dataCols - Int representing the number of columns used for the question and section scores
	 * @param totalRecords - Int representing the number of rows where reviewee records exist.
	 * @param eval - Evaluation template used to get compute range information
	 */
	private static void addTotals(XSSFWorkbook wb, int preloadCols, int dataCols, int totalRecords, Evaluation eval) {
		XSSFSheet sheet = wb.getSheetAt(0);
		XSSFRow totalsRow = sheet.createRow(totalRecords + RECORDS_START_ROW_INDEX);
		XSSFRow firstDataRow = null;
		XSSFRow lastDataRow = null;
		XSSFCell cell = null;
		double overallStatus = 0.0;
		
		// Totals
		cell = totalsRow.createCell(preloadCols - 1);
		cell.setCellValue("TOTALS:");
		cell.setCellStyle(styleDataRowTotals);
		
		int dataColCount = preloadCols;
		if (totalRecords > 0) {
			firstDataRow = sheet.getRow(RECORDS_START_ROW_INDEX);
			lastDataRow = sheet.getRow(totalRecords + RECORDS_START_ROW_INDEX - 1);
			
			// Cell formulas for totals
			while (dataColCount < dataCols) {
				
				// Get range of cells to calculate
				
				// top cell
				String firstCellName = firstDataRow.getCell(dataColCount).getReference();
				
				// bottom cell
				String lastCellName = lastDataRow.getCell(dataColCount).getReference();
				
				// formula
				String formula = "AVERAGE(" + firstCellName + ":" + lastCellName + ")";
				
				cell = totalsRow.createCell(dataColCount);
				//cell.setCellValue(formula);
				cell.setCellFormula(formula);
				cell.setCellStyle(styleDataRowTotals);
				
				dataColCount++;
			}
			
			cell = totalsRow.getCell(dataColCount - 1);
			FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator(); 
			overallStatus = evaluator.evaluate(cell).getNumberValue();
			
			String overallRating = "NO SCORE AVAILABLE";
			List<ComputeRange> computeRanges = eval.getComputeRanges();
			
			for (int i = 0; i < computeRanges.size(); i++) {
				if (overallStatus <= computeRanges.get(i).getRangeValMax() && overallStatus >= computeRanges.get(i).getRangeValMin()) {
					overallRating = computeRanges.get(i).getRangeName();
				}
			}
			
			cell = totalsRow.createCell(dataColCount);
			cell.setCellValue(overallRating);
			cell.setCellStyle(styleDataRowTotals);
			
			
		} else {
			while (dataColCount < dataCols) {

				cell = totalsRow.createCell(dataColCount);
				cell.setCellValue("N/A");
				cell.setCellStyle(styleDataRowTotals);
				dataColCount++;
			}
			
			cell = totalsRow.createCell(dataColCount);
			cell.setCellValue("NO SCORE AVAILABLE");
			cell.setCellStyle(styleDataRowTotals);
			sheet.autoSizeColumn(dataColCount);
		}
		
		// Write color key to the bottom of the  file
		XSSFRow keyRow1 = sheet.createRow(totalRecords + RECORDS_START_ROW_INDEX + 2);
		XSSFRow keyRow2 = sheet.createRow(totalRecords + RECORDS_START_ROW_INDEX + 3);
		XSSFRow keyRow3 = sheet.createRow(totalRecords + RECORDS_START_ROW_INDEX + 4);
		
		// Average by section
		cell = keyRow1.createCell(0);
		cell.setCellStyle(styleDataComputeKey);
		
		cell = keyRow1.createCell(1);
		cell.setCellValue("Average by section");
		cell.setCellStyle(styleDataNoBorder);
		
		
		
		// Average of all the sections
		cell = keyRow2.createCell(0);
		cell.setCellStyle(styleDataTotalsKey);
		
		cell = keyRow2.createCell(1);
		cell.setCellValue("Average of all the sections");
		cell.setCellStyle(styleDataNoBorder);
		
		
		
		// Average of all the sections
		cell = keyRow3.createCell(0);
		cell.setCellStyle(styleDataRowTotalsKey);
		
		cell = keyRow3.createCell(1);
		cell.setCellValue("Average by skill");
		cell.setCellStyle(styleDataNoBorder);
	}
}

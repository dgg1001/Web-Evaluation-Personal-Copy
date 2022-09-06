package edu.sru.group3.WebBasedEvaluations.excel;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
/*

public class ExcelRead {

	// Upload File
	public static XSSFSheet loadFile(String file) throws IOException {
		
		FileInputStream thisxls;
		XSSFWorkbook wb;
		XSSFSheet sheet;

		thisxls = new FileInputStream(file);
		wb = new XSSFWorkbook(thisxls);
		sheet = wb.getSheetAt(0);

		return sheet;
	}
	
	// checkStringType
	public static String checkStringType(XSSFCell testCell)
	{
		if (testCell != null) {
			if(testCell.getCellType() == CellType.NUMERIC)
			{
				return Integer.toString((int)testCell.getNumericCellValue());
			}
			
			return testCell.getStringCellValue();
		} else {
			return "EMPTY_CELL";
		}
		
	}
	
	// checkIntType
	public static int checkIntType(XSSFCell testCell)
	{
		
		if (testCell != null) {
			if(testCell.getCellType() == CellType.STRING)
			{
				return Integer.parseInt(testCell.getStringCellValue());
			}
			return (int)testCell.getNumericCellValue();
		} else {
			return -1;
		}
	}
}
*/
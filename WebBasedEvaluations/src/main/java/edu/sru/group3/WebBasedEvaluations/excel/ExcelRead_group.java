package edu.sru.group3.WebBasedEvaluations.excel;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;


public class ExcelRead_group {

	// Upload File
	public static XSSFWorkbook loadFile(MultipartFile file) throws IOException {
		
		FileInputStream thisxls;
		XSSFWorkbook wb;
		XSSFSheet sheet;

		//thisxls = new FileInputStream(file);
		wb = new XSSFWorkbook(file.getInputStream());
		sheet = wb.getSheetAt(0);

		return wb;
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
			return null;
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
	public static long checkLongType(XSSFCell testCell)
	{
		
		if (testCell != null) {
			if(testCell.getCellType() == CellType.STRING)
			{
				return Long.parseLong(testCell.getStringCellValue());
			}
			return (long)testCell.getNumericCellValue();
		} else {
			return (long) -1;
		}
	}
}
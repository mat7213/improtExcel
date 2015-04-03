package improtExcel.helper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 * 
 * @author weiyao.chen
 * 
 */

public class ExcelHelper {
	private static Logger logger = Logger.getLogger(ExcelHelper.class.getName());
	
	/**
	 * load sheet1中所有数据
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException 
	 */
	public static String[][] loadAllData(String filePath) throws IOException {
		XSSFWorkbook xwb = null;
		try {
			xwb = new XSSFWorkbook(new FileInputStream(filePath));
		} catch (IOException e) {
			logger.error("read Excel file from " + filePath + " IOException: "
					+ e.getMessage());
			return null;
		}
		try {
			XSSFSheet sheet = null;
			sheet = xwb.getSheetAt(0);
			XSSFRow row = null;
			String[][] result = new String[sheet.getPhysicalNumberOfRows() - 1][sheet
					.getRow(0).getLastCellNum()];

			for (int i = sheet.getFirstRowNum(); i < sheet
					.getPhysicalNumberOfRows() - 1; i++) {

				row = sheet.getRow(i + 1);

				for (int j = row.getFirstCellNum(); j < row.getLastCellNum(); j++) {
					result[i][j] = row.getCell(j).toString();
				}

			}
			return result;
		} catch (Exception e) {
			logger.error("readLine Exception: " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * load 指定sheet指定行开始的所有数据
	 * 
	 * @param filePath
	 * @param sheetID
	 * @param beginLine
	 * @return
	 */

	public static String[][] loadOneSheetData(String filePath,int sheetID,int beginLine) {

		XSSFWorkbook xwb = null;
		try {
			xwb = new XSSFWorkbook(new FileInputStream(filePath));
		} catch (IOException e) {
			logger.error("read Excel file from " + filePath + "sheet"+sheetID+" IOException: "
					+ e.getMessage());
			return null;
		}
		try {
			XSSFSheet sheet = null;
			sheet = xwb.getSheetAt(sheetID);
			XSSFRow row = null;
			String[][] result = new String[sheet.getPhysicalNumberOfRows()][sheet
					.getRow(0).getLastCellNum()];

			for (int i = beginLine-1; i < sheet
					.getPhysicalNumberOfRows(); i++) {

				row = sheet.getRow(i);

				for (int j = row.getFirstCellNum(); j < row.getLastCellNum(); j++) {
					if(row.getCell(j)!=null){
					result[i][j] = row.getCell(j).toString();
					}
					else result[i][j]="";
					}
			}
			return result;
		} catch (Exception e) {
			logger.error("readLine Exception: " + e.getMessage());
			return null;
		}
	}
}

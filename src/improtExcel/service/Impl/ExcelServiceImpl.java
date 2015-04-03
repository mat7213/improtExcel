package improtExcel.service.Impl;

import improtExcel.helper.ExcelHelper;

import improtExcel.service.ExcelService;

public class ExcelServiceImpl implements ExcelService {

	@Override
	public Object[][] noSort(String filePath) {
		// TODO Auto-generated method stub
		String[][] orgData = ExcelHelper.loadOneSheetData(filePath, 0,0 );

		return orgData;
	}

	@Override
	public String ExportProjectExcel(int vid) {
		// TODO Auto-generated method stub
		return null;
	}

}

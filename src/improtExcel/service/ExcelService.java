package improtExcel.service;


public interface ExcelService {
	
    public Object[][] noSort(String filePath);
    
    public String ExportProjectExcel(int vid);

}

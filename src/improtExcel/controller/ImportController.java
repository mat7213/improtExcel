package improtExcel.controller;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

import org.apache.log4j.Logger;

import improtExcel.helper.ExcelHelper;
import improtExcel.service.*;

public class ImportController {	

	 public static void main(String[] args) throws IOException {
		
		String filePath="./src/dict.xlsx";
		String[][] dicts=ExcelHelper.loadAllData(filePath);
		int length=dicts.length;
		int level3_id=0;
		for(int i=0;i<length;i++){
			String[] dictNames= null;
			String[] dictName5= null;
			String dictName1 =new String();
			String dictName2 =new String();
			String dictName3 =new String();
			String dictName4 =new String();
			if(dicts[i][0]!=null)
			{
				dictNames=dicts[i][0].split("/");
				dictName1=dictNames[0];
				if(dictNames.length>1)
				{
					dictName2=dictNames[1];
				}
			}
			if(dicts[i][1]!=null){
				dictName3=dicts[i][1];			
			}
			if(dicts[i][2]!=null)
			{
				dictName4=dicts[i][2];
			}
			if(dicts[i][3]!=null)
			{
				dictName5=dicts[i][3].split("、");
			}
			level3_id= ImprotDict.addWords(dictName1, dictName2, dictName3, dictName4, dictName5, level3_id);
		}
		
		
		System.out.printf("import finished");
	 }
}

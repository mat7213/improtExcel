package improtExcel.service;

import improtExcel.model.DictDao;

public class ImprotDict {
	
	static private DictDao database;

	public static int addWords(String level1,String level2,String level3,String level4, String[] words,int level3_id){
		if(level1!=null){
			int level1id = database.addName(0,level1);
			int level2id = database.addName(level1id,level2);
			level3_id = database.addName(level2id,level3);
			int level4id = database.addName(level3_id,level4);
			for(int i=0;i<words.length;i++){
				int result= database.addNewWord(words[i],String.valueOf(level4id));
			}

		}
		else{
			int level4id = database.addName(level3_id,level4);
			for(int i=0;i<words.length;i++){
				int result= database.addNewWord(words[i],String.valueOf(level4id));
			}
		}
		return level3_id;
	}
}

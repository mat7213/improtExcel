package improtExcel.model;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.context.WebApplicationContext;


public class DictDao {
	BasicDataSource source;
	JdbcTemplate template;
	
	private String outputPath;
	
	private String driver;
	private String server;
	private String user;
	private String passwd;
	
	public DictDao() {
		

	}
	
	public void OnInit() {
		if(source == null) {
			source = new BasicDataSource();
			source.setDriverClassName(driver);
			source.setUrl(server);
			source.setUsername(user);
			source.setPassword(passwd);
			template = new JdbcTemplate(source);
		}
	}
	
	public void OnClose() {
		if(source != null) {
			try {
				source.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			source = null;
			template = null;
		}
	}
	
	public String getDriver() {
		return driver;
	}


	public void setDriver(String driver) {
		this.driver = driver;
	}


	public String getServer() {
		return server;
	}


	public void setServer(String server) {
		this.server = server;
	}


	public String getUser() {
		return user;
	}


	public void setUser(String user) {
		this.user = user;
	}


	public String getPasswd() {
		return passwd;
	}


	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}


	public void close() throws SQLException {
		source.close();
	}
	
	private void run(String[] inputTables, String outputFile) throws IOException {
		OutputStream fout = new FileOutputStream(outputFile);
		final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
				new BufferedOutputStream(fout), "UTF-8"));
		
		for(String input : inputTables) {
			template.query("select url, url_crc, format_content from " + input + " limit 1000", new RowCallbackHandler(){

				@Override
				public void processRow(ResultSet r) throws SQLException {
					try {
						OnRow(r, writer);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
	
	public void OnRow(ResultSet r, BufferedWriter writer) throws SQLException, IOException {
		String url = r.getString("url");
		String urlcrc = r.getString("url_crc");
		String formatcontent = r.getString("format_content");
		
		writer.write("@@url@@ ");
		writer.write(urlcrc);
		writer.write(" ");
		writer.write(url);
		writer.write("\n");
		writer.write(formatcontent);
		writer.write("\n");
	}




	public int updateName(int id, String name) {
		int result = template.update("update dict_info set dictName = '"+name+"' where id="+id);
		return result;
	}

	public int updateTree(int id ,int parentid){
		int expid = template.queryForInt("select parentid from dict_info where id ="+id);
		//更新拖拽节点的双亲节点
		int result = template.update("update dict_info set parentid = '"+parentid+"' where id="+id);
		//判断原双亲节点是否仍有子节点，更新isParent情况
		int isparent = template.queryForInt("select count(*) from dict_info where parentid="+expid);
		System.out.println("expid的子节点个数为"+isparent);
		if(isparent==0)
			template.update("update dict_info set isParent ='false' where id = "+expid);
		//更新新双亲节点的isParent情况
		int result1 = template.update("update dict_info set isParent = 'true' where id = "+parentid);
		return result;
	}
	
	public int deleteName(int id, int parentid) {
		int result = template.update("delete from dict_info where id="+id);
		int result1 = template.update("delete from word_dict_relation where dictid="+id);
		int isparent = template.queryForInt("select count(*) from dict_info where parentid="+parentid);
		if(!(isparent>0))
			template.update("update dict_info set isParent ='false' where id = "+parentid);
		return result;
			
	}

	/**
	 * 添加一个子类，返回这个类号
	 * @param id
	 * @param name
	 * @return
	 */
	public int addName(int id, String name) {
		int maxcount = template.queryForInt("select max(id) from dict_info");
		int result0 = template.update("update dict_info set isParent = 'true' where id ="+id);
		int result = template.update("insert into dict_info (id, dictName, parentid, isParent) values ("+(maxcount+1)+",'"+name+"',"+id+",'false')");
		// TODO Auto-generated method stub
		return maxcount+1;
	}
	

	
	public String getAllChildren(int id, String tempresult) {
		
		List<Map<String, Object>> list = template.queryForList("select id from dict_info where parentid="+id);
		for(int i = 0;i < list.size();i++)
		{
			String temp = String.valueOf(list.get(i).get("id"));
			tempresult = tempresult + temp + ",";
			getAllChildren(Integer.valueOf(temp),tempresult);
		}
		return tempresult;
	}
	

	private void rewrite() {
		
		template.query("select wordid,dictid from word_dict_relation",  new RowCallbackHandler() {

			@Override
			public void processRow(ResultSet r)
					throws SQLException {
				int wordid = Integer.valueOf(r.getString(1));
				int dictid = Integer.valueOf(r.getString(2));
				System.out.println(wordid);
				template.update("insert into word_dict_relation_new values ("+wordid+","+dictid+")");
				
			}
		});
		
	}
	
	public int existsRelation (int id, Integer dictid) {
		int exist = template.queryForInt("select count(*) from word_dict_relation where wordid="+id+" and dictid = " + dictid );
		return exist;
	}
	
	public int addNodeClass(int id, Integer dictid, String name) {
		int result = 0;
		int exist = template.queryForInt("select count(*) from word_info where id="+id);
		if(exist<1)//不存在
		{
			int wordmax = template.queryForInt("select max(id) from word_info")+1;
			template.update("insert into word_info (id,word) values ("+wordmax+",'"+name+"')");
			result = template.update("insert into word_dict_relation(wordid,dictid) values ("+wordmax+","+dictid+")");
			return result;
		}
		int temp = template.queryForInt("select count(*) from word_dict_relation where wordid="+id+" and dictid="+dictid);
		if(temp>0)
			result = 1;
		else
		{
			result = template.update("insert into word_dict_relation(wordid,dictid) values ("+id+","+dictid+")");
		}
		return result;
	}

	public int delNodeClass(int id, Integer dictid) {
		// 删除的时候只删除关系
		int result = 0;
		int exist = template.queryForInt("select count(*) from word_dict_relation where wordid="+id+" and dictid="+dictid);
		if(exist<1)//不存在
		{
			result = 0;
			return result;
		}else
		{
			result = template.update("delete from word_dict_relation where wordid="+id+" and dictid="+dictid);
			int ex1 = template.queryForInt("select count(*) from word_dict_relation where wordid="+id);
			if(ex1<1)
			{
				template.update("delete from word_info where id = "+id);
				return result;
			}
			else
			{
				return result;
			}
		}
		
	}

	
	public HashMap<Integer, String> getWords()
	{
		
		final HashMap<Integer, String> map = new HashMap<Integer, String>();
		template.query("select id,word from word_info",  new RowCallbackHandler() {

			int count = 0;

			@Override
			public void processRow(ResultSet r)
					throws SQLException {
				map.put(r.getInt("id"), r.getString("word"));
				count++;
				if(count%10000==0)
					System.out.println(count);
			}
		});
		return map;
	}
	
	public HashMap<Integer, List<Integer>> getDictWord()
	{
		final HashMap<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
		template.query("select id from dict_info",  new RowCallbackHandler() {
			int id;
			int count = 0;
			@Override
			public void processRow(ResultSet r)
					throws SQLException {
				List<Integer> words = new ArrayList<Integer>();
				id = r.getInt(1);
				List<Map<String, Object>> list = template.queryForList("select DISTINCT wordid from word_dict_relation where dictid="+id);
				int len = list.size();
//				for(int i = 0;i < len;i++)
//				{
//					words.add((int) list.get(i).get("wordid"));
//				}
				
//				for(Map<String, Object> obj:list)
//				{
//					words.add((int) obj.get("wordid"));
//				}
				for(Iterator<Map<String, Object>> i = list.iterator(); i.hasNext();)
				{
					Object obj = i.next().get("wordid");
					words.add((Integer) obj);
				}
				map.put(id, words);
				count++;
				if(count%100==0)
					System.out.println(count);
			}
		});
		return map;
	}

	public int[] getWordDict(int wordid) {
		List<Map<String, Object>> words = template.queryForList("select DISTINCT dictid from word_dict_relation where wordid ="+wordid);
		int size = words.size();
		int[] wordarray = new int[size];
		for(int i = 0;i < size;i++)
		{
			wordarray[i] = (Integer) words.get(i).get("dictid");
		}
		return wordarray;
		
	}
	public int getWordId(String tmpword)
	{
		String word = escape(tmpword);
		SqlRowSet rs = template.queryForRowSet("select id from word_info where word='"+word+"'");
		if(rs.next())
			return rs.getInt(1);
		else
			return -1;
	}
	private String escape(String text) {
		if(text == null)
			return "";
		StringBuilder builder = new StringBuilder();
		for(char ch : text.toCharArray()) {
			if(ch == '\'')
				builder.append("\\'");
			else
				builder.append(ch);
		}
		return builder.toString();
	}


	public String getWordById(int id) {
		
		SqlRowSet rs = template.queryForRowSet("select word from word_info where id="+id);
		if(rs.next())
			return rs.getString(1);
		else
			return "not found";

	}


	/**
	 * 返回word的hashmap<String, Integer>词，词id
	 * @return
	 */
	public HashMap<String, Integer> getWordMap() {
		final HashMap<String, Integer> map = new HashMap<String, Integer>();
		template.query("select id,word from word_info",  new RowCallbackHandler() {
			int count = 0;
			@Override
			public void processRow(ResultSet r)
					throws SQLException {
				map.put(r.getString(2), r.getInt(1));
				count++;
				if(count%100000==0)
					System.out.println(count);
			}
		});
		return map;
	}


	public int addNewWord(String tmpword, String t) {
		int result = -1;
		//添加词
		String word = escape(tmpword);
		word = word.trim();
		System.out.println(word);
		int exitword = template.queryForInt("select count(*) from word_info where word='"+word+"'");
		
		if(exitword<1)
		{
			System.out.println(exitword);
			System.out.println(word+"不存在。");
			int wordmax = template.queryForInt("select max(id) from word_info")+1;
			template.update("insert into word_info (id,word) values ("+wordmax+",'"+word+"')"); 
			template.update("insert into word_dict_relation (wordid,dictid) values ("+wordmax+","+Integer.valueOf(t)+")");
			result = wordmax;
			System.out.println(word+"已经存入。");
		}
		else{
			System.out.println(exitword+"存入关系");
			List wordids = template.queryForList("select id from word_info where word='"+word+"'");
			Iterator it = wordids.iterator();  
			int wordid = 0;
			if(it.hasNext()) {  
			    
			Map userMap = (Map) it.next();  
			    
			wordid =(int) userMap.get("id");  
			    
			}
			System.out.println(wordid);
			{
				int existworddict = template.queryForInt("select count(*) from word_dict_relation where wordid="+wordid+" and dictid="+Integer.valueOf(t));
				System.out.println(existworddict);
				if(existworddict<1)
					template.update("insert into word_dict_relation (wordid,dictid) values ("+wordid+","+Integer.valueOf(t)+")");
			}
			result = wordid;
			System.out.println(word+"已经存入。");
		}
		return result;
		
		
	}
	
	public static void main(String[] args) throws IOException, SQLException {
		DictDao demo  = new DictDao();
		String text = "易安信";
		int tx = demo.addNewWord(text,"70460");
		System.out.println(tx);
		demo.close();
	}
}

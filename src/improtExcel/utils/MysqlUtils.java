package improtExcel.utils;

import java.sql.*;
import hylanda.segment.HLDef;
import hylanda.segment.HLSwknl;

public class MysqlUtils {

	public static String url = "jdbc:mysql://localhost:3306/dictionary";//characterEncoding=GBK
    public static String username = "root";
    public static String password = "root";
    public static Connection con;
    public static Statement stmt;
    public static ResultSet rs;

    public static HLSwknl oHLSwknl = new HLSwknl();
	public static boolean bInit = oHLSwknl.HLInitSeg("","");
	public static int nMode = HLDef.SEGMENT_RESULT_EX_MODE;
	public static boolean bIsFormat = false;
	public static int nExtCalcFlag  = HLDef.HL_CAL_OPT_POS;
	public static long hHandle = oHLSwknl.HLCreateSegHandle(nMode,bIsFormat);
    public static void main(String[] args) throws SQLException {
    	if(!bInit)
		{
			System.out.println("初始化不成功?");
			return ;
		}
        connect();
        test();
        stmt.close();
        con.close();
    }
    public static void test() {
        String sql_select = "select word from word_info where word like '%�?";
        select(sql_select);
    }
    public static void connect() {
        // 定位驱动
        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("加载驱动成功!");
        } catch (ClassNotFoundException e) {
            System.out.println("加载驱动失败!");
            e.printStackTrace();
        }
        // 建立连接
        try {
            con = DriverManager.getConnection(url, username, password);
            stmt = con.createStatement();
            System.out.println("数据库连接成功");
        } catch(SQLException e) {
            System.out.println("数据库连接失败");
        }
    } 
    public static void select(String sql) {
    	System.out.println("查询");
        try {
            rs = stmt.executeQuery(sql);
            ResultSetMetaData meta_data = rs.getMetaData();//列名
            System.out.println("查询结果如下");
            int k =0;
            while (rs.next()) {
                ///for (int i_col = 1; i_col <= meta_data.getColumnCount(); i_col++) {
            	k++;
            	String string = rs.getString(1);
            	System.out.println(string);
                boolean bSeg = oHLSwknl.HLSegment(hHandle,string,nExtCalcFlag);
            	if(bSeg){
            		int nSegResultCnt = oHLSwknl.HLGetResultCnt(hHandle);
            		for(int i = 0; i < nSegResultCnt; i++){
            			System.out.print(oHLSwknl.HLGetWordAt(hHandle,i)+""+GetPos(oHLSwknl.HLGetWordPosAt(hHandle,i))+" ");
            		}
            		System.out.println();
            		String n =GetPos(oHLSwknl.HLGetWordPosAt(hHandle,0)).split("/")[1];
            		if(nSegResultCnt > 3)
            			System.out.println(" 删除");
            		else if(nSegResultCnt == 3&&string.length()>4)
            			System.out.println(" 删除");
            		else if(nSegResultCnt == 2 &&(n.equals("n")||n.equals("r")||n.equals("nr")||n.equals("ns")||n.equals("nt")||n.equals("nz")||n.equals("e")||n.equals("f")||n.equals("mq")||n.equals("d")))
            			System.out.println(" 删除");
            		else 
            			System.out.println(" 保留");
            	}
            }
            System.out.println(k);
            rs.close();
        }catch (Exception e) {
            System.out.println("数据查询失败!");
        }
    }
    
    public static void insert(String sql) {
        try {
            stmt.clearBatch();
            stmt.addBatch(sql);
            stmt.executeBatch();
            System.out.println("数据插入成功!");
        }catch (Exception e) {
            System.out.println("数据插入失败!");
        }

    }
    public static void update(String sql) {
        try {
            stmt.executeUpdate(sql);
            System.out.println("数据更新成功!");
        }catch (Exception e) {
            System.out.println("数据更新失败!");
        }
    }

  //根据词�?标志，转化对应字�?
  	public static String GetPos(int nPos)
  	{
  		if((nPos & HLDef.NATURE_D_A) == HLDef.NATURE_D_A)
  		{//形容�?
  			return "/a";
  		}
  		else if((nPos & HLDef.NATURE_D_B) == HLDef.NATURE_D_B)
  		{//区别�?区别语素
  			return "/b";
  		}
  		else if((nPos & HLDef.NATURE_D_C) == HLDef.NATURE_D_C)
  		{//连词 连语�?
  			return "/c";
  		}
  		else if((nPos & HLDef.NATURE_D_D) == HLDef.NATURE_D_D)
  		{//副词 副语�?
  			return "/d";
  		}
  		else if((nPos & HLDef.NATURE_D_E) == HLDef.NATURE_D_E)
  		{//产品�?
  			return "/e";
  		}
  		else if((nPos & HLDef.NATURE_D_F) == HLDef.NATURE_D_F)
  		{//方位�?方位语素
  			return "/f";
  		}
  		else if((nPos & HLDef.NATURE_D_I) == HLDef.NATURE_D_I)
  		{//成语
  			return "/i";
  		}
  		else if((nPos & HLDef.NATURE_D_L) == HLDef.NATURE_D_L)
  		{//习语
  			return "/l";
  		}
  		else if((nPos & HLDef.NATURE_A_M) == HLDef.NATURE_A_M)
  		{//数词
  			return "/m";
  		}
  		else if((nPos & HLDef.NATURE_D_MQ) == HLDef.NATURE_D_MQ)
  		{//数量�?
  			return "/mq";
  		}
  		else if((nPos & HLDef.NATURE_D_N) == HLDef.NATURE_D_N)
  		{//名词
  			return "/n";
  		}
  		else if((nPos & HLDef.NATURE_D_O) == HLDef.NATURE_D_O)
  		{//拟声�?
  			return "/o";
  		}
  		else if((nPos & HLDef.NATURE_D_P) == HLDef.NATURE_D_P)
  		{//介词
  			return "/p";
  		}
  		else if((nPos & HLDef.NATURE_A_Q) == HLDef.NATURE_A_Q)
  		{//量词
  			return "/q";
  		}
  		else if((nPos & HLDef.NATURE_D_R) == HLDef.NATURE_D_R)
  		{//代词
  			return "/r";
  		}
  		else if((nPos & HLDef.NATURE_D_S) == HLDef.NATURE_D_S)
  		{//处所�?
  			return "/s";
  		}
  		else if((nPos & HLDef.NATURE_D_T) == HLDef.NATURE_D_T)
  		{//时间�?
  			return "/t";
  		}
  		else if((nPos & HLDef.NATURE_D_U) == HLDef.NATURE_D_U)
  		{//助词
  			return "/u";
  		}
  		else if((nPos & HLDef.NATURE_D_V) == HLDef.NATURE_D_V)
  		{//动词
  			return "/v";
  		}
  		else if((nPos & HLDef.NATURE_D_W) == HLDef.NATURE_D_W)
  		{//标点符号
  			return "/w";
  		}
  		else if((nPos & HLDef.NATURE_D_X) == HLDef.NATURE_D_X)
  		{//非语素字
  			return "/x";
  		}
  		else if((nPos & HLDef.NATURE_D_Y) == HLDef.NATURE_D_Y)
  		{//语气�?
  			return "/y";
  		}
  		else if((nPos & HLDef.NATURE_D_Z) == HLDef.NATURE_D_Z)
  		{//状�?�?
  			return "/z";
  		}
  		else if((nPos & HLDef.NATURE_A_NR) == HLDef.NATURE_A_NR)
  		{//人名
  			return "/nr";
  		}
  		else if((nPos & HLDef.NATURE_A_NS) == HLDef.NATURE_A_NS)
  		{//地名
  			return "/ns";
  		}
  		else if((nPos & HLDef.NATURE_A_NT) == HLDef.NATURE_A_NT)
  		{//机构团体
  			return "/nt";
  		}
  		else if((nPos & HLDef.NATURE_A_NX) == HLDef.NATURE_A_NX)
  		{//外文字符
  			return "/nx";
  		}
  		else if((nPos & HLDef.NATURE_A_NZ) == HLDef.NATURE_A_NZ)
  		{//其他专名
  			return "/nz";
  		}
  		else if((nPos & HLDef.NATURE_D_H) == HLDef.NATURE_D_H)
  		{//前接成分
  			return "/h";
  		}
  		else if((nPos & HLDef.NATURE_D_K) == HLDef.NATURE_D_K)
  		{//后接成分
  			return "/k";
  		}
  		else
  		{//未知，正常情况下应该没有
  			return "/?";
  		}
  	
  	}
    
}

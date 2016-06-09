package extraction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
/*1.通过构造函数选择需要连接的数据库并实现与数据库的连接
 * 2.通过返回stmt,使调用SQLConnection的类能够执行sql语句
 * */
public class SQLConnection {
	String database;
	Connection conn = null;
	Statement stmt=null;

	SQLConnection(String database){
		this.database=database;
		connect();
	}
	public void connect() {
		String url = "jdbc:mysql://localhost:3306/"+database+"?"
				+ "user=root&password=password&useUnicode=true&CharacterEncoding=UTF8";
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("成功加载mysql驱动");
			conn = DriverManager.getConnection(url);
			stmt = conn.createStatement();
		} catch (Exception e) {
			System.out.println("MySQL操作错误");
			e.printStackTrace();
		}
	}
	public Statement getStmt() {
		return stmt;
	}
	public String getDatabase() {
		return database;
	}
	public void setDatabase(String database) {
		this.database = database;
	}
}

package extraction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Test {
	String sql;
	SQLConnection sql1;
	Statement stmt;
	ResultSet resultSet;

	public Test(String database) {
		sql1 = new SQLConnection(database);
		this.stmt = sql1.getStmt();
	}

	public void testBugNum() throws SQLException{
		sql="select commit_id,file_id,current_file_path from file_commit where is_bug_intro=1";
		resultSet=stmt.executeQuery(sql);
		int count=0;
		while (resultSet.next()) {
			if (resultSet.getString(3).endsWith(".java")) {
				count++;
				System.out.println(resultSet.getInt(1)+"    "+resultSet.getInt(2));
			}
		}
		System.out.println(count);
	}
	
	public void testBugNumMy() throws SQLException{
		sql="select commit_id,file_id from extraction1 where bug_introducing=1";
		resultSet=stmt.executeQuery(sql);
		int count=0;
		while (resultSet.next()) {
				count++;
				System.out.println(resultSet.getInt(1)+"    "+resultSet.getInt(2));
		}
		System.out.println(count);
	}
	
	public void testBugIntro(int commit_id,int file_id) throws SQLException {	
		sql = "select hunk_id from hunk_blames where bug_commit_id="+commit_id+" and "+file_id+"=(select file_id from hunks where id=hunk_id)";
		resultSet = stmt.executeQuery(sql);
		List<Integer> hunk_ids = new ArrayList<>();
		while (resultSet.next()) {
			hunk_ids.add(resultSet.getInt(1));
		}
		List<List<Integer>> res=new ArrayList<>();
		for (Integer integer : hunk_ids) {
			sql = "select id,commit_id from hunks where id="
					+ integer
					+ " and 1=(select is_bug_fix from scmlog where id=commit_id)";
			resultSet = stmt.executeQuery(sql);

			while (resultSet.next()) {
				List<Integer> temp=new ArrayList<>();
				temp.add(resultSet.getInt(1));
				temp.add(resultSet.getInt(2));
				res.add(temp);
			}
		}
		System.out.println("hunk_id,commit_id");
		for (List<Integer> list : res) {
			System.out.println(list);
		}
	}

	public void scmlogSort() throws SQLException {
		sql = "select * from scmlog order by commit_date";
		resultSet = stmt.executeQuery(sql);
		List<Integer> commit_id = new ArrayList<>();
		while (resultSet.next()) {
			commit_id.add(resultSet.getInt(1));
		}
		for (Integer integer : commit_id) {
			System.out.println(integer);
		}
	}
}

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

	public void testBugNum() throws SQLException {
		sql = "select commit_id,file_id,current_file_path from file_commit where is_bug_intro=1";
		resultSet = stmt.executeQuery(sql);
		int count = 0;
		while (resultSet.next()) {
			if (resultSet.getString(3).endsWith(".java")) {
				count++;
				System.out.println(resultSet.getInt(1) + "    "
						+ resultSet.getInt(2));
			}
		}
		System.out.println(count);
	}

	public void testBugNumMy() throws SQLException {
		sql = "select commit_id,file_id from extraction1 where bug_introducing=1";
		resultSet = stmt.executeQuery(sql);
		int count = 0;
		while (resultSet.next()) {
			count++;
			System.out.println(resultSet.getInt(1) + "    "
					+ resultSet.getInt(2));
		}
		System.out.println(count);
	}

	/**
	 * 测试对于给定的，bug_introducing=1的实例，使用反推的方法测试其是否真的是引入bug的实例。需要注意的是，由于分支的原因，
	 * 同一个文件的不同时期的file_id可能不同
	 * ,这虽然是少数情况，但确实是存在的，例如voldemort工程中，第commi_id=499，file_id
	 * =160的文件与commit_id=2927，file_id=3473的文件是对应的.
	 * 故，在实际获取bug_introducing时需要考虑的是file_name的对应关系（file_name出现重复的情况不多见）
	 * 
	 * @param commit_id
	 *            待测试实例的commit_id。
	 * @param file_id
	 *            待测试实例的file_id。
	 * @throws SQLException
	 */
	public  void testBugIntro(int commit_id, int file_id) throws SQLException {
		String file_name = null;
		sql = "select file_name from files where id=" + file_id;
		resultSet = stmt.executeQuery(sql);
		while (resultSet.next()) {
			file_name = resultSet.getString(1);
		}
		sql = "select hunks.id,commit_id,file_id from hunks,files,scmlog where hunks.id in (select hunk_id from hunk_blames where bug_commit_id="
				+ commit_id
				+ ") and hunks.file_id=files.id and commit_id=scmlog.id and file_name='"
				+ file_name + "'" + " and is_bug_fix=1";
		resultSet = stmt.executeQuery(sql);
		List<List<Integer>> res = new ArrayList<>();
		while (resultSet.next()) {
			List<Integer> temp = new ArrayList<>();
			temp.add(resultSet.getInt(1));
			temp.add(resultSet.getInt(2));
			temp.add(resultSet.getInt(3));
			res.add(temp);
		}
		System.out.println("hunk_id,commit_id,file_id");
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

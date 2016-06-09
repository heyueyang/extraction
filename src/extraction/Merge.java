package extraction;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 将extraction1、extraction2和extraction3得到的数据进行合成。
 * 
 * @param content
 *            合成的内容，其中key值为id，commit_id，file_id组成的list，StringBuffer存放具体的属性信息，
 *            属性信息同时又包括了id、commit_id和file_id(因为将来在写文件的时候，这三个属性的值也需要被写入)。
 * @param sql
 *            sql语句的标识。
 * @param sqlConnection
 *            连接extraction1和extraction2所在的数据库。
 * @param stmt
 *            执行sql语句。
 * @param resultSet
 *            执行sql语句后得到的结果集。
 * @param id_commit_fileIds
 *            合成的实例的主键。分别对应与实例的id、commit_id和file_id，当这三项都为-1时，表明对应着的是属性名称而非属性值。
 * @author niu
 *
 */
public class Merge {
	Map<List<Integer>, StringBuffer> content;
	String sql;
	SQLConnection sqlConnection;
	Statement stmt;
	ResultSet resultSet;
	List<List<Integer>> id_commit_fileIds; // 对接的时候要注意，之前都是三维的，需要改

	/**
	 * 获取实例的主键集。函数名和实际功能不太对应，有待改正。
	 * 
	 * @return 所有实例的主键的集合。
	 */
	public List<List<Integer>> getCommit_fileId() {
		return id_commit_fileIds;
	}

	/**
	 * 设置实例的主键集。
	 * 
	 * @param id_commit_fileIds
	 */
	public void setCommit_fileId(List<List<Integer>> id_commit_fileIds) {
		this.id_commit_fileIds = id_commit_fileIds;
	}

	/**
	 * 获取所有实例内容。
	 * 
	 * @return content 所有实例内容，key为各实例的主键，value为实例属性的值。
	 */
	public Map<List<Integer>, StringBuffer> getContent() {
		return content;
	}

	/**
	 * 设置实例。
	 * 
	 * @param content
	 */
	public void setContent(Map<List<Integer>, StringBuffer> content) {
		this.content = content;
	}

	/**
	 * Merge的构造函数。 根据已有的信息构造Merge。
	 * 
	 * @param content
	 * @param id_commit_fileId
	 * @param database
	 */
	public Merge(Map<List<Integer>, StringBuffer> content,
			List<List<Integer>> id_commit_fileId, String database) {
		setContent(content);
		setCommit_fileId(id_commit_fileId);
		this.sqlConnection = new SQLConnection(database);
		stmt = sqlConnection.getStmt();
	}

	/**
	 * 合并extraction1和extraction2得到的数据。
	 * 
	 * @return 由extraction1和extraction2合并得到的实例集。
	 * @throws SQLException
	 */
	public Map<List<Integer>, StringBuffer> merge12() throws SQLException {
		System.out.println("merge12");
		Map<List<Integer>, StringBuffer> m12 = new HashMap<List<Integer>, StringBuffer>();
		for (List<Integer> commit_fileId : id_commit_fileIds) {
			System.out.println(commit_fileId.get(1) + "_"
					+ commit_fileId.get(2));
			StringBuffer temp = new StringBuffer();
			if (commit_fileId.get(1) == -1) {
				sql = "select * from extraction1 where id=1";
				resultSet = stmt.executeQuery(sql);
				int colcount = resultSet.getMetaData().getColumnCount();

				for (int i = 1; i <= colcount; i++) {
					temp.append(resultSet.getMetaData().getColumnName(i) + ",");
				}

			} else {
				sql = "select * from extraction1 where commit_id="
						+ commit_fileId.get(1) + " and file_id="
						+ commit_fileId.get(2);
				resultSet = stmt.executeQuery(sql);
				int colCount = resultSet.getMetaData().getColumnCount();
				resultSet.next();
				for (int i = 1; i <= colCount; i++) {
					temp.append(resultSet.getString(i) + ",");
				}
			}

			if (commit_fileId.get(1) == -1) {
				sql = "select * from extraction2 where id=1";
				resultSet = stmt.executeQuery(sql);
				int colcount = resultSet.getMetaData().getColumnCount();
				for (int i = 4; i <= colcount; i++) {
					temp.append(resultSet.getMetaData().getColumnName(i) + ",");
				}
				// System.out.println(temp);
			} else {
				sql = "select * from extraction2 where commit_id="
						+ commit_fileId.get(1) + " and file_id="
						+ commit_fileId.get(2);
				resultSet = stmt.executeQuery(sql);
				int colCount = resultSet.getMetaData().getColumnCount();
				resultSet.next();
				for (int i = 4; i <= colCount; i++) {
					temp.append(resultSet.getString(i) + ",");
				}
			}
			m12.put(commit_fileId, temp);
		}
		return m12;
	}

	public void merge123() throws SQLException, IOException {
		System.out.println("merge123");
		for (List<Integer> commit_fileId : id_commit_fileIds) {
			System.out.println(commit_fileId.get(1) + "_"
					+ commit_fileId.get(2));
			StringBuffer temp = new StringBuffer();
			if (commit_fileId.get(1) == -1) {
				sql = "select * from extraction1 where id=1";
				resultSet = stmt.executeQuery(sql);
				int colcount = resultSet.getMetaData().getColumnCount();

				for (int i = 1; i <= colcount; i++) {
					temp.append(resultSet.getMetaData().getColumnName(i) + ",");
				}

			} else {
				sql = "select * from extraction1 where commit_id="
						+ commit_fileId.get(1) + " and file_id="
						+ commit_fileId.get(2);
				resultSet = stmt.executeQuery(sql);
				int colCount = resultSet.getMetaData().getColumnCount();
				resultSet.next();
				for (int i = 1; i <= colCount; i++) {
					temp.append(resultSet.getString(i) + ",");
				}
			}

			if (commit_fileId.get(1) == -1) {
				sql = "select * from extraction2 where id=1";
				resultSet = stmt.executeQuery(sql);
				int colcount = resultSet.getMetaData().getColumnCount();
				for (int i = 4; i <= colcount; i++) {
					temp.append(resultSet.getMetaData().getColumnName(i) + ",");
				}
				// System.out.println(temp);
			} else {
				sql = "select * from extraction2 where commit_id="
						+ commit_fileId.get(1) + " and file_id="
						+ commit_fileId.get(2);
				resultSet = stmt.executeQuery(sql);
				int colCount = resultSet.getMetaData().getColumnCount();
				resultSet.next();
				for (int i = 4; i <= colCount; i++) {
					temp.append(resultSet.getString(i) + ",");
				}
			}

			content = merge(temp, content, commit_fileId.get(0),
					commit_fileId.get(1), commit_fileId.get(2));
		}
	}

	private Map<List<Integer>, StringBuffer> merge(StringBuffer temp,
			Map<List<Integer>, StringBuffer> content2, Integer id,
			Integer commitid, Integer fileid) {
		List<Integer> key = new ArrayList<>();
		key.add(id);
		key.add(commitid);
		key.add(fileid);
		StringBuffer addString = content2.get(key);
		addString.delete(0, addString.indexOf(",") + 1);
		addString.delete(0, addString.indexOf(",") + 1);
		addString.delete(0, addString.indexOf(",") + 1);
		content2.put(key, temp.append(addString));

		return content2;
	}

}

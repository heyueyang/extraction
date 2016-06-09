package extraction;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 针对某分为内的commit_id的file_id，提取understand得到的复杂度信息。
 * 为了防止程序规模过大，一般在进行bug分类的时候，数据选取extraction1中一部分实例。而extraction2则是针对extraction1
 * 中选择出的实例利用understand得到这些实例对应的文件的复杂度量。
 * 
 * @param attribute
 *            存储所有属性的列表
 * @param startId
 *            extraction1中选取实例范围的起始id，注意，不是起始的commit_id。
 * @param endId
 *            extraction1中选取实例范围的终止id。
 * @author niu
 *
 */
public class Extraction2 extends Extraction {
	List<String> attributes;
	int startId;
	int endId;

	// 不包括id,commit_id,file_id
	/**
	 * 
	 * @param database
	 * @param start
	 * @param end
	 * @throws SQLException
	 */
	public Extraction2(String database, int start, int end) throws SQLException {
		super(database);
		startId = start;
		endId = end;
		attributes = new ArrayList<>(); // 属性判断的时候还可以从数据库读取数据.
	}

	//startId和endId指的是要得到的数据的区间。如果两个参数为-1
	// 则表明对extraction1中的数据全部处理。
	/**
	 * 根据understand得到的复杂度文件filename提取选择出的各实例的复杂度信息。
	 * @param filename 利用understand得到的各文件的复杂度文件，是一个单个文件。
	 * @param gap 利用字符串gap的值有效区分（commit_id,file_id）对。
	 * @throws FileNotFoundException
	 * @throws SQLException
	 */
	public void extraFromTxt(String filename, String gap)
			throws FileNotFoundException, SQLException {

		sql = "create table extraction2(id int(11) primary key not null auto_increment,commit_id int(11),file_id int(11))";
		stmt.executeUpdate(sql);
		if (startId == -1 && endId == -1) {
			sql = "select commit_id,file_id from extraction1";
		} else {
			sql = "select commit_id,file_id from extraction1 where id>="
					+ startId + " and id<" + endId;
		}

		resultSet = stmt.executeQuery(sql);
		List<List<Integer>> commit_flieId = new ArrayList<>();
		while (resultSet.next()) {
			List<Integer> temp = new ArrayList<>();
			temp.add(resultSet.getInt(1));
			temp.add(resultSet.getInt(2));
			commit_flieId.add(temp);
		}
		for (List<Integer> list : commit_flieId) {
			sql = "insert extraction2 (commit_id,file_id) values("
					+ list.get(0) + "," + list.get(1) + ")";
			stmt.executeUpdate(sql);
		}
		Scanner in = new Scanner(new File(filename));
		String line = new String();
		while ((line = in.nextLine()) != null) {
			if (line.contains(gap)) {
				int commit_id = Integer.parseInt(line.substring(
						line.lastIndexOf("\\") + 1, line.lastIndexOf("."))
						.split("_")[0]);
				int file_id = Integer.parseInt(line.substring(
						line.lastIndexOf("\\") + 1, line.lastIndexOf("."))
						.split("_")[1]);
				System.out.println("commit_id " + commit_id + " file_id "
						+ file_id);
				String temp = in.nextLine();
				while (temp != null && (!temp.equals(""))) {
					String attr = temp.split(":")[0].replace(" ", ""); // 这里是不是可以用个正则表达式？
					double value = Double.parseDouble(temp.split(":")[1]
							.replace(" ", ""));
					if (!attributes.contains(attr)) {
						sql = "alter table extraction2 add column " + attr
								+ " float default 0";
						stmt.executeUpdate(sql);
						attributes.add(attr);
					}
					sql = "update extraction2 set " + attr + "=" + value
							+ " where commit_id=" + commit_id + " and file_id="
							+ file_id;
					stmt.executeUpdate(sql);
					if (in.hasNextLine()) {
						temp = in.nextLine();
					} else {
						temp = null;
					}
				}
			}
			if (!in.hasNextLine()) {
				break;
			}
		}
		in.close();
	}

/**
 * 根据understand得到的复杂度信息提取DeltMetrics。
 * @throws SQLException
 */
	public void creatDeltMetrics() throws SQLException {
		System.out.println("get delta metrics");
		List<String> attribute2 = new ArrayList<>();
		if (attributes.size() > 0) { // 一口气的构造，直接从内存中读
			attribute2.addAll(attributes);
		} else { // 非要分看查看的话只能从数据库再重新读一次了。
			sql = "desc extraction2";
			resultSet = stmt.executeQuery(sql);
			while (resultSet.next()) {
				String attString = resultSet.getString(1);
				if (attString.equals("id") || attString.equals("commit_id") // 虽然每次取出来一个列标签都得判断，但是总共的列数不多，还不值得优化
						|| attString.equals("file_id")) {
					continue;
				}
				attribute2.add(resultSet.getString(1));
			}
		}

		sql = "select id,commit_id,file_id from extraction2"; // 这个还是可以优化的，几千个元素装内存里，避免两次读。
		resultSet = stmt.executeQuery(sql);
		List<List<Integer>> list = new ArrayList<>();

		while (resultSet.next()) {
			List<Integer> temp = new ArrayList<>();
			temp.add(resultSet.getInt(1));
			temp.add(resultSet.getInt(2));
			temp.add(resultSet.getInt(3));
			list.add(temp);
		}

		for (String string : attribute2) {
			sql = "alter table extraction2 add column " + "Delta_" + string
					+ " float default 0";
			stmt.executeUpdate(sql);
			for (int i = 0; i < list.size(); i++) {
				sql = "select max(id) from extraction2 where id<"
						+ list.get(i).get(0) + " and file_id="
						+ list.get(i).get(2);
				resultSet = stmt.executeQuery(sql); // 返回一个至用resultset太笨了
				int preid = -1;
				while (resultSet.next()) {
					preid = resultSet.getInt(1);
				}
				if (preid > 0) {
					sql = "select " + string + " from extraction2 where id="
							+ preid;
					resultSet = stmt.executeQuery(sql);
					float des = 0;
					while (resultSet.next()) {
						des = resultSet.getFloat(1);
					}
					sql = "update extraction2 set Delta_" + string + "="
							+ string + "-" + des + "where id="
							+ list.get(i).get(0);
					stmt.executeUpdate(sql);
				}
			}

		}
	}
/**
 * 显示当前数据库中的表有哪些
 * @throws SQLException
 */
	public void Show() throws SQLException {
		sql = "show tables";
		resultSet = stmt.executeQuery(sql);
		while (resultSet.next()) {
			System.out.println(resultSet.getString(1));
		}
	}
}

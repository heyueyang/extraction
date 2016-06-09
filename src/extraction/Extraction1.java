package extraction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 从miningit生成的数据库中提取一些基本信息，例如作者姓名，提交时间，累计的bug计数等信息。 构造函数中提供需要连接的数据库。
 * 根据指定的范围获取commit_id列表（按照时间顺序）。通过对各表的操作获取一些基本数据。
 * @param sql
 *            用以提取数据并存放处理后的数据的数据库。
 * @param start
 *            指定的commit的起始值（按照时间排序）。
 * @param end
 *            指定的commit的结束值（按照时间排序）。
 * @author niu
 *
 */
public class Extraction1 extends Extraction {

	/**
	 * 提取第一部分change info，s为指定开始的commit_id，e为结束的commit_id
	 * @param database 指定的miningit生成数据的数据库。
	 * @param s 指定的commit的起始值
	 * @param e 指定的commit的结束值
	 * @throws Exception
	 */
	public Extraction1(String database, int s, int e) throws Exception {
		super(database, s, e);
	}

	/**
	 * 批量化执行若干函数。
	 * 此处四个获取信息的函数可以优化成一个，以减少时间开销，但是会增加代码长度。
	 * @throws SQLException
	 */
	public void Carry1() throws SQLException {
		CreateTable();
		initial();
		author_name();
		commit_day();
		commit_hour();
		change_log_length();
	}
/**
 * 批量化执行若干函数。
 * 防止Carry1责任过大，故将所有函数分为两部分执行。
 * @throws SQLException
 */
	public void Carry2() throws SQLException {
		sloc();
		cumulative_bug_count();
		cumulative_change_count();
		changed_LOC();
		bug_introducing();
	}
/**
 * 创建数据表extraction1。
 * 若构造函数中所连接的数据库中已经存在extraction1表，则会产生冲突。
 * 解决方案有2：（1）若之前的extraction1为本程序生成的表，则可将其卸载。
 * （2）若之前的extraction1为用户自己的表，则可考虑备份原表的数据，并删除原表（建议），
 * 或者重命名本程序中的extraction1的名称（不建议）。
 * @throws SQLException
 */
	public void CreateTable() throws SQLException {
		sql = "create table extraction1(id int(11) primary key not null auto_increment,commit_id int(11),file_id int(11),author_name varchar(255),commit_day varchar(15),commit_hour int(2),"
				+ "cumulative_change_count int(15) default 0,cumulative_bug_count int(15) default 0,change_log_length int(10),changed_LOC int(13),"
				+ "sloc int(15),bug_introducing tinyint(1) default 0)";
		int result = stmt.executeUpdate(sql);
		if (result != -1) {
			System.out.println("创建表extraction1成功");
		}
	}


/**
 * 初始化表格。
 * 根据指定范围内的按时间排序的commit列表（commit_ids）初始化extraction1。
 * 初始化内容包括id，commit_id，file_id。需要注意的是，目前只考虑java文件，且不考虑java中的测试文件
 * 所以在actions表中选择对应的项时需要进行过滤。参数表示想要提取file change信息的commit跨度
 * @throws SQLException
 */
	public void initial() throws SQLException {
		System.out.println("initial the table");
		for (Integer integer : commit_ids) {
			sql = "select commit_id,file_id,file_name,current_file_path from actions,files where commit_id="
					+ integer + " and file_id=files.id"; // 只选取java文件,同时排除测试文件。
			resultSet = stmt.executeQuery(sql);
			List<List<Integer>> list = new ArrayList<>();
			while (resultSet.next()) {
				if (resultSet.getString(3).contains(".java")
						&& (!resultSet.getString(4).contains("test"))) {   //过滤不完全，如果是Test呢？
					List<Integer> temp = new ArrayList<>();
					temp.add(resultSet.getInt(1));
					temp.add(resultSet.getInt(2));
					list.add(temp);
				}
			}

			for (List<Integer> list2 : list) {
				sql = "insert extraction1 (commit_id,file_id) values("
						+ list2.get(0) + "," + list2.get(1) + ")";
				stmt.executeUpdate(sql);
			}
		}
	}

/**
 * 获取作者姓名。
 * @throws SQLException
 */
	public void author_name() throws SQLException {
		System.out.println("get author_name");
		sql = "update extraction1,scmlog,people set extraction1.author_name=people.name where extraction1.commit_id="
				+ "scmlog.id and scmlog.author_id=people.id";
		stmt.executeUpdate(sql);
	}

	/**
	 * 获取提交的日期，以星期标示。
	 * @throws SQLException
	 */
	public void commit_day() throws SQLException {
		System.out.println("get commit_day");
		Map<Integer, String> mapD = new HashMap<>(); // 加入修改日期
		for (Integer integer : commit_ids) {
			sql = "select id,commit_date from scmlog where id=" + integer;
			resultSet = stmt.executeQuery(sql);
			while (resultSet.next()) {
				mapD.put(resultSet.getInt(1),
						resultSet.getString(2).split(" ")[0]);
			}
		}

		// System.out.println(mapD.size()); //测试是否提取出时间，结果正确
		Calendar calendar = Calendar.getInstance();// 获得一个日历
		String[] str = { "Sunday", "Monday", "Tuesday", "Wednesday",
				"Thursday", "Friday", "Saturday", };
		for (Integer i : mapD.keySet()) {
			int year = Integer.parseInt(mapD.get(i).split("-")[0]);
			int month = Integer.parseInt(mapD.get(i).split("-")[1]);
			int day = Integer.parseInt(mapD.get(i).split("-")[2]);

			calendar.set(year, month - 1, day);// 设置当前时间,月份是从0月开始计算
			int number = calendar.get(Calendar.DAY_OF_WEEK);// 星期表示1-7，是从星期日开始，
			mapD.put(i, str[number - 1]);
			sql = "update extraction1 set commit_day=\" " + str[number - 1]
					+ "\" where commit_id=" + i;
			stmt.executeUpdate(sql);
		}
	}

	/**
	 * 获取提交的时间，以小时标示。
	 * @throws NumberFormatException
	 * @throws SQLException
	 */
	public void commit_hour() throws NumberFormatException, SQLException {
		System.out.println("get commit_hour");
		Map<Integer, Integer> mapH = new HashMap<>(); // 加入修改时间
		for (Integer integer : commit_ids) {
			sql = "select id,commit_date from scmlog where id=" + integer;
			resultSet = stmt.executeQuery(sql);
			while (resultSet.next()) {
				mapH.put(resultSet.getInt(1), Integer.parseInt(resultSet
						.getString(2).split(" ")[1].split(":")[0]));
			}
		}

		Iterator<Entry<Integer, Integer>> iter = mapH.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Integer, Integer> e = iter.next();
			int key = (int) e.getKey();
			int value = (int) e.getValue();
			sql = "update  extraction1 set commit_hour=" + value
					+ "  where commit_id=" + key;
			stmt.executeUpdate(sql);
		}
	}

	/**
	 * 获取changlog的长度。
	 * @throws SQLException
	 */
	public void change_log_length() throws SQLException {
		sql = "select id, message from scmlog";
		resultSet = stmt.executeQuery(sql);
		Map<Integer, Integer> cllMap = new HashMap<>();
		while (resultSet.next()) {
			cllMap.put(resultSet.getInt(1), resultSet.getString(2).length());
		}
		for (Integer integer : commit_ids) {
			sql = "update extraction1 set change_log_length ="
					+ cllMap.get(integer) + " where commit_id=" + integer;
			stmt.executeUpdate(sql);
		}
	}

	/**
	 * 获取源码长度
	 * @throws SQLException
	 */
	public void sloc() throws SQLException {
		System.out.println("get sloc");
		sql = "update extraction1,metrics set extraction1.sloc=metrics.loc where extraction1.commit_id=metrics.commit_id and "
				+ "extraction1.file_id=metrics.file_id";
		stmt.executeUpdate(sql);
	}

	/**
	 * 获取累计的bug计数。
	 * @throws SQLException
	 */
	public void cumulative_bug_count() throws SQLException {
		System.out.println("get cumulative bug count");
		List<List<Integer>> cbclist = new ArrayList<>();
		for (Integer integer : commit_ids) {
			sql = "select id,is_bug_fix from scmlog where id=" + integer;
			resultSet = stmt.executeQuery(sql);

			while (resultSet.next()) { // 此处可能还能优化，is_bug_fix为0的项目或许不需要存储
				List<Integer> temp = new ArrayList<>();
				temp.add(resultSet.getInt(1));
				temp.add(resultSet.getInt(2));
				cbclist.add(temp);
			}
		}

		for (int i = 0; i < cbclist.size(); i++) {
			if (cbclist.get(i).get(1) == 1) {
				sql = "update extraction1 set cumulative_bug_count="
						+ "cumulative_bug_count+1 where commit_id="
						+ (cbclist.get(i).get(0) - 1);
				stmt.executeUpdate(sql);
			}
			sql = "select id,file_id from extraction1 where commit_id="
					+ (cbclist.get(i).get(0));

			resultSet = stmt.executeQuery(sql);
			List<List<Integer>> list = new ArrayList<>();
			while (resultSet.next()) {
				List<Integer> temp = new ArrayList<>();
				temp.add(resultSet.getInt(1));
				temp.add(resultSet.getInt(2));
				list.add(temp);
			}
			if (!list.isEmpty()) {
				for (List<Integer> list2 : list) {
					sql = "select max(id) from extraction1 where id<" // id与commit_id是否都是严格的时间序？不是的话就有bug。
							+ list2.get(0) + " and file_id=" + list2.get(1);
					resultSet = stmt.executeQuery(sql);
					if (resultSet.next()) {
						int maxId = resultSet.getInt(1);
						int maxCBC = 0;
						sql = "select cumulative_bug_count from extraction1 where id="
								+ maxId;
						resultSet = stmt.executeQuery(sql);
						while (resultSet.next()) {
							maxCBC = resultSet.getInt(1);
						}
						sql = "update extraction1 set cumulative_bug_count="
								+ maxCBC + " where id=" + list2.get(0);
						stmt.executeUpdate(sql);
					}
				}
			}
		}
	}

	/**
	 * 获取累计的change计数。
	 * @throws SQLException
	 */
	public void cumulative_change_count() throws SQLException {
		System.out.println("get cumulative change count");
		sql = "select id,file_id from extraction1";
		resultSet = stmt.executeQuery(sql);
		List<List<Integer>> list = new ArrayList<>();
		while (resultSet.next()) {
			List<Integer> temp = new ArrayList<>();
			temp.add(resultSet.getInt(1));
			temp.add(resultSet.getInt(2));
			list.add(temp);
		}
		// System.out.println(list.get(1));
		// System.out.println(list.size());
		for (List<Integer> list2 : list) {
			sql = "select max(id) from extraction1 where id<" + list2.get(0)
					+ " and file_id=" + list2.get(1);
			// System.out.println(sql);
			resultSet = stmt.executeQuery(sql);
			if (!resultSet.next()) { // 为空的情况下
				sql = "update extraction1 set cumulative_change_count=cumulative_change_count+1 where id="
						+ list2.get(0);
				// System.out.println(sql);
				stmt.executeUpdate(sql);
				continue;
			} else { // 不空的情况下
				int maxId = resultSet.getInt(1);
				// System.out.println(maxId);
				sql = "select cumulative_change_count from extraction1 where id="
						+ maxId;
				resultSet = stmt.executeQuery(sql);
				int cccMax = 0;
				while (resultSet.next()) {
					cccMax = resultSet.getInt(1);
				}
				sql = "update extraction1 set cumulative_change_count="
						+ (cccMax + 1) + " where id=" + list2.get(0);
				stmt.executeUpdate(sql);
			}
		}
	}

	/**
	 * 获取改变的代码的长度。
	 *主要从hunks中提取数据，如果在miningit中hunks运行两遍会导致hunks中数据有问题，出现重复项。
	 *数据库中为null的项取出的数值是0,而不是空。
	 * @throws SQLException
	 */
	public void changed_LOC() throws SQLException {
		System.out.println("get changed loc");
		sql = "select id,commit_id,file_id from extraction1";
		List<List<Integer>> re = new ArrayList<>();
		resultSet = stmt.executeQuery(sql);
		while (resultSet.next()) {
			List<Integer> temp = new ArrayList<>();
			temp.add(resultSet.getInt(1));
			temp.add(resultSet.getInt(2));
			temp.add(resultSet.getInt(3));
			re.add(temp);
		}
		for (List<Integer> list : re) {
			sql = "select old_start_line,old_end_line,new_start_line,new_end_line from hunks where commit_id="
					+ list.get(1) + " and file_id=" + list.get(2);
			resultSet = stmt.executeQuery(sql);
			int changeLoc = 0;
			while (resultSet.next()) {
				if (resultSet.getInt(1) != 0) {
					changeLoc = changeLoc + resultSet.getInt(2)
							- resultSet.getInt(1) + 1;
				}
				if (resultSet.getInt(3) != 0) {
					changeLoc = changeLoc + resultSet.getInt(4)
							- resultSet.getInt(3) + 1;
				}
			}
			sql = "update extraction1 set changed_LOC=" + changeLoc
					+ " where id=" + list.get(0);
			stmt.executeUpdate(sql);
		}
	}

	/**
	 * 获取类标号。
	 * 对于表extraction1中的每个实例（每一行内容）标识其是否为引入bug。bug_introducing为每个实例的类标签，用于
	 * 构建分类器。
	 * @throws SQLException
	 */
	public void bug_introducing() throws SQLException {
		System.out.println("get bug introducing");
		List<Integer> ids = new ArrayList<>();
		for (Integer integer : commit_ids) {
			sql = "select id from scmlog where is_bug_fix=1 and id=" + integer;
			resultSet = stmt.executeQuery(sql);
			while (resultSet.next()) {
				ids.add(resultSet.getInt(1));
			}
		}
       System.out.println(ids.size());
		for (Integer integer : ids) {
			sql = "select  id,file_id,old_end_line from hunks where commit_id=" + integer;
			resultSet = stmt.executeQuery(sql);
			List<List<Integer>> hunkFileId = new ArrayList<>(); // 有些只是行错位了也会被标记为bug_introducing。但是作为hunks的一部分好像也成。
			while (resultSet.next()) {
				if (resultSet.getInt(3)!=0) {  //此处算是个优化，如果old_end_line=0，那么必然是新加的内容，无需回溯。
					List<Integer> temp = new ArrayList<>();
					temp.add(resultSet.getInt(1));
					temp.add(resultSet.getInt(2));
					hunkFileId.add(temp);
				}
			}

			for (List<Integer> integer2 : hunkFileId) {
				sql = "update extraction1 set  bug_introducing=1 where file_id="
						+ integer2.get(1)
						+ " and commit_id IN (select bug_commit_id "    
						+ "from hunk_blames where hunk_id="
						+ integer2.get(0)
						+ ")";
				stmt.executeUpdate(sql);
			}
		}
	}
}

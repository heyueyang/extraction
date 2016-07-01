package extraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
	static FileOperation fileOperation;
	
	public static void main(String[] args) throws Exception {
	//	Extraction1 extraction1 = new Extraction1("voldemort", -1, 4305);
	//	extraction1.bug_introducing();
	Test test=new Test("voldemort");
	test.testBugIntro(568, 686);
	}

	public void example() throws SQLException, IOException {
		Extraction2 extraction2 = new Extraction2("voldemort");
		Map<List<Integer>, StringBuffer> content = new Extraction3("voldemort", "/home/niu/test/voldeProject", -1, -1).getContent();
		System.out.println(content.size());
		List<List<Integer>> id_commit_fileId = extraction2.GetId_commit_file();
		Merge merge = new Merge(content, "voldemort");
		FileOperation fileOperation = new FileOperation();
		fileOperation.writeContent(merge.merge123(), "123.csv");
	}

	static public void Automatic() throws Exception {
		Extraction1 extraction1 = new Extraction1("voldemort", -1, 4305); // 虽然只选取了300次commit，为了辨识这300次commit，
		extraction1.Carry1(); // //所有的commit都要处理。
		extraction1.Carry2();

		Extraction2 extraction2 = new Extraction2("voldemort", 1219, 1754);
		extraction2.extraFromTxt("501-800.txt", "voldeProject");
		extraction2.creatDeltMetrics();

		Extraction3 extraction3 = new Extraction3("voldemort", "/home/niu/test/voldeProject", -1, -1);
		fileOperation=new FileOperation();
		Merge merge=new Merge(extraction3.getContent(), "voldemort");
		fileOperation.writeContent(merge.merge123(), "123d.csv");
		fileOperation.writeDict("dict.txt", extraction3.getDictionary());
	}

	static public void testMatch() {
		String test = new String("http://hello.com");
		String[] arrayStrings = test.split("//");
		for (String string : arrayStrings) {
			System.out.println(string);
		}
	}

	static public void testBow(String fileName) throws IOException {
		BufferedReader brReader = new BufferedReader(new FileReader(new File(
				fileName)));
		String line;
		StringBuffer text = new StringBuffer();
		while ((line = brReader.readLine()) != null) {
			text.append(line + "\n");
		}
		brReader.close();
		Bow bow = new Bow();
		Map<String, Integer> bag = bow.bowP2(text);
		for (String s : bag.keySet()) {
			System.out.println(s + "    " + bag.get(s));
		}
	}

	public static void exectMerge() throws SQLException, IOException {
		String csvFileString = "/home/niu/test.csv";
		String projectHomeString = "/home/niu/project";
		Extraction3 extraction3 = new Extraction3("try",
				projectHomeString, 1000, 1499);
		Map<List<Integer>, StringBuffer> content = extraction3.getContent();
		FileOperation fileOperation = new FileOperation();
		List<List<Integer>> commit_fileIds = extraction3.getCommitId_fileIds();
		Merge merge = new Merge(content, commit_fileIds, "try");
		merge.merge123();
		FileOperation writeFile = new FileOperation();
		writeFile.writeContent(content, csvFileString);
		writeFile.writeDict("/home/niu/dict.txt", extraction3.dictionary);

	}

	public static void exect2() throws FileNotFoundException, SQLException {
		Extraction2 extraction2 = new Extraction2("try", 1000, 1499);
		extraction2.extraFromTxt("/home/niu/test/metrics.txt", "project");
		extraction2.creatDeltMetrics();
	}
}

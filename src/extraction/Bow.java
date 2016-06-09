package extraction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bow {
	Map<String, Integer> bag;
	String[] dictory2 = { "!=", "==", "++", "--", "||", "&&" };
	String[] dictory1 = { "=", "+", "-", "*", "/", "%", "!", "?" };
	String[] dictory3 = { "=", "!=", "+", "*", "-", "||", "/", "&", "%", "!",
			"?" }; // 去除注释中或者字符串中的特殊符号。

	public Map<String, Integer> bow(String text) {
		bag = new HashMap<String, Integer>();
		int startIndex = 0;
		int endIndex = 0;
		while (endIndex <= text.length() - 1) {
			while (endIndex <= text.length() - 1
					&& (!isCharacter(text.charAt(endIndex)))) {
				endIndex++;
			}
			startIndex = endIndex;
			while ((endIndex <= text.length() - 1)
					&& isCharacter(text.charAt(endIndex))) {
				endIndex++;
			}
			String subString = text.substring(startIndex, endIndex);
			subString = subString.toLowerCase();
			if (bag.keySet().contains(subString)) {
				bag.put(subString, bag.get(subString) + 1);
			} else {
				bag.put(subString, 1);
			}
			while (endIndex <= text.length() - 1
					&& (!isCharacter(text.charAt(endIndex)))) {
				endIndex++;
			}
			startIndex = endIndex;
		}

		return bag;
	}

	public void printBag() {
		System.out.println(bag);
	}

	public boolean isCharacter(char c) {
		if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
			return true;
		}
		return false;
	}

	public int getIndex(StringBuffer text, int start) {
		while (start < text.length() - 1
				&& (!text.substring(start, start + 2).equals("/*"))
				&& text.charAt(start) != '"'
				&& (!text.substring(start, start + 2).equals("//"))) {
			start++;
		}
		return start;
	}

	public Map<String, Integer> bowP2(StringBuffer text) {
		StringBuffer hunkBuffer = new StringBuffer();
		bag = new HashMap<String, Integer>();
		int i = 1;
		while (text.toString().length() > 0) {
			int start = 0;

			start = getIndex(text, start);

			while (text.charAt(start) == '"') {
				if (start > 0 && text.charAt(start - 1) == '\\') {    //第一个引号的出现形式肯定不可能是乱七八糟的,只要处理好尾巴就行了
					start++;
					start = getIndex(text, start);
				} else {
					break;
				}
			}
			
			if (start == text.length() - 1) {
				hunkBuffer.append(text);
				break;
			}

			hunkBuffer.append(" " + text.substring(0, start));
			text.delete(0, start);
			start = 0;
			String startOper = new String();
			if (text.charAt(start) == '/') { // 匹配之前出现的操作符
				if (text.charAt(start + 1) == '*') {
					startOper = "/*";
				} else {
					startOper = "//";
				}
			} else {
				startOper = "\"";
			}
			String rage;

			if (startOper.equals("//")) {
				int inedex = text.indexOf("\n");
				if (inedex == -1) { // 最后一样的注释
					rage = text.substring(start + 2, text.length());
					text = null;
				} else { // 之后还有内容
					rage = text.substring(start + 2, text.indexOf("\n"));
					text.delete(0, text.indexOf("\n") + 1);
				}
				hunkBuffer.append(" " + removeSC2(rage));
			} else if (startOper.equals("/*")) {
				rage = text.substring(start + 2, text.indexOf("*/"));
				hunkBuffer.append(" " + removeSC2(rage));
				text.delete(0, text.indexOf("*/") + 2);
			} else {
				text.deleteCharAt(0);
				int tail = text.indexOf("\"");
				while((tail== 1 && text.charAt(tail - 1) == '\\')||(tail>1&&text.charAt(tail-1)=='\\'&&text.charAt(tail-2)!='\\')) { // 必定有一个tail与start对应
					tail = tail + 1;
					tail =tail+ text.substring(tail, text.length()).indexOf('"');
				}
				rage = text.substring(0, tail);
				i++;
				hunkBuffer.append(" " + removeSC2(rage));
				text.delete(0, tail+ 1);
			}
		}

		String dirList[] = hunkBuffer.toString().split(
				"[\\s{1,}\\){1,}\\({1,};:<>\"\\[{1,}\\]{1,}]|//|@"); // regular
																		// \\.{1,}不应该按照.分
																		// expression
																		// optimize
																		// ;:<>-\\/
		for (String string : dirList) {
			if (!string.equals("")) { // 这句话是不是也可以优化？
				boolean contain = false;
				for (String oper : dictory2) {
					contain = diviOper(oper, string, bag);
					if (contain == true) {
						break;
					}
				}
				if (contain == false) {
					for (String oper2 : dictory1) {
						contain = diviOper(oper2, string, bag);
						if (contain == true) {
							break;
						}
					}
				}
				if (contain == false) {
					if (bag.keySet().contains(string)) {
						bag.put(string, bag.get(string) + 1);
					} else {
						bag.put(string, 1);
					}
				}
			}
		}
		return bag;
	}

	private String removeSC2(String rage) {
		for (String string : dictory3) {
			rage = rage.replace(string, " ");
		}
		return rage;
	}

	public boolean diviOper(String oper, String string, Map<String, Integer> bag) {
		if (string.equals(oper)) {
			putInBag(string, bag);
			return true;
		}
		if (string.contains(oper)) {
			String divide1 = string.replace(oper, "");
			putInBag(divide1, bag);
			putInBag(oper, bag);
			return true;
		}
		return false;
	}

	public void putInBag(String string, Map<String, Integer> map) {
		if (map.containsKey(string)) {
			map.put(string, map.get(string) + 1);
		} else {
			map.put(string, 1);
		}
	}

	public Map<String, Integer> bowPP(String text) {
		bag = new HashMap<>();
		String dirList[] = text.split("/");
		String regex = ".*[A-Z].*";
		for (String string : dirList) {
			if (string.matches(regex)) {
				int startIndex = 0;
				int endIndex = 1;
				while (endIndex < string.length()) {
					while (endIndex < string.length()
							&& (!Character.isUpperCase(string.charAt(endIndex)))) {
						endIndex++;
					}
					String temp = string.substring(startIndex, endIndex)
							.toLowerCase(); // 之前没有处理大小写转换问题。
					if (bag.keySet().contains(temp)) {
						bag.put(temp, bag.get(temp) + 1);
					} else {
						bag.put(temp, 1);
					}
					startIndex = endIndex;
					endIndex = endIndex + 1;
					if (endIndex >= string.length()
							&& startIndex < string.length()) {
						if (bag.keySet().contains(string.charAt(startIndex))) {
							bag.put(string.charAt(startIndex) + "",
									bag.get(string.charAt(startIndex) + "") + 1);
						} else {
							bag.put(string.charAt(startIndex) + "", 1);
						}
					}
				}
			} else {
				if (bag.keySet().contains(string)) {
					bag.put(string, bag.get(string) + 1);
				} else {
					bag.put(string, 1);
				}
			}
		}
		return bag;
	}
}

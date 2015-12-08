package dqcup.repair.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dqcup.repair.RepairedCell;
import dqcup.repair.Tuple;

public class RegEx {
	public LinkedList<Tuple> tuples;

	public RegEx(LinkedList<Tuple> tuples) {
		super();
		this.tuples = tuples;
	}

	public HashSet<RepairedCell> verify(HashSet<RepairedCell> result) {
		String cropChar = "[`~!@#$%^&*()+=|{}':;',//[//].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		String temp = null;
		for (Tuple tuple : tuples) {
			// ZIP:邮政编码,五位纯数字
			Pattern pattern = Pattern.compile("[0-9]{5}");
			Matcher isNum = pattern.matcher(tuple.getValue("ZIP"));
			if (!isNum.matches()) {
				tuple.set("ZIP", "null");
				// result.add(new
				// RepairedCell(Integer.valueOf(tuple.getValue(0)), "ZIP",
				// tuple.getValue("ZIP")));
			}

			// SSN:社保号码,9位纯数字
			pattern = Pattern.compile("[0-9]{9}");
			isNum = pattern.matcher(tuple.getValue("SSN"));
			if (!isNum.matches()) {
				tuple.set("SSN", "null");
			}

			// FNAME:First name,可能包含字母,逗号及句号,首字母大写
			pattern = Pattern.compile("^[A-Z][a-z\\,\\.]*");
			temp = tuple.getValue("FNAME");
			isNum = pattern.matcher(temp);
			if (!isNum.matches()) {
				pattern = Pattern.compile("[A-Za-z\\,\\.]*");
				isNum = pattern.matcher(temp);
				// 如果只包含字母和符号
				if (isNum.matches()) {
					String tempAfter = temp.toLowerCase();
					tempAfter = tempAfter.replaceFirst(tempAfter.substring(0, 1),
							tempAfter.substring(0, 1).toUpperCase());
					if (tuple.set("FNAME", tempAfter)) {
						result.add(new RepairedCell(Integer.valueOf(tuple.getValue(0)), "FNAME", tempAfter));
					}
				} else {
					tuple.set("FNAME", "null");
				}
			}

			// LNAME:Last name,可能包含字母,逗号及句号,首字母大写
			temp = tuple.getValue("LNAME");
			isNum = pattern.matcher(temp);
			if (!isNum.matches()) {
				pattern = Pattern.compile("[A-Za-z\\,\\.]*");
				isNum = pattern.matcher(temp);
				// 如果只包含字母和符号
				if (isNum.matches()) {
					String tempAfter = temp.toLowerCase();
					tempAfter = tempAfter.replaceFirst(tempAfter.substring(0, 1),
							tempAfter.substring(0, 1).toUpperCase());
					if (tuple.set("LNAME", tempAfter)) {
						result.add(new RepairedCell(Integer.valueOf(tuple.getValue(0)), "LNAME", tempAfter));
					}
				} else {
					tuple.set("LNAME", "null");
				}
			}

			// MINIT:中间名缩写,可为空,或为1位大写字母
			pattern = Pattern.compile("[A-Z]?");
			temp = tuple.getValue("MINIT");
			isNum = pattern.matcher(temp);
			if (!isNum.matches()) {
				for (int i = 0; i < temp.length(); i++) {
					char c = temp.charAt(i);
					if (c >= 'a' && c <= 'z') {
						String temp1 = temp.substring(i, i + 1).toUpperCase();
						if (tuple.set("MINIT", temp1)) {
							result.add(new RepairedCell(Integer.valueOf(tuple.getValue(0)), "MINIT", temp1));
						}
						break;
					} else if (c >= 'A' && c <= 'Z') {
						String temp1 = temp.substring(i, i + 1);
						if (tuple.set("MINIT", temp1)) {
							result.add(new RepairedCell(Integer.valueOf(tuple.getValue(0)), "MINIT", temp1));
						}
						break;
					}
				}
			}

			// STADD:街道名,可能包含字母,空格,逗号及句号,或者为“PO Box xxxx”,其中"xxxx"为1-4位纯数字.若为“PO
			// Box xxxx”,则STNUM和APMT属性皆为空
			pattern = Pattern.compile("PO Box [0-9]{1,4}");
			isNum = pattern.matcher(tuple.getValue("STADD"));
			if (tuple.getValue("STNUM").length() == 0 && tuple.getValue("APMT").length() == 0 && !isNum.matches()) {
				tuple.set("STADD", "null");
			}
			isNum = pattern.matcher(tuple.getValue("STADD"));
			if (isNum.matches()) {
				if (tuple.getValue("STNUM").length() > 0) {
					if (tuple.set("STNUM", "")) {
						result.add(new RepairedCell(Integer.valueOf(tuple.getValue(0)), "STNUM", ""));
					}
				}
				if (tuple.getValue("APMT").length() > 0) {
					if (tuple.set("APMT", "")) {
						result.add(new RepairedCell(Integer.valueOf(tuple.getValue(0)), "APMT", ""));
					}
				}
			} else if (!isNum.matches() && (!tuple.getValue("STADD").equals("null"))) {
				pattern = Pattern.compile("[A-Za-z\\s\\,\\.]+");
				isNum = pattern.matcher(tuple.getValue("STADD"));
				if (!isNum.matches()) {
					tuple.set("STADD", "null");
					// isNum=pattern.matcher(tuple.getValue("STADD"));
				}
				pattern = Pattern.compile("[0-9][a-z][0-9]");
				String str = tuple.getValue("APMT");

				isNum = pattern.matcher(str);
				if (!isNum.matches()) {
					if (str.length() == 3) {
						String f = str.substring(0, 1);
						String m = str.substring(1, 2);
						String l = str.substring(2, 3);
						if (f.charAt(0) >= 'a' && f.charAt(0) <= 'z')
							str = m + f + l;
						if (l.charAt(0) >= 'a' && l.charAt(0) <= 'z')
							str = f + l + m;
						if (tuple.set("APMT", str.toLowerCase())) {
							result.add(new RepairedCell(Integer.valueOf(tuple.getValue(0)), "APMT", str.toLowerCase()));
						}
					} else
						tuple.set("APMT", "null");
					// result.add(new
					// RepairedCell(Integer.valueOf(tuple.getValue(0)),
					// "APMT", str));
				}

			}

			// CITY:城市名,可能包含字母+五种标点符号'-/. (空格也算一种)
			pattern = Pattern.compile("[a-zA-Z// //'//./////-]+");
			temp = tuple.getValue("CITY");
			isNum = pattern.matcher(temp);
			String tempAfter = new String();
			if (!isNum.matches()) {
				pattern = Pattern.compile(cropChar);
				isNum = pattern.matcher(temp);
				tempAfter = isNum.replaceAll("").trim();
				if (tempAfter.equals(temp)) {
					tuple.set("CITY", "null");
				} else {
					if (tuple.set("CITY", tempAfter)) {
						result.add(new RepairedCell(Integer.valueOf(tuple.getValue(0)), "CITY", tempAfter));
					}
				}
			}

			// STATE:所在州缩写,为美国的50个州的简称
			ArrayList<String> up = new ArrayList<String>(Arrays.asList("VT", "RI", "HI", "NY", "NV", "TN", "CA", "VI",
					"OK", "ME", "VA", "OH", "DE", "ID", "WY", "FM", "IA", "FL", "MD", "MA", "SD", "SC", "AR", "UT",
					"IL", "IN", "CT", "WV", "MN", "DC", "AZ", "KY", "MO", "KS", "OR", "MT", "LA", "GU", "NH", "WA",
					"NJ", "PR", "NM", "AK", "TX", "CO", "PA", "NC", "ND", "NE", "MS", "GA", "WI"));
			ArrayList<String> low = new ArrayList<String>(Arrays.asList("tn", "ca", "vi", "vt", "ri", "hi", "ny", "nv",
					"de", "id", "wy", "fm", "ia", "fl", "md", "ma", "ok", "me", "va", "oh", "mn", "dc", "az", "ky",
					"mo", "ks", "or", "mt", "sd", "sc", "ar", "ut", "il", "in", "ct", "wv", "ak", "tx", "co", "pa",
					"nc", "nd", "ne", "la", "gu", "nh", "wa", "nj", "pr", "nm", "ms", "ga", "wi"));
			pattern = Pattern.compile("[A-Z][A-Z]");
			temp = tuple.getValue("STATE");
			if (low.contains(temp)) {
				if (tuple.set("STATE", temp.toUpperCase())) {
					result.add(new RepairedCell(Integer.valueOf(tuple.getValue(0)), "STATE", temp.toUpperCase()));
				}
			} else if (!up.contains(temp)) {
				tuple.set("STATE", "null");
			}

		}
		return result;
	}
}

package dqcup.repair.impl;

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
				// result.add(new
				// RepairedCell(Integer.valueOf(tuple.getValue(0)), "SSN",
				// tuple.getValue("SSN")));
			}

			// FNAME:First name,可能包含字母,逗号及句号,首字母大写
			pattern = Pattern.compile("^[A-Z][A-Za-z\\,\\.]*");
			temp = tuple.getValue("FNAME");
			isNum = pattern.matcher(temp);
			if (!isNum.matches()) {
				char c = temp.charAt(0);
				if (c >= 'a' && c <= 'z') {
					temp = temp.replaceFirst(temp.substring(0, 1), temp.substring(0, 1).toUpperCase());
					if(tuple.set("FNAME", temp)){
						result.add(new RepairedCell(Integer.valueOf(tuple.getValue(0)), "FNAME", temp));	
					}
				} else {
					tuple.set("FNAME", "null");
					
				}
				// result.add(new
				// RepairedCell(Integer.valueOf(tuple.getValue(0)), "FNAME",
				// temp));
			}

			// LNAME:Last name,可能包含字母,逗号及句号,首字母大写
			temp = tuple.getValue("LNAME");
			isNum = pattern.matcher(temp);
			if (!isNum.matches()) {
				char c = temp.charAt(0);
				if (c >= 'a' && c <= 'z') {
					temp = temp.replaceFirst(temp.substring(0, 1), temp.substring(0, 1).toUpperCase());
					if(tuple.set("LNAME", temp)){
						result.add(new RepairedCell(Integer.valueOf(tuple.getValue(0)), "LNAME", temp));	
					}
				} else
					tuple.set("LNAME", "null");
				// result.add(new
				// RepairedCell(Integer.valueOf(tuple.getValue(0)), "LNAME",
				// temp));
			}

			// MINIT:中间名缩写,可为空,或为1位大写字母
			pattern = Pattern.compile("[A-Z]*");
			temp = tuple.getValue("MINIT");
			isNum = pattern.matcher(temp);
			if (!isNum.matches()) {
				char c = temp.charAt(0);
				if (c >= 'a' && c <= 'z') {
					temp = temp.substring(0, 1).toUpperCase();
					if (tuple.set("MINIT", temp)){
						result.add(new RepairedCell(Integer.valueOf(tuple.getValue(0)), "MINIT", temp));	
					}
				} else
					tuple.set("MINIT", "null");
				// result.add(new
				// RepairedCell(Integer.valueOf(tuple.getValue(0)), "MINIT",
				// temp));

			}

			// STADD:街道名,可能包含字母,空格,逗号及句号,或者为“PO Box xxxx”,其中"xxxx"为1-4位纯数字.若为“PO
			// Box xxxx”,则STNUM和APMT属性皆为空
			pattern = Pattern.compile("PO Box [0-9]{1,4}");
			isNum = pattern.matcher(tuple.getValue("STADD"));
			if (tuple.getValue("STNUM").length() == 0&&tuple.getValue("APMT").length() == 0&&!isNum.matches())
			{
				tuple.set("STADD", "null");
			}
			isNum = pattern.matcher(tuple.getValue("STADD"));
			if (isNum.matches()) {
				if (tuple.getValue("STNUM").length() > 0){
					if (tuple.set("STNUM", "")){
						result.add(new RepairedCell(Integer.valueOf(tuple.getValue(0)), "STNUM", ""));
					}
				}
				if (tuple.getValue("APMT").length() > 0){
					if (tuple.set("APMT", "")){
						result.add(new RepairedCell(Integer.valueOf(tuple.getValue(0)), "APMT", ""));	
					}
				}
			} else if(!isNum.matches()&&tuple.getValue("STADD")!="null") {
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
							str = m + f+ l;
						if (l.charAt(0) >= 'a' && l.charAt(0) <= 'z')
							str = f + l+ m;
						if (tuple.set("APMT", str.toLowerCase())){
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
			isNum = pattern.matcher(tuple.getValue("CITY"));
			if (!isNum.matches()) {
				// result.add(new
				// RepairedCell(Integer.valueOf(tuple.getValue(0)), "CITY",
				// tuple.getValue("CITY")));
				tuple.set("CITY", "null");
			}
			
			pattern = Pattern.compile("[A-Z][A-Z]");
			isNum = pattern.matcher(tuple.getValue("STATE"));
			if (!isNum.matches()) {
				// result.add(new
				// RepairedCell(Integer.valueOf(tuple.getValue(0)), "CITY",
				// tuple.getValue("CITY")));
				if (tuple.set("STATE", tuple.getValue("STATE").toUpperCase())){
					result.add(new RepairedCell(Integer.valueOf(tuple.getValue(0)), "STATE", tuple.getValue("STATE").toUpperCase()));	
				}
			}
		}
		return result;
	}
}

package dqcup.repair.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dqcup.repair.RepairedCell;
import dqcup.repair.Tuple;

public class FDImprov {
	public LinkedList<Tuple> tuples;

	public FDImprov(LinkedList<Tuple> tup) {
		this.tuples = tup;
	}

	public HashSet<RepairedCell> repair() {
		Collections.sort(tuples, new TupleCompare1()); // 排序
		HashSet<RepairedCell> result = new HashSet<RepairedCell>();
		RecordXY record = null;
		Iterator<Tuple> iterator = tuples.iterator();
		Tuple current = iterator.next();
		Tuple next = iterator.next();
		Matcher isNum;
		Pattern pattern;
		while (iterator.hasNext()) {
			current = next;
			next = iterator.next();
			
			String stadd = current.getValue("STADD");
			String stnum = current.getValue("STNUM");
			pattern = Pattern.compile("PO Box [0-9]{1,4}");
			isNum=pattern.matcher(stadd);
			StringBuilder sBuilder1 = new StringBuilder();
			StringBuilder sBuilder2 = new StringBuilder();
			if ( !isNum.matches() ){
				sBuilder1.append(stadd);
				sBuilder1.append(":");
				sBuilder1.append(stnum);
				sBuilder2.append(next.getValue("STADD")).append(":").append(next.getValue("STNUM"));
			}else{
				continue;
			}
			
//			StringBuilder sBuilder1 = new StringBuilder();
//			StringBuilder sBuilder2 = new StringBuilder();
//			if (current.getValue("STNUM") != null){
//				sBuilder1.append(current.getValue("STADD")).append(":").append(current.getValue("STNUM"));
//				sBuilder2.append(next.getValue("STADD")).append(":").append(next.getValue("STNUM"));
//			}else{
//				continue;
//			}
			
			boolean equal = sBuilder1.toString().equals(sBuilder2.toString());

			if (equal) {
				if (record == null) {
					record = new RecordXY();
				}
				addRecord(current, record);
			} else if (record != null) {
				addRecord(current, record);
				// 投票
				HashMap<String, ArrayList<String>> map = record.valueMap;
				if (map.size() > 1 && record.maxLength > 1) {
					Iterator iter = map.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry entry = (Map.Entry) iter.next();
						ArrayList<String> val = (ArrayList<String>) entry.getValue();
						String key = (String) entry.getKey();
						if (val.size() < record.maxLength || key.equals("null")) {
							for (String str : val) {
								int RUID = Integer.parseInt(str);
								result.add(new RepairedCell(RUID, "ZIP", record.maxKey));
							}
						}
					}
				}
				// 刷新
				record = null;
			} else {
				continue;
			}
		}
		return result;
	}

	private void addRecord(Tuple current, RecordXY record) {
		// 建立value->RUID_List对
		String value = current.getValue("ZIP");
		// 如果value在key中已经存在了， 就直接把RUID加入对应的list
		if (record.valueMap.containsKey(value)) {
			ArrayList<String> list = record.valueMap.get(value);
			list.add(current.getValue("RUID"));
			if ((!value.equals("null")) && list.size() > record.maxLength) {
				record.maxLength = list.size();
				record.maxKey = value;
			}
		} else {
			ArrayList<String> list = new ArrayList<String>();
			list.add(current.getValue("RUID"));
			// 将<value, list>对存放到对应的map中
			record.valueMap.put(value, list);
			if ((!value.equals("null")) && record.maxLength == 0) {
				record.maxLength = 1;
				record.maxKey = value;
			}
		}
	}
}


class TupleCompare1 implements Comparator<Tuple> {
	private Matcher isNum1, isNum2;
	private Pattern pattern;
	@Override
	public int compare(Tuple o1, Tuple o2) {
		pattern = Pattern.compile("PO Box [0-9]{1,4}");
		String stadd1 = o1.getValue("STADD");
		String stnum1 = o1.getValue("STNUM");
		String stadd2 = o2.getValue("STADD");
		String stnum2 = o2.getValue("STNUM");
		isNum1 = pattern.matcher(stadd1);
		isNum2 = pattern.matcher(stadd2);
		
		StringBuilder sBuilder2 = new StringBuilder();
		StringBuilder sBuilder1 = new StringBuilder();
		
		if ( !isNum2.matches()){
			sBuilder2.append(o2.getValue("STADD")).append(":").append(o2.getValue("STNUM"));
		}else{
			sBuilder2.append("");
		}
		if (!isNum1.matches()){
			sBuilder1.append(o1.getValue("STADD")).append(":").append(o1.getValue("STNUM"));
		}else{
			sBuilder1.append("");
		}
		
		return (sBuilder2.toString().compareTo(sBuilder1.toString()));
	}
}

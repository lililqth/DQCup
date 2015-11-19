package dqcup.repair.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import dqcup.repair.RepairedCell;
import dqcup.repair.Tuple;

public class FD3 {
	public LinkedList<Tuple> tuples;

	public FD3(LinkedList<Tuple> tup) {
		this.tuples = tup;
	}

	public HashSet<RepairedCell> repair() {
		Collections.sort(tuples, new TupleCompare_FNAME_STNUM()); // 排序

		HashSet<RepairedCell> result = new HashSet<RepairedCell>();
		RecordXY record = null;
		Iterator<Tuple> iterator = tuples.iterator();
		Tuple current = iterator.next();
		Tuple next = iterator.next();
		while (iterator.hasNext()) {
			current = next;
			next = iterator.next();

			StringBuilder sBuilder2 = new StringBuilder();
			StringBuilder sBuilder1 = new StringBuilder();
			sBuilder2.append(current.getValue("FNAME")).append(current.getValue("STNUM"));
					
			sBuilder1.append(next.getValue("FNAME")).append(next.getValue("STNUM"));
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
								result.add(new RepairedCell(RUID, "APMT", record.maxKey));
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
		String value = current.getValue("APMT");
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


class TupleCompare_FNAME_STNUM implements Comparator<Tuple> {
	@Override
	public int compare(Tuple o1, Tuple o2) {
		StringBuilder sBuilder2 = new StringBuilder();
		StringBuilder sBuilder1 = new StringBuilder();
		sBuilder2.append(o2.getValue("FNAME")).append(o2.getValue("STNUM"));
		sBuilder1.append(o1.getValue("FNAME")).append(o1.getValue("STNUM"));
		return (sBuilder2.toString().compareTo(sBuilder1.toString()));
	}
}

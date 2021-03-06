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

public class FD {
	public LinkedList<Tuple> tuples;

	public FD(LinkedList<Tuple> tup) {
		this.tuples = tup;
	}

	public HashSet<RepairedCell> repair() {
		Collections.sort(tuples, new TupleCompare()); // 排序

		HashSet<RepairedCell> result = new HashSet<RepairedCell>();
		RecordXY record = null;
		Iterator<Tuple> iterator = tuples.iterator();
		Tuple current = null;
		Tuple next = iterator.next();
		while (iterator.hasNext()) {
			current = next;
			next = iterator.next();

			StringBuilder sBuilder2 = new StringBuilder();
			StringBuilder sBuilder1 = new StringBuilder();
			sBuilder2.append(current.getValue("FNAME")).append(current.getValue("LNAME"));
			sBuilder1.append(next.getValue("FNAME")).append(next.getValue("LNAME"));
			boolean equal = sBuilder1.toString().equals(sBuilder2.toString());

			if (equal) {
				if (record == null) {
					record = new RecordXY();
				}
				addRecord(current, record);
				if (!iterator.hasNext()) {
					addRecord(next, record);
				}
			}
			if (((!equal) && record != null) || (equal && !iterator.hasNext())) {
				if (!equal) {
					addRecord(current, record);
				}
				// 投票

				HashMap<String, ArrayList<String>> map = record.valueMap;
				if (map.size() > 1 && record.maxLength >= 1) {
					Iterator iter = map.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry entry = (Map.Entry) iter.next();
						ArrayList<String> val = (ArrayList<String>) entry.getValue();
						String key = (String) entry.getKey();

						if (val.size() < record.maxLength || key.equals("null")) {
							for (String str : val) {
								int RUID = Integer.parseInt(str);

								// 将这个人的最后一条记录修改过来
								Tuple personLastRecord = record.tupleMap.get(str);
								if (personLastRecord.set("ZIP", record.maxKey)) {
									result.add(new RepairedCell(RUID, "ZIP", record.maxKey));
								}
								// 将这个人的每一条记录全部修改过来
								for (int i = 1; i <= personLastRecord.number; i++) {
									if (Tuple.set(String.valueOf(RUID - i), "ZIP", record.maxKey)) {
										result.add(new RepairedCell(RUID - i, "ZIP", record.maxKey));
									}
								}

							}
						}
					}
				}
				// 刷新
				record = null;
			}
		}
		return result;
	}

	private void addRecord(Tuple current, RecordXY record) {
		// 建立value->RUID_List对
		String value = current.getValue("ZIP");
		// 如果value在key中已经存在了， 就直接把RUID加入对应的list
		String RUID = current.getValue("RUID");
		if (record.valueMap.containsKey(value)) {
			ArrayList<String> list = record.valueMap.get(value);
			list.add(RUID);
			record.tupleMap.put(RUID, current);
			if ((!value.equals("null")) && list.size() > record.maxLength) {
				record.maxLength = list.size();
				record.maxKey = value;
			}
		} else {
			ArrayList<String> list = new ArrayList<String>();
			list.add(RUID);
			record.tupleMap.put(RUID, current);
			// 将<value, list>对存放到对应的map中
			record.valueMap.put(value, list);
			if ((!value.equals("null")) && record.maxLength == 0) {
				record.maxLength = 1;
				record.maxKey = value;
			}
		}
	}
}

class RecordXY {
	public HashMap<String, ArrayList<String>> valueMap = new HashMap<String, ArrayList<String>>();
	public HashMap<String, Tuple> tupleMap = new HashMap<String, Tuple>();
	public int maxLength; // 字段的最高票数
	public String maxKey;// 字段最高票数对应的key

	public RecordXY() {
		valueMap = new HashMap<String, ArrayList<String>>();
	}
}

class TupleCompare implements Comparator<Tuple> {
	@Override
	public int compare(Tuple o1, Tuple o2) {
		StringBuilder sBuilder2 = new StringBuilder();
		StringBuilder sBuilder1 = new StringBuilder();
		sBuilder2.append(o2.getValue("FNAME")).append(o2.getValue("LNAME"));
		sBuilder1.append(o1.getValue("FNAME")).append(o1.getValue("LNAME"));
		return (sBuilder2.toString().compareTo(sBuilder1.toString()));
	}
}

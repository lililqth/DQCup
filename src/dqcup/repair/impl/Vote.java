package dqcup.repair.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import dqcup.repair.RepairedCell;
import dqcup.repair.Tuple;

public class Vote {
	public LinkedList<Tuple> tuples;
	private String[] itemNameList = { "RUID", "CUID", "SSN", "FNAME", "MINIT", "LNAME", "STNUM", "STADD", "APMT",
			"CITY", "STATE", "ZIP" };
	HashSet<RepairedCell> result;

	public Vote(LinkedList<Tuple> tup) {
		this.tuples = tup;
	}

	public HashSet<RepairedCell> repair() {
		result = new HashSet<RepairedCell>();
		Record record = new Record();
		Iterator<Tuple> iterator = tuples.iterator();
		Tuple current = null;

		Tuple next = iterator.next();
		while (iterator.hasNext()) {
			current = next;
			next = iterator.next();
			boolean equal = current.getValue("CUID").equals(next.getValue("CUID"));
			if (equal) {
				if (record == null) {
                    record = new Record();
                }
				addRecord(current, record);
				if (!iterator.hasNext()) {
					addRecord(next, record);
				}
			}
			if (((!equal) && record != null) || (equal && !iterator.hasNext())){
				if (!equal) {
                    addRecord(current, record);
                }
				// 投票
				for (int i = 0; i < 10; i++) {
					HashMap<String, ArrayList<String>> map = record.valueMap[i];
					if (map.size() >= 1 && record.maxLength[i] >= 1) {

						Iterator iter = map.entrySet().iterator();
						while (iter.hasNext()) {
							Map.Entry entry = (Map.Entry) iter.next();
							ArrayList<String> val = (ArrayList<String>) entry.getValue();
							String key = (String) entry.getKey();
							if (val.size() < record.maxLength[i] || key.equals("null")) {
								for (String str : val) {
									int RUID = Integer.parseInt(str);
									Tuple tup = record.tupleMap.get(str);
									String name = this.itemNameList[i + 2];
									if (tup.set(name, record.maxKey[i])) {
										result.add(new RepairedCell(RUID, name, record.maxKey[i]));
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

	// 在 Record 记录表中添加一条记录
	private void addRecord(Tuple current, Record record) {
		// 建立value->RUID_List对，对每一个tuple的10个属性进行处理
		String RUID = current.getValue("RUID");
		record.tupleMap.put(RUID, current);
		for (int i = 2; i < 12; i++) {
			String value = current.getValue(this.itemNameList[i]);
			// 如果value在key中已经存在了， 就直接把RUID加入对应的list
			if (record.valueMap[i - 2].containsKey(value)) {
				ArrayList<String> list = (ArrayList<String>) record.valueMap[i - 2].get(value);
				list.add(current.getValue("RUID"));
				if ((!value.equals("null")) && list.size() > record.maxLength[i - 2]) {
					record.maxLength[i - 2] = list.size();
					record.maxKey[i - 2] = value;
				}
			} else {
				ArrayList<String> list = new ArrayList<String>();
				list.add(current.getValue("RUID"));
				// 将<value, list>对存放到对应的map中
				record.valueMap[i - 2].put(value, list);
				if ((!value.equals("null")) && record.maxLength[i - 2] == 0) {
					record.maxLength[i - 2] = 1;
					record.maxKey[i - 2] = value;
				}
			}
		}
	}
}

class Record {
	public HashMap[] valueMap = new HashMap[10];
	public int[] maxLength; // 每一个字段的最高票数
	public String[] maxKey;// 每一个字段最高票数对应的key
	public HashMap<String, Tuple> tupleMap = new HashMap<String, Tuple>();

	public Record() {
		for (int i = 0; i < 10; i++) {
			valueMap[i] = new HashMap<String, ArrayList<String>>();
		}
		maxLength = new int[10];
		maxKey = new String[10];
	}
}
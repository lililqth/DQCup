package dqcup.repair.impl;

import dqcup.repair.RepairedCell;
import dqcup.repair.Tuple;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FD1 {
	public LinkedList<Tuple> tuples;
	private HashSet<YRecord> XSet= new HashSet<YRecord>();
	private String[] itemNameList = { "RUID", "CUID", "SSN", "FNAME", "MINIT", "LNAME", "STNUM", "STADD", "APMT",
			"CITY", "STATE", "ZIP" };

	public FD1(LinkedList<Tuple> tup) {
		this.tuples = tup;
	}

	private void generateXSet()
	{
		Matcher isNum;
		Pattern pattern;
		for (Tuple tuple : tuples) {
			// 有两种方式可以获取Tuple中某一列的值，用列的index(起始为第0列)或是用列名均可
			
			String stadd = tuple.getValue("STADD");
			String stnum = tuple.getValue("STNUM");
			pattern = Pattern.compile("PO Box [0-9]{1,4}");
			isNum=pattern.matcher(stadd);
			StringBuilder sBuilder = new StringBuilder();
			if ( !isNum.matches() ){
				sBuilder.append(stnum);
				sBuilder.append(":");
				sBuilder.append(stadd);
			}else{
				continue;
			}
			String Name = sBuilder.toString();
			
			YRecord record = new YRecord(Name);
			if (!XSet.contains(record)) {
				// value所在的行数，也就是RUID存放到list中
				ArrayList<String> list = new ArrayList<String>();
				list.add(tuple.getValue("RUID"));

				// 将<value, list>对存放到对应的map中
				String value = tuple.getValue("ZIP");
				record.ZipCodeMap.put(value, list);
				XSet.add(record);
			} else {
				//X(Name)在XSet中已经存在
				//找到XSet中的位置
				YRecord Item = null;
				for (YRecord item : XSet) {
					if (item.X.equals(Name))
					{
						Item = item;
						break;
					}
				}

				// 将行号加入到Name所对应的list(zipcode， RUID)中。。。。。Name->多个zipcode->多个RUID.
				// 获取了ZipCode的值
				String value = tuple.getValue("ZIP");
				// 如果值在map中，在map对应的list中添加一个RUID
				if (Item.ZipCodeMap.containsKey(value)){
					ArrayList<String> list = (ArrayList<String>)Item.ZipCodeMap.get(value);
					list.add(tuple.getValue("RUID"));
					if (Item.maxLength < list.size()){
						Item.maxLength = list.size();
						Item.maxKey = value;
					}
				}else{
					// 如果值不在map中，在map中添加一项
					ArrayList<String> list = new ArrayList<String>();
					list.add(tuple.getValue("RUID"));
					// 将<value, list>对存放到对应的map中
					Item.ZipCodeMap.put(value, list);
					if (Item.maxLength == 0){
						Item.maxLength = 1;
						Item.maxKey = value;
					}
				}
			}
		}
	}

	//根据建立好的XSet进行投票
	private HashSet<RepairedCell> vote()
	{
		HashSet<RepairedCell> result = new HashSet<RepairedCell>();
		// 对每一个X的数据进行检查
		for (YRecord record : XSet)
		{
			if (record.maxLength <= 1){
				continue;
			}
			HashMap<String, ArrayList<String>> map = record.ZipCodeMap;
			if(map.size() > 1)
			{
				Iterator iter = map.entrySet().iterator();
				int maxNum = 0;
				// 将票数较低的元素全部改过来
				iter = map.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					ArrayList<String> val = (ArrayList<String>) entry.getValue();
					String key = (String) entry.getKey();
					if (val.size() < record.maxLength || key.equals("null"))
					{
						for (String str : val)
						{
							int RUID = Integer.parseInt(str);
							result.add(new RepairedCell(RUID, "ZIP", record.maxKey));
						}
					}
				}
			}
		}
		return result;
	}

	public HashSet<RepairedCell> repair() {
		generateXSet();
		return vote();
	}
}

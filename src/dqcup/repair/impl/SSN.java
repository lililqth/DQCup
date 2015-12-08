package dqcup.repair.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import dqcup.repair.RepairedCell;
import dqcup.repair.Tuple;

public class SSN {
	public LinkedList<Tuple> tuples;

	public SSN(LinkedList<Tuple> tup) {
		this.tuples = tup;
	}
	public HashSet<RepairedCell> SetSSN()
	{
		HashSet<RepairedCell> result = new HashSet<RepairedCell>();
		HashMap<String,ArrayList<String>> count=new HashMap<String,ArrayList<String>>();
		for(Tuple tup:tuples)
		{
			String temp=tup.getValue("SSN");
			if(!temp.equals("000000000")&&!temp.equals("null"))
			{
				String cuid=tup.getValue("CUID");
				if(count.containsKey(temp))
				{
					count.get(temp).add(cuid);
				}
				else
				{
					ArrayList<String> first=new ArrayList<String>();
					first.add(cuid);
					count.put(temp, first);
				}
			}
		}//ssn对于cuid
		HashMap<String,String> cor=new HashMap<String,String>();//cuid 对应ssn
		Iterator iter = count.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			ArrayList<String> list = (ArrayList<String>) entry.getValue();
			String key = (String) entry.getKey();
			HashSet<String> dif=new HashSet<String>();
			dif.addAll(list);
			if(dif.size()==1)
				cor.put(list.get(0),key);
			else if(dif.size()>1)
			{
				int max=0;
				int temp=0;
				String real=null;
				for(String no: dif) 
				{
					temp=Collections.frequency(list, no);
					if(temp>max)
					{
						real=no;
						max=temp;
					}
						
				}
				cor.put(real,key);
			}
		}
		
		for(Tuple tup:tuples)
		{
			String CUID=tup.getValue("CUID");
			String SSN=tup.getValue("SSN");
			if(cor.containsKey(CUID))
			{
				String RealSSN=cor.get(CUID);
				if(!RealSSN.equals(SSN)){
					if (tup.set("SSN", RealSSN)){
						result.add(new RepairedCell(Integer.parseInt(tup.getValue("RUID")), "SSN", RealSSN));
					}
				}
			}
		}
		return result;
	}
}

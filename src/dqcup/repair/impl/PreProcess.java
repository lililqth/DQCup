package dqcup.repair.impl;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import dqcup.repair.Tuple;

public class PreProcess {
	public LinkedList<Tuple> tuples;
	public PreProcess(LinkedList<Tuple> tup){
		this.tuples = tup;
	}
	public LinkedList<Tuple> Process(){
		Iterator<Tuple> iterator = tuples.iterator();
		Tuple current = null;
	
		Tuple next = iterator.next();
		LinkedList<Tuple> newTuple = new LinkedList<Tuple>();
		String curCUID = "", nextCUID = "";
		while (iterator.hasNext()) {
			current = next;
			next = iterator.next();
			curCUID = current.getValue("CUID");
			nextCUID = next.getValue("CUID");
			// 记录同一个人有几条记录
			if (curCUID.equals(nextCUID)){
				next.number = current.number + 1;
			}else{
				newTuple.add(current);
			}
		}
		
		newTuple.add(next);
		
		 
		/*
		PrintStream ps;
		try {
			ps = new PrintStream("/Users/qiji/Desktop/easy.txt");
			System.setOut(ps);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
		System.out.println("RUID:CUID:SSN:FNAME:MINIT:LNAME:STNUM:STADD:APMT:CITY:STATE:ZIP");
		for(iterator = newTuple.iterator();iterator.hasNext();){
			current = iterator.next();
			StringBuilder sBuilder = new StringBuilder();
			sBuilder.append(current.getValue("RUID"));
			sBuilder.append(":");
			sBuilder.append(current.getValue("CUID"));
			sBuilder.append(":");
			sBuilder.append(current.getValue("SSN"));
			sBuilder.append(":");
			sBuilder.append(current.getValue("FNAME"));
			sBuilder.append(":");
			sBuilder.append(current.getValue("MINIT"));
			sBuilder.append(":");
			sBuilder.append(current.getValue("LNAME"));
			sBuilder.append(":");
			sBuilder.append(current.getValue("STNUM"));
			sBuilder.append(":");
			sBuilder.append(current.getValue("STADD"));
			sBuilder.append(":");
			sBuilder.append(current.getValue("APMT"));
			sBuilder.append(":");
			sBuilder.append(current.getValue("CITY"));
			sBuilder.append(":");
			sBuilder.append(current.getValue("STATE"));
			sBuilder.append(":");
			sBuilder.append(current.getValue("ZIP"));
			System.out.println(sBuilder.toString());
		}
		*/
		return newTuple;
	}
}

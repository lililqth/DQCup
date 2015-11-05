package dqcup.repair.impl;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import dqcup.repair.DatabaseRepair;
import dqcup.repair.DbFileReader;
import dqcup.repair.RepairedCell;
import dqcup.repair.Tuple;

public class DatabaseRepairImpl implements DatabaseRepair {

	@Override
	public Set<RepairedCell> repair(String fileRoute) {
		//Please implement your own repairing methods here.
		LinkedList<Tuple> tuples = DbFileReader.readFile(fileRoute);
		
		HashSet<RepairedCell> result = new HashSet<RepairedCell>();
		
		// 正则表达式
//		RegEx reg=new RegEx(tuples);
//		result=reg.verify(result);
		
		
		Vote vote = new Vote(tuples);
		result.addAll(vote.repair());
		
		// name决定zip
//		FD fd = new FD(tuples);
//		result.addAll(fd.repair());
		
		// stadd决定zip
//		FD1 fd1 = new FD1(tuples);
//		result.addAll(fd1.repair());
		
		
		
		return result;
	}
}

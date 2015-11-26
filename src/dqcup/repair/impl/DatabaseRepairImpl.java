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
		// Please implement your own repairing methods here.
		LinkedList<Tuple> tuples = DbFileReader.readFile(fileRoute);

		HashSet<RepairedCell> result = new HashSet<RepairedCell>();

		// 正则表达式
		RegEx reg = new RegEx(tuples);
		result = reg.verify(result);

		// 进行单人投票
		Vote vote = new Vote(tuples);
		result.addAll(vote.repair());
		PreProcess PP = new PreProcess(tuples);
		tuples = PP.Process();
		
		// name决定zip
		FD fdim = new FD(tuples);
		result.addAll(fdim.repair());

		// stadd， CITY决定zip
		FD1 fd1 = new FD1(tuples);
		result.addAll(fd1.repair());

		// APMT ZIP 决定 STATE 没有效果
		FD2 fd2 = new FD2(tuples);
		result.addAll(fd2.repair());

		// FNAME STNUM 决定 APMT
		FD3 fd3 = new FD3(tuples);
		result.addAll(fd3.repair());

		return result;
	}
}

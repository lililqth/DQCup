package dqcup.repair.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import dqcup.repair.RepairedCell;
import dqcup.repair.Tuple;

public class AddNull {
	public LinkedList<Tuple> tuples;
	private String[] itemNameList = { "RUID", "CUID", "SSN", "FNAME", "MINIT", "LNAME", "STNUM", "STADD", "APMT",
			"CITY", "STATE", "ZIP" };
	HashSet<RepairedCell> result;

	public AddNull(LinkedList<Tuple> tup) {
		this.tuples = tup;
	}

	public HashSet<RepairedCell> repair() {
		result = new HashSet<RepairedCell>();
		Iterator<Tuple> iterator = tuples.iterator();
		Tuple current = null;
		while (iterator.hasNext()) {
			current = iterator.next();
			for (int i = 0; i < 10; i++) {
				String name = this.itemNameList[i + 2];
				String value = current.getValue(name);

				if (value.equals("null")) {
					int RUID = Integer.parseInt(current.getValue("RUID"));
					for (int j = 0; j <= current.number; j++) {
						result.add(new RepairedCell(RUID - j, name, "null"));
					}
				}
			}
		}
		return result;
	}
}
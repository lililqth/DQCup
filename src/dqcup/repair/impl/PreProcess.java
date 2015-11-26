package dqcup.repair.impl;

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
		Tuple current = iterator.next();
		Tuple next = iterator.next();
		LinkedList<Tuple> newTuple = new LinkedList<Tuple>(); 
		while (iterator.hasNext()) {
			current = next;
			next = iterator.next();
			String curCUID = current.getValue("CUID");
			String nextCUID = next.getValue("CUID");
			if (!curCUID.equals(nextCUID)){
				newTuple.add(current);
			}
		}
		return newTuple;
	}
}

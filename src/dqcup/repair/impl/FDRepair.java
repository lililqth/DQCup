package dqcup.repair.impl;

import java.util.ArrayList;
import java.util.LinkedList;

import dqcup.repair.Tuple;

class HyperEdge{
	public ArrayList<Point> pointList;
	public HyperEdge(ArrayList<Point> points){
		pointList = points;
	}
	public void addPoint(Point point){
		pointList.add(point);
	}
}

class Point{
	public String x, y;
}
public class FDRepair {
	public LinkedList<Tuple> tuples;
	
	public FDRepair(LinkedList<Tuple> tuples){
		this.tuples = tuples;
	}
	public static void main(String[] args){
		
	}
	public ArrayList<Point> find(ArrayList<HyperEdge> hyperEdges){
		ArrayList<Point> ans = new ArrayList<Point>();
		return ans;
		
	}
	
	
}

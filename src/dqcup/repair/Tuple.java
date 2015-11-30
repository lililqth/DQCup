package dqcup.repair;

import java.util.HashMap;
import java.util.HashSet;

public class Tuple {
	private ColumnNames columnNames;
	private HashMap<String, String> cells;
	public static HashSet<String> isModified;
	public int number;
	
	public HashMap<String, String> getCells() {
		return cells;
	}
	
	public boolean set(String key, String value){
		String RUID = this.getCells().get("RUID");
		boolean judge = isModified.contains(RUID+key);
		if (judge == false){
			cells.put(key, value);
			if (!value.equals("null")){
				isModified.add(RUID+key);
			}
			return true;
		}
		return false;
	}
	
	public void setCells(HashMap<String, String> cells) {
		this.cells = cells;
	}

	public Tuple(ColumnNames columnNames, String tupleLine){
		cells = new HashMap<String,String>();
		isModified = new HashSet<String>();
		this.setColumnNames(columnNames);
		String[] cellValues = tupleLine.split(":");
		for(int i = 0 ; i < cellValues.length ; i++){
			cells.put(columnNames.get(i), cellValues[i]);
		}
		number = 0;
	}
	
	public String toString(){
		return cells.toString();
	}
	
	public String getValue(String columnName){
		return cells.get(columnName);
	}
	
	public String getValue(int columnIndex){
		String columnName = columnNames.get(columnIndex);
		return getValue(columnName);
	}

	/**
	 * @return the columnNames
	 */
	public ColumnNames getColumnNames() {
		return columnNames;
	}

	/**
	 * @param columnNames the columnNames to set
	 */
	private void setColumnNames(ColumnNames columnNames) {
		this.columnNames = columnNames;
	}
	
}

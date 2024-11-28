package vMem;

import java.util.Arrays;

public class Process {
	private String pID;
	int[] pageTable;
	int[]invertedPageTable;
	
	public Process(String id,int numPages) {
		pID=id;
		this.pageTable = new int[numPages];
		Arrays.fill(pageTable, -1);
	
	}
	
	public String printPageTable() {
		String output ="The page table currently:\n Page#  ";
		for(int i=0;i<pageTable.length;i++) {
			output+= i +"  ";
		}
		output+="\n Frame# ";
		for(int j: pageTable) {
			output+= j + "  ";
		}
		
		
		return output;
	}
	public String getID() {
		return pID;
	}
	
}

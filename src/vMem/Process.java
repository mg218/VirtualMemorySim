package vMem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Process {
	private String pID;
	int[] pageTable;
	int[]invertedPageTable;
	List<Integer> pageRef = new ArrayList<Integer>();
	private int quantum;
	private int numPages;
	
	public Process(String id,int numPages,int quantum) {
		pID=id;
		this.quantum=quantum;
		this.pageTable = new int[numPages];
		Arrays.fill(pageTable, -1);
		this.numPages=numPages;
	
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
	public void fillList() {
		for(int i=0;i<quantum;i++) {
			pageRef.add((int)(Math.random()*numPages));
		}
	}
	
}

package vMem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Process {
	private String pID;
	int[] pageTable;
	int[]invertedPageTable;
	List<Integer> pageRef = new ArrayList<Integer>();
	private int numPages;
	boolean active;
	
	public Process(String id,int numPages) {
		pID=id;
		this.pageTable = new int[numPages];
		Arrays.fill(pageTable, -1);
		this.numPages=numPages;
		active = false;
	
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
	public void fillList(int quantum) {
		for(int i=0;i<quantum;i++) {
			pageRef.add((int)(Math.random()*numPages));
		}
	}
	public boolean getActive() {
		return active;
	}
	
}

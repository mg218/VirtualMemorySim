package vMem;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Driver {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter the number of pages: ");
		int noPages = sc.nextInt();
		System.out.print("Enter the number of Frames: ");
		int noFrames = sc.nextInt();
		System.out.print("Enter the tlb sized: ");
		int tlbSize = sc.nextInt();
		System.out.println("How many processes are there?:");
		int numProcs=sc.nextInt();
		List<Process> procList = new ArrayList<Process>();
		for (int i = 0; i < numProcs; i++) {
			procList.add(new Process("P"+i,noPages,4));
		}
		VirtualMemory mmu= new VirtualMemory(noPages,noFrames,tlbSize,procList);
		
		
		for(int i=0;i<10;i++) {
			mmu.step();
		} 
	}

}

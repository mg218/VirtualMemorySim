package vMem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import vMem.ProcessEvent.type;

public class VirtualMemory {
	public static final int PAGE_SIZE = 256;
	public static final int FRAME_SIZE = PAGE_SIZE;
	public int numPages; // number of pages
	public int numFrames; // number of frames
	public int tlbSize; // number of entries in the TLB
	public List<Process> procList = new ArrayList<Process>();
	protected Process curProcess;
	private TLB_Entry[] tlb; // list of TLB entries
	private int nextLoadLocation = 0; // next frame index to load the new page
	private int next_free_tlb_index = 0; // index of next free TLB entry
	private int page_faults = 0; // number of page faults
	private int page_hits=0;
	private int tlb_hits = 0; // number of TLB hits
	private int pagerefs=0;//counts the times step() has been called
	private MEMORY_ENTRY[] memory; 
	public static final int QUANTUM=4;
	public Process oldProc;
	public int disk_access=0;

	public int getPage_faults() {
		return page_faults;
	}

	public int getTlb_hits() {
		return tlb_hits;
	}

	// constructor
	public VirtualMemory(int numPages, int numFrames, int tlbSize,List<Process> procList) {
		// TODO: add code below
		this.numPages = numPages;
		this.numFrames = numFrames;
		this.tlbSize = tlbSize;
		this.curProcess=procList.get(0);
		curProcess.active=true;
		curProcess.fillList(QUANTUM);
		this.procList=procList;
		memory=new MEMORY_ENTRY[numFrames];	
		for(int i=0;i<memory.length;i++) {
			memory[i]= new MEMORY_ENTRY(null,-1);
		}
		// set up the TLB
		tlb = new TLB_Entry[tlbSize];
		for (int i = 0; i < tlbSize; i++) {
			tlb[i] = new TLB_Entry(-1, -1, false);

		}

	}
	
	
	
	public List<ProcessEvent> step() {
		if(curProcess.pageRef.size()==QUANTUM) {
			resetTLB();
		}
		pagerefs++;
		List<ProcessEvent> events= new ArrayList<>();
		int page_number = curProcess.pageRef.get(0);
		int frameNumber= search_tlb(page_number);
		if(frameNumber>=0) {//hit
			System.out.println("TLB HIT: The page "+ page_number+ " has been loaded into the frame "+ frameNumber);
			tlb_hits++;
			events.add(new ProcessEvent(curProcess,type.TLBHIT ,page_number,frameNumber));
			
		}else {//miss
			//search entry on the page table
			disk_access++;
			events.add(new ProcessEvent(curProcess, type.TLBMISS,page_number,frameNumber));
			frameNumber=curProcess.pageTable[page_number];
			if(frameNumber>=0) {
				System.out.println("Page hit: The page "+ page_number+ " has been loaded into the frame "+ frameNumber);
				page_hits++;
				events.add(new ProcessEvent(curProcess,type.PAGEHIT,page_number,frameNumber));
				//update_tlb(page_number,frame);
			}else {//Page Fault
				page_faults++;
				events.add(new ProcessEvent(curProcess,type.PAGEFAULT,page_number,frameNumber));
				//try to find any free frame
				int frame=-1;
				for(int f=0;f<numFrames;f++) {
					if(memory[f].val==-1) {
						frame=f;
						break;
					}
				}
				if(frame<0) {
					frame=nextLoadLocation;
					nextLoadLocation=(nextLoadLocation+1)%numFrames;
				}
				//update page table
				frameNumber=frame;
				curProcess.pageTable[page_number]=frame;
				memory[frame].val=page_number;
				memory[frame].proc=curProcess;
				for(Process p: procList) {
					for(int i=0;i<p.pageTable.length;i++) {
						if(p==curProcess) {
							if(i!=page_number) {
								if(p.pageTable[i]==frame) {
									p.pageTable[i]=-1;
									events.add(new ProcessEvent(p, type.SAVE,i,frame));
								}
							}
						}else {
							if(p.pageTable[i]==frame) {
								p.pageTable[i]=-1;
								events.add(new ProcessEvent(p, type.SAVE,i,frame));
								
							}
						}
					}
				}
				update_tlb(page_number,frame);
				System.out.println("Page fault: The page "+ page_number+ " has been loaded into the frame "+ frame);
				
			}
			
		}
		curProcess.pageRef.remove(0);
		System.out.println("Number of TLB hits "+getTlb_hits());
		System.out.println("Number of Faults "+getPage_faults());
		System.out.println(printAllPageTable());
		System.out.println(printTlb());
		System.out.println(printMem());
		updateProc();
		return events;
	}

	// search if page number in tlb or not
	private int search_tlb(int pageNumber) {
		// TODO: add code below
		for (int i = 0; i < tlb.length; i++) {
			if (tlb[i].getPageNumber() == pageNumber)
				return tlb[i].getFrameNumber();

		}
		return -1;
	}

	// Updates the tlb so that it now contains a mapping of the specified
	// page number to frame number
	void update_tlb(int page_number, int frame_number) {
		// TODO: add code below
		tlb[next_free_tlb_index].setPageNumber(page_number);
		tlb[next_free_tlb_index].setFrameNumber(frame_number);
		tlb[next_free_tlb_index].setValid(true);
		next_free_tlb_index = (next_free_tlb_index + 1) % tlbSize;
	}

	// checks if the associated page number is in the table.
	// Returns -1 if page fault, >= 0 representing frame number if table hit.

	public String printTlb() {
		String output="The TLB currently: \n Page#  ";
		for(TLB_Entry t:tlb) {
			output+=t.getPageNumber()+"  ";
		}
		output+="\n Frame# ";
		for(TLB_Entry t:tlb) {
			output+=t.getFrameNumber()+"  ";
		}
		return output;
	}
	public String printAllPageTable() {
		String output="Page Tables: \n";
		for(Process p:procList) {
			output+= p.getID()+": \n"+p.printPageTable()+"\n";
		}
		
		return output;
		
	}
	public String printMem() {
		String output="Memory table: \nFrame#: ";
		for(int i=0;i<memory.length;i++) {
			output+= i+" ";
		}
		output+="\nProcess: ";
		for(MEMORY_ENTRY m:memory) {
			if(m.proc!=null) {
				output+=m.proc.getID()+" ";
			}else {
				output+=-1+" ";
			}
		}
		output+="\nValue: ";
		for (MEMORY_ENTRY m : memory) {
			output+=m.val+" ";
		}
		
		return output;
	}
	public void updateProc() {
		
		if(curProcess.pageRef.isEmpty()) {
			
			oldProc=curProcess;
			if(procList.indexOf(curProcess)==procList.size()-1) {
				curProcess=procList.get(0);
			}else {
				curProcess=procList.get(procList.indexOf(curProcess)+1);
			}
			curProcess.fillList(QUANTUM);
			oldProc.active=false;
			curProcess.active=true;
		}
		
	}
	public void resetTLB() {
		next_free_tlb_index=0;
		for(TLB_Entry e:tlb) {
			e.setFrameNumber(-1);
			e.setPageNumber(-1);
			e.setValid(false);
		}
	}
	public TLB_Entry[] getTLB() {
		return tlb;
	}
	public MEMORY_ENTRY[] getMemory() {
		return memory;
	}
	public int getTLBMisses() {
		return pagerefs-tlb_hits;
	}
	public int getTLBHits() {
		return tlb_hits;
	}
	public int getPagehits() {
		return page_hits;
	}
	public int getPageFaults() {
		return page_faults;
	}
	public int getDiskAccess() {
		return disk_access;
	}
	public boolean[] getProcessSate() {
		boolean[] actives = new boolean[procList.size()];
		for(int i=0;i<procList.size();i++) {
			actives[i]=procList.get(i).active;
		}
		return actives;
	}
	public double getTLBHitRatio() {
		double hits = (double)(tlb_hits);
		return hits/pagerefs;
	}
	public int getPageReferences() {
		return pagerefs;
	}
	public double getPageFaultRatio() {
		double faults = (double)(page_faults);
		return faults/pagerefs;
	}

	
}

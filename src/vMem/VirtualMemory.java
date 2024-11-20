package vMem;

import java.util.Arrays;
import java.util.Scanner;

public class VirtualMemory {
	public static final int PAGE_SIZE = 256;
	public static final int FRAME_SIZE = PAGE_SIZE;
	public int numPages; // number of pages
	public int numFrames; // number of frames
	public int tlbSize; // number of entries in the TLB

	private int[] pageTable; // map from page# (index) to frame#(value)
	private int[] invertedPageTable; // map frame# (index) to page#(value)
	private TLB_Entry[] tlb; // list of TLB entries
	private int nextLoadLocation = 0; // next frame index to load the new page
	private int next_free_tlb_index = 0; // index of next free TLB entry
	private int page_faults = 0; // number of page faults
	private int tlb_hits = 0; // number of TLB hits

	public int getPage_faults() {
		return page_faults;
	}

	public int getTlb_hits() {
		return tlb_hits;
	}

	// constructor
	public VirtualMemory(int numPages, int numFrames, int tlbSize) {
		// TODO: add code below
		this.numPages = numPages;
		this.numFrames = numFrames;
		this.tlbSize = tlbSize;
		// setup page table and the inverted one
		this.pageTable = new int[numPages];
		Arrays.fill(pageTable, -1);
		this.invertedPageTable = new int[numFrames];
		Arrays.fill(invertedPageTable, -1);
		// set up the TLB
		tlb = new TLB_Entry[tlbSize];
		for (int i = 0; i < tlbSize; i++) {
			tlb[i] = new TLB_Entry(-1, -1, false);

		}

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
	int search_page_table(int page_number) {
		
		int frameNumber= search_tlb(page_number);
		if(frameNumber>=0) {//hit
			System.out.println("TLB HIT: The page "+ page_number+ " has been loaded into the frame "+ frameNumber);
			tlb_hits++;
			
		}else {//miss
			//search entry on the page table
			frameNumber=pageTable[page_number];
			if(frameNumber>=0) {
				System.out.println("Page hit: The page "+ page_number+ " has been loaded into the frame "+ frameNumber);
			}else {//Page Fault
				page_faults++;
				//try to find any free frame
				int frame=-1;
				for(int f=0;f<numFrames;f++) {
					if(invertedPageTable[f]==-1) {
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
				pageTable[page_number]=frame;
				invertedPageTable[frame]=page_number;
				for(int i=0;i<pageTable.length;i++) {
					if(i!=page_number) {
						if(pageTable[i]==frame) {
							pageTable[i]=-1;
						}
					}
				}
				update_tlb(page_number,frame);
				System.out.println("Page fault: The page "+ page_number+ " has been loaded into the frame "+ frame);
				
			}
			
		}
		return frameNumber;
	}
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
	
}

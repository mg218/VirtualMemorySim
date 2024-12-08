package vMem;

public class ProcessEvent {
	public enum type {//Different possible types of events
		TLBHIT, TLBMISS, PAGEHIT, PAGEFAULT, LOAD, SAVE
	};

	public ProcessEvent.type event;
	public Process process;
	public int pageNum, frameNum;
	//Contrtuctor
	public ProcessEvent(Process p, ProcessEvent.type t, int pageNum, int frameNum) {
		event = t;
		process = p;
		this.pageNum = pageNum;
		this.frameNum = frameNum;
	}

	@Override
	public String toString() {//Output a messages based on event that happened
		String action;
		switch (this.event) {
		case TLBHIT:
			action = "Process: " + process.getID() + " Page Number: " + pageNum + " is in frame " + frameNum;
			break;
		case TLBMISS:
			action = "Process: " + process.getID() + " Page Number: " + pageNum + " is not in TLB";
			break;
		case PAGEHIT:
			action = "Process: " + process.getID() + " Page Number: " + pageNum + " is in frame " + frameNum;
			break;
		case PAGEFAULT:
			action = "Process: " + process.getID() + " Page Number: " + pageNum
					+ " is not in Memory and will be loaded into frame " + frameNum;
			break;
		case LOAD:
			action = "Process: " + process.getID() + " Page Number: " + pageNum + " is loaded into frame " + frameNum;
			break;
		case SAVE:
			action = "Process: " + process.getID() + " Page Number: " + pageNum + " has been saved to storage ";
			break;
		default:
			action = this.event.toString();
		}
		return this.event.toString() + ": " + action;
	}
	

}

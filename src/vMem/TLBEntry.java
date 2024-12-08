package vMem;
public class TLBEntry {
	protected int pageNumber;      // store page number
	protected int frameNumber;     // store the mapped frame number
	protected boolean valid;       // valid mapping or not

	// constructor method
	public TLBEntry(int pageNumber, int frameNumber, boolean valid) {
		this.pageNumber = pageNumber;
		this.frameNumber = frameNumber;
		this.valid=valid;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int page_number) {
		this.pageNumber = page_number;
	}

	public int getFrameNumber() {
		return frameNumber;
	}

	public void setFrameNumber(int frame_number) {
		this.frameNumber = frame_number;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	@Override
	public String toString() {
		if(!valid)
			return "invalid";

		return pageNumber + ":" + frameNumber;
	}
} 


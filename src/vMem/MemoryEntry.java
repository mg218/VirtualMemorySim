package vMem;

public class MemoryEntry {

	protected Process proc;
	protected int val;

	public MemoryEntry(Process proc, int val) {
		this.proc = proc;
		this.val = val;
	}

	@Override
	public String toString() {
		if (proc == null)
			return "invalid";
		else
			return proc.getID() + "." + val;
	}
}

package vMem;

public class MEMORY_ENTRY {

	protected Process proc;
	protected int val;

	public MEMORY_ENTRY(Process proc, int val) {
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

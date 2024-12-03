package vMem;

import java.util.ArrayList;
import java.util.List;

public class SimulatorThread implements Runnable {
  //thread managment fields
  private final Thread thread;
  private int speedMS = 1000;
  private boolean paused = true;
  private boolean done = false;

  //simulator fields
  private VirtualMemory mmu;
  private List<Process> allProcesses;
  private int numberPages = 32;
  private int numberFrames = 16;
  private int tlbSize = 4;
  private int processCount = 4;

  public SimulatorThread() {
    resetSimulator();

    //create and start ourselves
    thread = new Thread(this);
    thread.start();
  }

  @Override
  public synchronized void run() {
    while (true) {
      try {
        do {
          if (paused || done) {
            wait();
          } else {
            wait((long) speedMS);
          }
        } while (paused || done);
      } catch (InterruptedException ex) {
        System.err.println("Interrupted: " + ex.getMessage());
      }

      stepSimulator();
    }
  }

  //field variable setters

  public Void setSpeed(Integer speed) {
    speedMS = speed;

    return null;
  }

  public Void setNumberPages(Integer pages) {
    numberPages = pages;
    resetSimulator();

    return null;
  }

  public Void setNumberFrames(Integer frames) {
    numberFrames = frames;
    resetSimulator();

    return null;
  }

  public Void setTlbSize(Integer size) {
    tlbSize = size;
    resetSimulator();

    return null;
  }

  public Void setProcessCount(Integer count) {
    processCount = count;
    resetSimulator();

    return null;
  }

  //player controls

  public void play() {
		paused = false;

		switch (thread.getState()) {
		case WAITING -> unpause();
		case TIMED_WAITING, RUNNABLE -> {
		} // do nothing
		default -> throw new IllegalArgumentException("Unexpected value: " + thread.getState());
		}
  }

  public void pause() {
    paused = true;
  }

  public void step() {
		if (!done && paused) {
			stepSimulator();
		}
  }

  //private methods

	private synchronized void unpause() {
		notify();
	}

  private void stepSimulator() {
    mmu.step();
  }

  private void resetSimulator() {
    //reset thread variables
    paused = true;
    done = false;
    //create new list of processes
    allProcesses = new ArrayList<Process>();
    for (int i = 0; i < processCount; i++) {
      allProcesses.add( new Process("P" + i, numberPages));
    }
    //setup new mmu
    mmu = new VirtualMemory(numberPages, numberFrames, tlbSize, allProcesses);
  }
}

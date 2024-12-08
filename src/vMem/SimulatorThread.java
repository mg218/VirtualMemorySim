package vMem;

import java.util.ArrayList;
import java.util.List;

import gui.MemoryView;
import gui.StatisticsView;
import gui.TLBView;

public class SimulatorThread implements Runnable {
  // thread managment fields
  private final Thread thread;
  private int speedMS = 1000;
  private boolean paused = true;
  private boolean done = false;

  // gui listener fields
  private TLBView tlbView;
  private StatisticsView statsView;
  private MemoryView memoryView;

  // simulator fields
  private VirtualMemory mmu;
  private List<Process> allProcesses;
  private int numberPages = 32;
  private int numberFrames = 16;
  private int tlbSize = 4;
  private int processCount = 4;

  public SimulatorThread(TLBView tv, MemoryView mv, StatisticsView sv) {
    tlbView = tv;
    statsView = sv;
    memoryView = mv;

    resetSimulator();

    // create and start ourselves
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

  // field variable setters

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

  // player controls

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

  // private methods

  private synchronized void unpause() {
    notify();
  }

  private void stepSimulator() {
    mmu.step();
    // refresh listeners with new data
    tlbView.refresh();
    memoryView.refresh();
    statsView.refresh(mmu.getPageReferences(), mmu.getTLBMisses(), mmu.getPageFaults(), mmu.getTLBHitRatio(),
        mmu.getPageFaultRatio(), mmu.getDiskAccess());
  }

  private void resetSimulator() {
    // reset thread variables
    paused = true;
    done = false;
    // create new list of processes
    allProcesses = new ArrayList<Process>();
    for (int i = 0; i < processCount; i++) {
      allProcesses.add(new Process("P" + i, numberPages));
    }
    // setup new mmu
    mmu = new VirtualMemory(numberPages, numberFrames, tlbSize, allProcesses);
    tlbView.setTLB(mmu.getTLB());
    memoryView.setMemory(mmu.getMemory());
  }
}

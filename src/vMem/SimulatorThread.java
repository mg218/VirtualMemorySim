package vMem;

import java.util.*;

import gui.EventLog;
import gui.MemoryView;
import gui.PageTableView;
import gui.ProcessTableModel;
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
  private ProcessTableModel processModel;
  private StatisticsView statsView;
  private MemoryView memoryView;
  private PageTableView pageTableView;
  private EventLog log;

  // simulator fields
  private VirtualMemory mmu;
  private List<Process> allProcesses;
  private int numberPages = 32;
  private int numberFrames = 16;
  private int tlbSize = 4;
  private int processCount = 4;

  // finals
  private static final String invalidConfig = "Invalid Config! ";
  private static final String notPowerofTwo = " must be a power of 2";

  private static final int[] tlbSizes = { 16, 12, 8, 4 };
  private static final int[] processCounts = { 8, 4, 2, 1 };

  public SimulatorThread(TLBView tv, ProcessTableModel pm, MemoryView mv, PageTableView ptv, StatisticsView sv, EventLog log) {
    tlbView = tv;
    processModel = pm;
    statsView = sv;
    memoryView = mv;
    pageTableView = ptv;
    this.log = log;

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
  public void loadConfig(HashMap<String, Integer> config) {
    boolean resetNeeded = false;

    if (config.containsKey("NUM_PAGES")) {
      if (!isPowerOfTwo(config.get("NUM_PAGES")))
        throw new IllegalArgumentException(invalidConfig + "Number of pages" + notPowerofTwo);
      numberPages = config.get("NUM_PAGES");
      resetNeeded = true;
    }

    if (config.containsKey("NUM_FRAMES")) {
      if (!isPowerOfTwo(config.get("NUM_FRAMES")))
        throw new IllegalArgumentException(invalidConfig + "Number of frames" + notPowerofTwo);
      numberFrames = config.get("NUM_FRAMES");
      resetNeeded = true;
    }

    if (config.containsKey("NUM_PAGES")) {
      var size = config.get("NUM_TLB_ENTRIES");
      if (!Arrays.stream(tlbSizes).anyMatch(x -> x == size))
        throw new IllegalArgumentException(invalidConfig + "The number of TLB entries must be either 4, 8, 12 or 16");
      tlbSize = size;
      resetNeeded = true;
    }

    if (config.containsKey("NUM_PROCS")) {
      var count = config.get("NUM_PROCS");
      if (!Arrays.stream(processCounts).anyMatch(x -> x == count))
        throw new IllegalArgumentException(invalidConfig + "The number of processes must be either 1, 2, 4 or 8");
      processCount = count;
      resetNeeded = true;
    }

    if(resetNeeded)
      resetSimulator();
  }

  public Void setSpeed(Integer speed) {
    speedMS = speed;

    return null;
  }

  public Void setNumberPages(Integer pages) throws IllegalArgumentException {
    if (!isPowerOfTwo(pages))
      throw new IllegalArgumentException("Number of pages" + notPowerofTwo);
    numberPages = pages;
    resetSimulator();

    return null;
  }

  public Void setNumberFrames(Integer frames) throws IllegalArgumentException {
    if (!isPowerOfTwo(frames))
      throw new IllegalArgumentException("Number of frames" + notPowerofTwo);
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
    var events = mmu.step();
    for(var event : events) {
      log.addEvent(event);
    }
    // refresh listeners with new data
    tlbView.refresh();
    processModel.refresh();
    memoryView.refresh();
    pageTableView.refresh();
    statsView.refresh(mmu.getPageReferences(), mmu.getTLBMisses(), mmu.getPageFaults(), mmu.getTLBMissRatio(),
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
    processModel.setProcesses(allProcesses);
    memoryView.setMemory(mmu.getMemory());
    pageTableView.setProcesses(allProcesses);
  }

  private boolean isPowerOfTwo(int n) {
    return (n != 0) && ((n & (n - 1)) == 0);
  }
}

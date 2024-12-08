package gui;

import vMem.FileHandler;
import vMem.SimulatorThread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.function.Function;

public class MemorySimulator implements Runnable {

  private JFrame memSim;
  private PlayerControls controls;
  private SimulatorThread player;
  private TLBView tlbView;
  private ProcessTableModel processModel;
  private StatisticsView statsView;
  private MemoryView memoryView;
  private PageTableView pageTableView;
  private EventLog log;

  public static void main(String[] args) {
    EventQueue.invokeLater(new MemorySimulator());
  }

  @Override
  public void run() {
    memSim.setVisible(true);
  }

  // main simulator constructor, begin gui and simulator thread
  public MemorySimulator() {
    memSim = new JFrame();
    memSim.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // kill JVM if this frame dies
    memSim.setLayout(new GridBagLayout());
    memSim.setTitle("Memory Simulator");

    initTLBTable();
    initProcessTable();
    initStatsView();
    initMemoryTable();
    initPageTable();
    initEventLog();
    initControls();

    player = new SimulatorThread(tlbView, processModel, memoryView, pageTableView, statsView, log);

    initMenuBar();

    memSim.setMinimumSize(new Dimension(800, 450));
    memSim.setLocationRelativeTo(null); // move us to center of the sceen
  }

  private void initMenuBar() {
    JMenuBar menuBar = new JMenuBar();
    memSim.setJMenuBar(menuBar);

    // file menu
    var mnFile = new JMenu("File");
    menuBar.add(mnFile);

    var mntmOpenConfigFile = new JMenuItem("Open config file");
    mntmOpenConfigFile.addActionListener((ActionEvent e) -> {
      var filePath = doFileDialog(System.getProperty("user.home"), FileDialog.LOAD);
      try {
        var config = FileHandler.readFile(filePath);
        player.loadConfig(config);

      } catch (FileNotFoundException fnfe) {
        JOptionPane.showMessageDialog(memSim, fnfe.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      } catch (IllegalArgumentException iae) {
        JOptionPane.showMessageDialog(memSim, iae.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      }
    });
    mnFile.add(mntmOpenConfigFile);

    // settings menu
    var mnSettings = new JMenu("Settings");
    menuBar.add(mnSettings);

    String[] speedDefaults = { "1000 ms", "100 ms" };
    mnSettings.add(generateSubMenu("Speed", player::setSpeed, speedDefaults, true));

    String[] pageDefaults = { "128", "64", "32" };
    mnSettings.add(generateSubMenu("Pages", player::setNumberPages, pageDefaults, true));

    String[] frameDefaults = { "32", "16" };
    mnSettings.add(generateSubMenu("Frames", player::setNumberFrames, frameDefaults, true));

    String[] tlbDefaults = { "16", "12", "8", "4" };
    mnSettings.add(generateSubMenu("TLB Size", player::setTlbSize, tlbDefaults, false));

    String[] processCountDefaults = { "8", "4", "2", "1" };
    mnSettings.add(generateSubMenu("Processes", player::setProcessCount, processCountDefaults, false));

    // help menu
    var mnHelp = new JMenu("Help");
    menuBar.add(mnHelp);

    var mntmAbout = new JMenuItem("About");
    var aboutListener = (ActionListener) (ActionEvent e) -> {
      JOptionPane.showMessageDialog(memSim, "Made By:\nJoshua Miertschin\nMason Goss", "About",
          JOptionPane.INFORMATION_MESSAGE);
    };
    mntmAbout.addActionListener(aboutListener);
    mnHelp.add(mntmAbout);
  }

  // creates a submenu with a Custom option and presets stored in an array
  // presets can have units as long as the first word in the string is number
  private JMenu generateSubMenu(String name, Function<Integer, Void> func, String[] presets, boolean haveCustom) {
    var temp = new JMenu(name);

    var customListener = (ActionListener) (ActionEvent e) -> {
      var input = doInputDialog("set " + name);
      try {
        var inputInt = Integer.parseInt(input);
        if (inputInt < 1)
          throw new IllegalArgumentException(name + " must be greater than 0");
        func.apply(inputInt);
      } catch (NumberFormatException _e) {
        JOptionPane.showMessageDialog(memSim, name + " must be a number", "Error", JOptionPane.ERROR_MESSAGE);
      } catch (IllegalArgumentException iae) {
        JOptionPane.showMessageDialog(memSim, iae.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      }
    };

    if (haveCustom) {
      var mntmCustom = new JMenuItem("Custom...");
      mntmCustom.addActionListener(customListener);
      temp.add(mntmCustom);
    }

    for (int i = 0; i < presets.length; i++) {
      var mntmPreset = new JMenuItem(presets[i]);
      int j = Integer.parseInt(presets[i].split(" ")[0].trim());
      mntmPreset.addActionListener((ActionEvent e) -> {
        func.apply(j);
      });
      temp.add(mntmPreset);
    }

    return temp;
  }

  private void initTLBTable() {
    tlbView = new TLBView();

    var tlbTableConstraints = new GridBagConstraints();
    tlbTableConstraints.gridx = 0;
    tlbTableConstraints.gridy = 0;
    tlbTableConstraints.gridwidth = 1;
    tlbTableConstraints.gridheight = 1;
    tlbTableConstraints.fill = GridBagConstraints.BOTH;
    tlbTableConstraints.weightx = 1.0;
    tlbTableConstraints.weighty = 1.0;
    memSim.add(tlbView, tlbTableConstraints);
  }

  private void initProcessTable() {
    processModel = new ProcessTableModel();

    var table = new JTable(processModel);

    var processTableConstraints = new GridBagConstraints();
    processTableConstraints.gridx = 1;
    processTableConstraints.gridy = 0;
    processTableConstraints.gridwidth = 1;
    processTableConstraints.gridheight = 1;
    processTableConstraints.fill = GridBagConstraints.BOTH;
    processTableConstraints.weightx = .4;
    processTableConstraints.weighty = 1.0;
    memSim.add(new JScrollPane(table), processTableConstraints);
  }

  private void initStatsView() {
    statsView = new StatisticsView();

    var statsViewConstraints = new GridBagConstraints();
    statsViewConstraints.gridx = 2;
    statsViewConstraints.gridy = 0;
    statsViewConstraints.gridwidth = 1;
    statsViewConstraints.gridheight = 1;
    statsViewConstraints.fill = GridBagConstraints.BOTH;
    statsViewConstraints.weightx = 2.0;
    statsViewConstraints.weighty = 1.0;
    memSim.add(statsView, statsViewConstraints);
  }

  private void initMemoryTable() {
    memoryView = new MemoryView();

    var memoryTableConstraints = new GridBagConstraints();
    memoryTableConstraints.gridx = 0;
    memoryTableConstraints.gridy = 1;
    memoryTableConstraints.gridwidth = 3;
    memoryTableConstraints.gridheight = 2;
    memoryTableConstraints.fill = GridBagConstraints.BOTH;
    memoryTableConstraints.weightx = 1.0;
    memoryTableConstraints.weighty = 1.0;
    memSim.add(memoryView, memoryTableConstraints);
  }

  private void initPageTable() {
    pageTableView = new PageTableView();

    var pageTableConstraints = new GridBagConstraints();
    pageTableConstraints.gridx = 0;
    pageTableConstraints.gridy = 3;
    pageTableConstraints.gridwidth = 3;
    pageTableConstraints.gridheight = 2;
    pageTableConstraints.fill = GridBagConstraints.BOTH;
    pageTableConstraints.weightx = 1.0;
    pageTableConstraints.weighty = 1.0;
    memSim.add(pageTableView, pageTableConstraints);
  }

  private void initEventLog() {
    log = new EventLog();

    var logConstraints = new GridBagConstraints();
    logConstraints.gridx = 3;
    logConstraints.gridy = 0;
    logConstraints.gridwidth = 1;
    logConstraints.gridheight = 5;
    logConstraints.fill = GridBagConstraints.BOTH;
    logConstraints.weightx = 1.0;
    logConstraints.weighty = 1.0;
    memSim.add(new JScrollPane(log), logConstraints);
  }

  private void initControls() {
    var controlsListener = (ActionListener) (ActionEvent e) -> {
      controlsAction((JButton) e.getSource());
    };
    controls = new PlayerControls(controlsListener);
    var constraints = new GridBagConstraints();
    constraints.gridx = 0;
    constraints.gridy = 5;
    constraints.gridwidth = 3;
    constraints.gridheight = 1;
    constraints.fill = GridBagConstraints.NONE;
    constraints.weightx = 0.0;
    constraints.weighty = Double.MIN_VALUE;
    memSim.add(controls, constraints);
  }

  void controlsAction(JButton button) {
    switch (button.getName()) {
    case "play" -> player.play();
    case "pause" -> player.pause();
    case "step" -> player.step();
    default -> throw new UnsupportedOperationException("Bad Controls input");
    }
  }

  // gets user input and returns it
  private String doInputDialog(String message) {
    return (String) JOptionPane.showInputDialog(memSim, message, "");
  }

  // creates a file diaglog and returns a String of what the uer selected or it's
  // empty
  private String doFileDialog(String dir, int mode) {
    var fd = new FileDialog(memSim, "Choose a file", mode);
    fd.setDirectory(dir);
    fd.setFile("*.txt");
    fd.setVisible(true);
    String filename = fd.getFile();
    if (filename == null) {
      System.out.println("You cancelled the choice");
      return "";
    }

    fd.dispose();
    return fd.getDirectory() + filename;
  }
}
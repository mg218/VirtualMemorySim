package gui;

import vMem.SimulatorThread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.function.Function;

public class MemorySimulator implements Runnable {

  JFrame memSim;
  private PlayerControls controls;
  private SimulatorThread player;

  public static void main(String[] args) {
    EventQueue.invokeLater(new MemorySimulator());
  }

  @Override
  public void run() {
    memSim.setVisible(true);
  }

  public MemorySimulator() {
    memSim = new JFrame();
    memSim.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // kill JVM if this frame dies
    memSim.setLayout(new GridBagLayout());

    memSim.setTitle("Memory Simulator");

    player = new SimulatorThread();

    initMenuBar();
    initControls();

    memSim.setSize(800, 600);
    memSim.setLocationRelativeTo(null); // move us to center of the sceen
  }

  private void initMenuBar() {
    JMenuBar menuBar = new JMenuBar();
    memSim.setJMenuBar(menuBar);

    // settings menu
    var mnSettings = new JMenu("Settings");
    menuBar.add(mnSettings);

    String[] speedDefaults = { "1000 ms", "100 ms" };
    mnSettings.add(generateSubMenu("Speed", player::setSpeed, speedDefaults));

    String[] pageDefaults = { "128", "64", "32" };
    mnSettings.add(generateSubMenu("Pages", player::setNumberPages, pageDefaults));

    String[] frameDefaults = { "32", "16" };
    mnSettings.add(generateSubMenu("Frames", player::setNumberFrames, frameDefaults));

    String[] tlbDefaults = { "8", "4" };
    mnSettings.add(generateSubMenu("TLB Size", player::setTlbSize, tlbDefaults));

    String[] processCountDefaults = { "10", "8", "6", "4" };
    mnSettings.add(generateSubMenu("Pages", player::setProcessCount, processCountDefaults));

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
  private JMenu generateSubMenu(String name, Function<Integer, Void> func, String[] presets) {
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

    var mntmCustom = new JMenuItem("Custom...");
    mntmCustom.addActionListener(customListener);
    temp.add(mntmCustom);

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

  private void initControls() {
    var controlsListener = (ActionListener) (ActionEvent e) -> {
      controlsAction((JButton) e.getSource());
    };
    controls = new PlayerControls(controlsListener);
    var constraints = new GridBagConstraints();
    constraints.gridx = 0;
    constraints.gridy = 4;
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
}
package gui;

import vMem.SimulatorThread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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

}
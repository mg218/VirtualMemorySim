package gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JPanel;

import vMem.MemoryEntry;
import vMem.Process;

public class PageTableView extends JPanel {
  private List<Process> allProcesses;

  public PageTableView() {
    super();
  }

  public void refresh() {
    repaint();
  }

  // sets the current process Objects to be viewed in the table
  public void setProcesses(List<Process> processes) {
    allProcesses = processes;

    repaint();
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(200, 200);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    //draw TLB label
    int labelHeight = getHeight() / 8;
    g.drawRect(3, 2, getWidth() - 6, labelHeight);

    Font font = new Font("Arial", Font.PLAIN, 3 * labelHeight / 4);
    g.setFont(font);
    
    if(allProcesses == null) {
      g.drawString("Current Page Table", 8, labelHeight - labelHeight / 4);
      return;
    }
    
    //find the process with active set on it
    Process activeProcess = allProcesses.stream()
    .filter(p -> p.getActive())
    .findFirst()
    .orElse(allProcesses.get(0));
    
    g.drawString("Current Page Table: " + activeProcess.getID(), 8, labelHeight - labelHeight / 4);
    
    int[] pageTable = activeProcess.getPageTable();

    // calc columns needed by making smallest square size
    int columns = (int) Math.ceil(Math.sqrt(pageTable.length));
    // calc how many rows we really need for the memory size
    int rows = (int) Math.ceil(pageTable.length / (double) columns);

    int sizeX = (getWidth() - 4) / columns;
    int sizeY = (getHeight() - labelHeight - 4) / rows;

    int x = getWidth() / 2 - sizeX * columns / 2;
    for (int column = 0; column < columns; column++) {
      int y = (getHeight() - labelHeight) / 2 - sizeY * rows / 2;
      for (int row = 0; row < rows; row++) {
        var framePointer = columns * row + column;
        paintMemoryFrame(g, framePointer, pageTable, x, y + labelHeight, sizeX, sizeY);

        y += sizeY;
      }
      x += sizeX;
    }
  }

  private void paintMemoryFrame(Graphics g, int framePointer, int[] pageTable, int x, int y, int sizeX, int sizeY) {
    Font font = new Font("Arial", Font.PLAIN, sizeY / 2);
    g.setFont(font);

    g.drawRect(x, y, sizeX, sizeY);
    if (framePointer < pageTable.length) {
      var frame = pageTable[framePointer];
      g.drawString(Integer.toString(framePointer) + ": " + ((frame == -1) ? "invalid" : Integer.toString(frame)), x + 4, y + 3 * sizeY / 4);
    }
  }
}

package gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

import vMem.MEMORY_ENTRY;

public class MemoryView extends JPanel {
  MEMORY_ENTRY[] memory;

  public MemoryView() {
    super();
  }

  public void refresh() {
    repaint();
  }

  // sets the current process Objects to be viewed in the table
  public void setMemory(MEMORY_ENTRY[] m) {
    memory = m;

    repaint();
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(200, 200);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    // calc columns needed by making smallest square size
    int columns = (int) Math.ceil(Math.sqrt(memory.length));
    // calc how many rows we really need for the memory size
    int rows = (int) Math.ceil(memory.length / (double) columns);

    int sizeX = (getWidth() - 4) / columns;
    int sizeY = (getHeight() - 4) / rows;

    int x = getWidth() / 2 - sizeX * columns / 2;
    for (int column = 0; column < columns; column++) {
      int y = getHeight() / 2 - sizeY * rows / 2;
      for (int row = 0; row < rows; row++) {
        var framePointer = columns * row + column;
        paintMemoryFrame(g, framePointer, x, y, sizeX, sizeY);

        y += sizeY;
      }
      x += sizeX;
    }
  }

  private void paintMemoryFrame(Graphics g, int framePointer, int x, int y, int sizeX, int sizeY) {
    Font font = new Font("Arial", Font.PLAIN, sizeY / 2);
    g.setFont(font);

    g.drawRect(x, y, sizeX, sizeY);
    if (framePointer < memory.length) {
      var frame = memory[framePointer];
      g.drawString(frame.toString(), x, y + 3 * sizeY / 4);
    }
  }
}

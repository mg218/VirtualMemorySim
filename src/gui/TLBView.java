package gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

import vMem.TLB_Entry;

public class TLBView extends JPanel {
  TLB_Entry[] tlb;

  public TLBView() {
    super();
  }

  public void refresh() {
    repaint();
  }

  public void setTLB(TLB_Entry[] t) {
    tlb = t;

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
    int columns = (int) Math.ceil(Math.sqrt(tlb.length));
    // calc how many rows we really need for the memory size
    int rows = (int) Math.ceil(tlb.length / (double) columns);

    int sizeX = (getWidth() - 4) / columns;
    int sizeY = (getHeight() - 4) / rows;

    int x = getWidth() / 2 - sizeX * columns / 2;
    for (int column = 0; column < columns; column++) {
      int y = getHeight() / 2 - sizeY * rows / 2;
      for (int row = 0; row < rows; row++) {
        var framePointer = columns * row + column;
        paintTLBFrame(g, framePointer, x, y, sizeX, sizeY);

        y += sizeY;
      }
      x += sizeX;
    }
  }

  private void paintTLBFrame(Graphics g, int framePointer, int x, int y, int sizeX, int sizeY) {
    Font font = new Font("Arial", Font.PLAIN, sizeY / 2);
    g.setFont(font);

    g.drawRect(x, y, sizeX, sizeY);
    if (framePointer < tlb.length) {
      var frame = tlb[framePointer];
      g.drawString(frame.toString(), x, y + 3 * sizeY / 4);
    }
  }
}

package gui;

import java.awt.*;

import javax.swing.JPanel;

public class StatisticsView extends JPanel {
  private final String pageReferencesFormat = "Page References: %d";
  private final String tlbMissesFormat = "TLB misses: %d";
  private final String pageFaultsFormat = "Page Faults: %d";
  private final String tlbRatioFormat = "TLB miss ratio: %.2f";
  private final String pageFaultRatioFormat = "Mem ratio: %.2f";
  private final String diskAccessesFormat = "disk accesses: %d";

  private String pageReferences = String.format(pageReferencesFormat, 0);
  private String tlbMisses = String.format(tlbMissesFormat, 0);
  private String pageFaults = String.format(pageFaultsFormat, 0);
  private String tlbRatio = String.format(tlbRatioFormat, 0.0);
  private String pageFaultRatio = String.format(pageFaultRatioFormat, 0.0);
  private String diskAccesses = String.format(diskAccessesFormat, 0);

  public StatisticsView() {
    super();
  }

  public void refresh(int pr, int tm, int pf, double tr, double pfr, int da) {
    pageReferences = String.format(pageReferencesFormat, pr);
    tlbMisses = String.format(tlbMissesFormat, tm);
    pageFaults = String.format(pageFaultsFormat, pf);
    tlbRatio = String.format(tlbRatioFormat, tr);
    pageFaultRatio = String.format(pageFaultRatioFormat, pfr);
    diskAccesses = String.format(diskAccessesFormat, da);

    repaint();
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(200, 200);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    var rowHeight = getHeight() / 4;
    var columnWidth = getWidth() / 2;

    var rowHeightOffset = rowHeight / 4;

    Font font = new Font("Arial", Font.PLAIN, 3 * rowHeight / 4);
    this.setFont(font);

    // row 0
    g.drawString(pageReferences, 0, rowHeight - rowHeightOffset);

    // row 1
    g.drawString(tlbMisses, 0, rowHeight * 2 - rowHeightOffset);
    g.drawString(pageFaults, columnWidth, rowHeight * 2 - rowHeightOffset);

    // row 2
    g.drawString(tlbRatio, 0, rowHeight * 3 - rowHeightOffset);
    g.drawString(pageFaultRatio, columnWidth, rowHeight * 3 - rowHeightOffset);

    // row 3
    g.drawString(diskAccesses, 0, rowHeight * 4 - rowHeightOffset);
  }

}

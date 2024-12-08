package gui;

import java.io.Serializable;
import java.util.List;

import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import vMem.Process;

public class ProcessTableModel implements TableModel, Serializable {
	private static final long serialVersionUID = 999999999L;

	private List<Process> processes;
	protected EventListenerList listenerList = new EventListenerList();

	// Columns
	private static final String[] columnNames = { "ID", "State" };

	// a Custom TableModel class using a List of PCB Objects as its source of data
	public ProcessTableModel() {
	}

	// sets the current process Objects to be viewed in the table
	public void setProcesses(List<Process> processes) {
		this.processes = processes;
		fireTableChanged(new TableModelEvent(this));
	}

	// force a refresh of the table
	public void refresh() {
		fireTableChanged(new TableModelEvent(this));
	}

	public int getRowCount() {
		return (processes != null) ? processes.size() : 0;
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		var process = processes.get(rowIndex);

    return switch(columnIndex) {
      case 0 -> process.getID();
      default -> (process.getActive()) ? "active" : "sleeping";
    };
	}

  @Override
  public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0 -> {
			return String.class;
		}
		default -> {
			return Integer.class;
		}
		}
  }

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false; // no cell editable
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		// see function above
		throw new UnsupportedOperationException("Table not editable");
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
		listenerList.add(TableModelListener.class, l);
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		listenerList.remove(TableModelListener.class, l);
	}

	// copied from AbstractTableModel's implmentation
	public void fireTableChanged(TableModelEvent e) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TableModelListener.class) {
				((TableModelListener) listeners[i + 1]).tableChanged(e);
			}
		}
	}

}

package gui;

import java.awt.Font;
import java.io.*;
import javax.swing.JTextArea;

import vMem.ProcessEvent;

public class EventLog extends JTextArea {
	private static final long serialVersionUID = -5464549856362389332L;

	// simple JTextField with support for printing ProcessEvent Objects
	public EventLog() {
		super();
		setFont(new Font("Arial", Font.PLAIN, 12));
		setEditable(false);
		setLineWrap(true);
		setWrapStyleWord(true);
	}

	public void addEvent(ProcessEvent event) {
		this.addEvent(event.toString());
	}

	public void addEvent(String event) {
		this.append(event + "\n");
		this.setCaretPosition(this.getDocument().getLength());
	}

	public void clear() {
		super.setText("");
	}
}
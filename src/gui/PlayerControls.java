package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import javax.swing.*;

public class PlayerControls extends JPanel {
	private static final long serialVersionUID = 5743620645684390758L;
	private final JButton playBtn;
	private final JButton pauseBtn;
	private final JButton nextBtn;

	public PlayerControls(ActionListener al) {
		setLayout(new FlowLayout(FlowLayout.CENTER));

		// TODO: icons?
		playBtn = new JButton("play");
		playBtn.setName("play");
		playBtn.addActionListener(al);
		add(playBtn, BorderLayout.CENTER);

		pauseBtn = new JButton("pause");
		pauseBtn.setName("pause");
		pauseBtn.addActionListener(al);
		add(pauseBtn, BorderLayout.CENTER);

		nextBtn = new JButton("next");
		nextBtn.setName("step");
		nextBtn.addActionListener(al);
		add(nextBtn, BorderLayout.CENTER);
	}
}

package manage;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Inactivity extends Thread {
	
	private Frame mainFrame;
	private int seconds = 0; // Timer
	private boolean userDidSomething = false;
	private boolean warningIsShowing = false;
	private boolean paused = false;
	private Dialog warningDialog;
	private Label countdownLabel;
	
	// Constructor
	public Inactivity(Frame mainFrame) {	
		this.mainFrame = mainFrame;
		
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
			
			public void eventDispatched(AWTEvent e) {
				if (e instanceof MouseEvent) {
					MouseEvent me = (MouseEvent) e;
					if (me.getID() == MouseEvent.MOUSE_PRESSED) userDidSomething = true;
				}
				else if (e instanceof KeyEvent) {
					userDidSomething = true;
				}
			}
			
		}, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.KEY_EVENT_MASK);
	}
	
	// Setter
	public void setPaused(boolean paused) {
		this.paused = paused;
		if (paused == true && warningIsShowing == true) closeWarning();
	}
	
	public void run() {
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
			
			if (paused == true) continue;
			
			if (userDidSomething == true) {
				seconds = 0;
				userDidSomething = false;
				if (warningIsShowing == true) closeWarning();
			}
			else seconds += 1;
			
			if (seconds >= 60) {
				System.exit(0);
			}
			else if (seconds >= 55 && warningIsShowing == false) {
				showWarning();
			}
			else if (warningIsShowing == true) {
				int secondsLeft = 60 - seconds;
				countdownLabel.setText("Closing in " + secondsLeft + " seconds.");
			}
		}
	}
	
	// Shows the warning tab with the timer counting down
	private void showWarning() {
		warningIsShowing = true;
		countdownLabel = new Label("Closing in 5 seconds.");
		
		Button continueButton = new Button("Continue");
		continueButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				seconds = 0;
				closeWarning();
			}
			
		});
		
		Button endButton = new Button("End Now");
		endButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
			
		});
		
		Panel bottomPanel = new Panel();
		bottomPanel.add(continueButton);
		bottomPanel.add(endButton);
		
		warningDialog = new Dialog(mainFrame, "Warning", false);
		warningDialog.setLayout(new BorderLayout());
		warningDialog.add(countdownLabel, BorderLayout.CENTER);
		warningDialog.add(bottomPanel, BorderLayout.SOUTH);
		warningDialog.setSize(300, 100);
		warningDialog.setLocationRelativeTo(mainFrame);
		warningDialog.setVisible(true);
	}
	
	// Closes the warning tab
	private void closeWarning() {
		warningIsShowing = false;
		if (warningDialog != null) {
			warningDialog.setVisible(false);
			warningDialog.dispose();
			warningDialog = null;
		}
	}

}

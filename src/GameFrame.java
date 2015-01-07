import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class GameFrame extends JFrame {

	GUI gui;
	SidePanel sidePanel;
	
	public GameFrame() {
		
		gui = new GUI();
		sidePanel = new SidePanel(gui);
		JPanel guiPanel = new JPanel();
		guiPanel.add(gui);
		setLayout(new BorderLayout());
		//add(gui, BorderLayout.CENTER);
		add(sidePanel, BorderLayout.EAST);
		add(gui, BorderLayout.CENTER);
		setSize(new Dimension(1000,1000));
		setVisible(true);
		gui.init();
		
	}
	
	public static void main(String[] args) {
		
		GameFrame game = new GameFrame();
		
	}

}

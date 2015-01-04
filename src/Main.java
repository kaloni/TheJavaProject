import server.Server;
import view.LoginForm;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.Random;

import javax.swing.JFrame;


public class Main extends JFrame {
	
	/*
	 * Use Main for testing
	 */
	
	Matrix<Integer> matrix;

	public static void main(String[] args) {
		
		
		/*
		Server server;
		try {
			server = new Server();
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}

		LoginForm loginForm = new LoginForm();
		loginForm.setVisible(true);
		 */
		GUI gui = new GUI();
		BuildingBlock road = new Road(3, true, gui);
		BuildingBlock curve = new Curve(0, -1,  true, gui);
		
		for(int i = -1; i <= 1; i++) {
			for(int j = -1; j <= 1; j++) {
				boolean connected = curve.checkConnect(new Pos(i, j), road);
				System.out.println(connected + " at " + i + " " + j);
			}
		}
	
		
		
	}
	

}

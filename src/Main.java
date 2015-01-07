import processing.core.PVector;
import server.Server;
import util.Network;
import view.GameMenuView;
import view.LoginForm;

import javax.swing.*;
import java.io.IOException;
import java.net.URISyntaxException;

public class Main extends JFrame {

	/*
     * Use Main for testing
	 */

    Matrix<Integer> matrix;

    public static void main(String[] args) throws IOException {

		
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
		
		/*
		GUI gui = new GUI();
		BuildingBlock road = new Road(3, true, gui);
		BuildingBlock curve = new Curve(0, -1,  true, gui);
		
		for(int i = -1; i <= 1; i++) {
			for(int j = -1; j <= 1; j++) {
				boolean connected = curve.checkConnect(new Pos(i, j), road);
				System.out.println(connected + " at " + i + " " + j);
			}
		}
		*/
		
		/*
		GUI gui = new GUI();
		BlockMap<Pos, BlockGroup> blockMap = new BlockMap<>();
		BlockGroup.initGroups(gui, blockMap);
		
		BlockGroup road = BlockGroup.newLongRoad(1, 1, true);
		BlockGroup curve = BlockGroup.newCurve(1, true);
		blockMap.put(new Pos(0,0), road);
		blockMap.put(new Pos(1,0), curve);
		
		PathFinder pathFinder = new PathFinder(blockMap);
		Matrix<Double> matrix = Matrix.getRandomMatrix(4,10);
		Integer[] previous = pathFinder.shortestPath(matrix, 1);
		
		System.out.println(matrix);
		for(int i = 0; i < previous.length; i++) {
			System.out.print(previous[i] + " ");
		}
		*/
		
		/*
		Map<Integer, Pos> biMap = HashBiMap.create();
		biMap.put(0, new Pos(0, 0));
		biMap.put(1, new Pos(0, 1));
		biMap.put(2, new Pos(1, 0));
		biMap.put(3, new Pos(1, 1));
		
		for(int i = 0; i < 4; i++) {
			System.out.println(biMap.get(i));
		}
		*/
        BlockMap<Pos, BlockGroup> blockMap = new BlockMap<>();
        GUI gui = new GUI();
        CarSimulator sim = new CarSimulator(blockMap, gui);

        PVector dir1 = new PVector(1, 1);
        PVector dir2 = new PVector(1.00001f, 1.00001f);
        System.out.println(sim.parallell(dir1, dir2));

        Server server = new Server();
        server.start();

        LoginForm loginForm = new LoginForm();
        loginForm.setLoginListener(new LoginForm.LoginListener() {
            @Override
            public void login(String username, boolean loginSuccessful, String serverIP, int serverPort) {
                if (loginSuccessful) {

					// put fake data in database
					try {
						Network.sendUserScoreToServer("Barack Obama", "110", serverIP, serverPort);
						Network.sendUserScoreToServer("John Doe", "20", serverIP, serverPort);
						Network.sendUserScoreToServer("Arielle", "120", serverIP, serverPort);
						Network.sendUserScoreToServer("Carl Dehlin", "150", serverIP, serverPort);
					} catch (URISyntaxException | IOException e) {
						e.printStackTrace();
					}

					loginForm.dispose();
                    GameMenuView gameMenuView = new GameMenuView(username, serverIP, serverPort);
                    gameMenuView.setVisible(true);
                }
            }
        });
        loginForm.setVisible(true);



    }
}

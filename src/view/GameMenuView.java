package view;

import util.Network;
import util.ViewUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;

public class GameMenuView extends JFrame {
    private String username;
    private String serverIP;
    private int serverPort;

    public GameMenuView(String username, String serverIP, int serverPort) {
        super();

        this.username = username;
        this.serverIP = serverIP;
        this.serverPort = serverPort;

        this.setTitle("GameMenuView");
        this.setVisible(false);
        this.setLayout(null);
        this.setResizable(false);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new SpringLayout());

        JButton startGameButton = new JButton("Start Game");
        startGameButton.addActionListener(startGameActionPerformed);
        panel.add(startGameButton);

        JButton leaderBoardButton = new JButton("LeaderBoard");
        leaderBoardButton.addActionListener(leaderBoardActionPerformed);
        panel.add(leaderBoardButton);

        JButton exitButton = new JButton("Exit Game");
        exitButton.addActionListener(exitGameActionPerformed);
        panel.add(exitButton);

        ViewUtil.makeCompactGrid(panel,
                3, 1, //rows, cols
                50, 20,        //initX, initY
                50, 20);       //xPad, yPad
        this.setContentPane(panel);
        this.pack();
        this.setVisible(false);

        // center the form
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
    }

    private final ActionListener startGameActionPerformed = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
        	/*
        	GUI gui = new GUI();
        	gui.start();
        	*/
        }
    };

    private final ActionListener leaderBoardActionPerformed = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String scoresJson = Network.getAllScoresFromServer(serverIP, serverPort);



                LeaderboardView leaderboardView = new LeaderboardView(scoresJson);
                leaderboardView.setVisible(true);
            } catch (URISyntaxException | IOException e1) {
                e1.printStackTrace();
            }
        }
    };
    private final ActionListener exitGameActionPerformed = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            dispose();
            System.exit(0);
        }
    };
}

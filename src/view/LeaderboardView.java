package view;

import org.json.JSONObject;
import util.ViewUtil;

import javax.swing.*;
import java.awt.*;
import java.time.Period;
import java.util.*;

public class LeaderboardView extends JFrame {

    private class Player {
        String username;
        Integer score;
    }

    public LeaderboardView(String scoreJson) {
        super();
        this.setTitle("Leaderboard");
        this.setVisible(false);
        this.setLayout(null);
        this.setResizable(false);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new SpringLayout());

        JSONObject jsonObject = new JSONObject(scoreJson);
        Iterator<?> keys = jsonObject.keys();


        java.util.List<Player> playerList = new ArrayList<>();
        while (keys.hasNext()) {
            Player player = new Player();
            player.username = (String) keys.next();
            player.score = Integer.valueOf(jsonObject.getString(player.username));
            playerList.add(player);
        }


        Collections.sort(playerList, new Comparator<Player>() {
            @Override
            public int compare(Player player1, Player player2) {
                return player1.score.compareTo(player2.score) * -1;  //  reverse order comparator
            }
        });

        for (Player player : playerList) {
            System.out.println(player.username + " => " + player.score);

            JLabel usernameLabel = new JLabel(player.username, JLabel.TRAILING);
            panel.add(usernameLabel);

            JLabel scoreLabel = new JLabel(String.valueOf(player.score), JLabel.TRAILING);
            panel.add(scoreLabel);
        }

        ViewUtil.makeCompactGrid(panel,
                playerList.size(), 2, //rows, cols
                30, 20,        //initX, initY
                30, 20);       //xPad, yPad
        this.setContentPane(panel);
        this.pack();

        // center the form
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);

    }
}

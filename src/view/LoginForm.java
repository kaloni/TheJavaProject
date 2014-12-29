package view;

import org.apache.commons.io.IOUtils;
import server.Server;
import util.Network;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.*;


public class LoginForm extends JFrame {
    JDialog dialog;

    JTextField serverIpField;
    JTextField serverPortField;

    JTextField usernameField;
    JPasswordField passwordField;

    public LoginForm() {
        super();
        this.setTitle("Login or Sign Up");
        this.setVisible(false);
        this.setLayout(null);
        this.setResizable(false);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new SpringLayout());

        JLabel serverIPLabel = new JLabel("Server IP", JLabel.TRAILING);
        panel.add(serverIPLabel);
        serverIpField = new JTextField(Network.getMyIp(), 10);
        serverIPLabel.setLabelFor(serverIpField);
        panel.add(serverIpField);

        JLabel serverPortLabel = new JLabel("Server Port", JLabel.TRAILING);
        panel.add(serverPortLabel);
        serverPortField = new JTextField(String.valueOf(Server.DEFAULT_SERVER_PORT), 10);
        serverPortLabel.setLabelFor(serverPortField);
        panel.add(serverPortField);


        JLabel usernameLabel = new JLabel("Username", JLabel.TRAILING);
        panel.add(usernameLabel);
        usernameField = new JTextField(10);
        usernameLabel.setLabelFor(usernameField);
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password", JLabel.TRAILING);
        panel.add(passwordLabel);
        passwordField = new JPasswordField(10);
        passwordLabel.setLabelFor(passwordField);
        panel.add(passwordField);

        JButton signUpButton = new JButton("Sign Up");
        signUpButton.addActionListener(signUpActionPerformed);
        panel.add(signUpButton);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(loginActionPerformed);
        panel.add(loginButton);

        makeCompactGrid(panel,
                5, 2, //rows, cols
                7, 7,        //initX, initY
                7, 7);       //xPad, yPad
        this.setContentPane(panel);
        this.pack();
        this.setVisible(false);

        // center the form
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
    }


    private void showDialog(String str){
        JPanel panel = new JPanel(new GridBagLayout());
        panel.add(new JLabel(str + "\n"+" please Wait..."), new GridBagConstraints());
        dialog = new JDialog();
        dialog.getContentPane().removeAll();
        dialog.getContentPane().add(panel);
        dialog.setSize(200  , 100);
        dialog.setLocationRelativeTo(LoginForm.this);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setModal(true);
        dialog.setVisible(true);
    }

    private void hideDialog() {
        if (dialog != null) {
            dialog.dispose();
        }
    }

    private final ActionListener loginActionPerformed = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("username: " + usernameField.getText() + ", password: " + passwordField.getText());

            Thread t = new Thread(){
                public void run(){
                    try {
                        URI uri = Network.loginUriCreator(serverIpField.getText(),
                                Integer.valueOf(serverPortField.getText()),
                                usernameField.getText(),
                                passwordField.getText());

                        URL url = uri.toURL();
                        URLConnection conn = url.openConnection();
                        InputStream is = conn.getInputStream();

                        StringWriter writer = new StringWriter();
                        IOUtils.copy(is, writer);
                        String response = writer.toString();

                        System.out.println(response);
                    } catch (URISyntaxException | IOException e1) {
                        e1.printStackTrace();
                    }

                    SwingUtilities.invokeLater(new Runnable(){//do swing work on EDT
                        public void run(){
                            hideDialog();
                        }
                    });
                }
            };
            t.start();

            showDialog("Login in...");

        }
    };

    private final ActionListener signUpActionPerformed = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("username: " + usernameField.getText() + ", password: " + passwordField.getText());

            Thread t = new Thread(){
                public void run(){
                    try {
                        URI uri = Network.signUpUriCreator(serverIpField.getText(),
                                Integer.valueOf(serverPortField.getText()),
                                usernameField.getText(),
                                passwordField.getText());

                        URL url = uri.toURL();
                        URLConnection conn = url.openConnection();
                        InputStream is = conn.getInputStream();

                        StringWriter writer = new StringWriter();
                        IOUtils.copy(is, writer);
                        String response = writer.toString();

                        System.out.println(response);
                    } catch (URISyntaxException | IOException e1) {
                        e1.printStackTrace();
                    }

                    SwingUtilities.invokeLater(new Runnable(){//do swing work on EDT
                        public void run(){
                            hideDialog();
                        }
                    });
                }
            };
            t.start();

            showDialog("Signing up...");

        }
    };


    private static SpringLayout.Constraints getConstraintsForCell(
            int row, int col,
            Container parent,
            int cols) {
        SpringLayout layout = (SpringLayout) parent.getLayout();
        Component c = parent.getComponent(row * cols + col);
        return layout.getConstraints(c);
    }

    public static void makeCompactGrid(Container parent,
                                       int rows, int cols,
                                       int initialX, int initialY,
                                       int xPad, int yPad) {
        SpringLayout layout;
        try {
            layout = (SpringLayout) parent.getLayout();
        } catch (ClassCastException exc) {
            System.err.println("The first argument to makeCompactGrid must use SpringLayout.");
            return;
        }

        //Align all cells in each column and make them the same width.
        Spring x = Spring.constant(initialX);
        for (int c = 0; c < cols; c++) {
            Spring width = Spring.constant(0);
            for (int r = 0; r < rows; r++) {
                width = Spring.max(width,
                        getConstraintsForCell(r, c, parent, cols).
                                getWidth());
            }
            for (int r = 0; r < rows; r++) {
                SpringLayout.Constraints constraints =
                        getConstraintsForCell(r, c, parent, cols);
                constraints.setX(x);
                constraints.setWidth(width);
            }
            x = Spring.sum(x, Spring.sum(width, Spring.constant(xPad)));
        }

        //Align all cells in each row and make them the same height.
        Spring y = Spring.constant(initialY);
        for (int r = 0; r < rows; r++) {
            Spring height = Spring.constant(0);
            for (int c = 0; c < cols; c++) {
                height = Spring.max(height,
                        getConstraintsForCell(r, c, parent, cols).
                                getHeight());
            }
            for (int c = 0; c < cols; c++) {
                SpringLayout.Constraints constraints =
                        getConstraintsForCell(r, c, parent, cols);
                constraints.setY(y);
                constraints.setHeight(height);
            }
            y = Spring.sum(y, Spring.sum(height, Spring.constant(yPad)));
        }

        //Set the parent's size.
        SpringLayout.Constraints pCons = layout.getConstraints(parent);
        pCons.setConstraint(SpringLayout.SOUTH, y);
        pCons.setConstraint(SpringLayout.EAST, x);
    }
}

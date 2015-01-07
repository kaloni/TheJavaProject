package view;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import server.Server;
import util.Network;
import util.ViewUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;


public class LoginForm extends JFrame {
    JDialog dialog;

    JTextField serverIpField;
    JTextField serverPortField;

    JTextField usernameField;
    JPasswordField passwordField;

    private LoginListener loginListener;

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

        ViewUtil.makeCompactGrid(panel,
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

    private void showAlertDialog(String title, String message) {
        JOptionPane.showMessageDialog(this,
                message,
                title,
                JOptionPane.PLAIN_MESSAGE);
    }

    private void showProgressDialog(String str) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.add(new JLabel(str + "\n" + " please Wait..."), new GridBagConstraints());
        dialog = new JDialog();
        dialog.getContentPane().removeAll();
        dialog.getContentPane().add(panel);
        dialog.setSize(200, 100);
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

            Thread t = new Thread() {
                public void run() {
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

                        JSONObject jsonObject = new JSONObject(response);
                        System.out.println(response);

                        String responseValue = jsonObject.getString("response");

                        boolean loginSuccessfully;
                        if (responseValue.compareTo("OK") ==0) {
                            showAlertDialog("Login", "Login successful!");
                            loginSuccessfully = true;
                        } else {
                            String reasonValue = jsonObject.getString("reason");
                            showAlertDialog("Login", "Login Failed!\n" + reasonValue);
                            loginSuccessfully = false;
                        }

                        if (loginListener != null) {
                            loginListener.login(loginSuccessfully);
                        }
                    } catch (URISyntaxException | IOException e1) {
                        loginListener.login(false);
                        e1.printStackTrace();
                    }

                    SwingUtilities.invokeLater(new Runnable() {//do swing work on EDT
                        public void run() {
                            hideDialog();
                        }
                    });
                }
            };
            t.start();

            showProgressDialog("Login in...");

        }
    };

    private final ActionListener signUpActionPerformed = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("username: " + usernameField.getText() + ", password: " + passwordField.getText());

            Thread t = new Thread() {
                public void run() {
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

                        showAlertDialog("Signup", response);
                        System.out.println(response);

                    } catch (URISyntaxException | IOException e1) {
                        e1.printStackTrace();
                    }

                    SwingUtilities.invokeLater(new Runnable() {//do swing work on EDT
                        public void run() {
                            hideDialog();
                        }
                    });
                }
            };
            t.start();

            showProgressDialog("Signing up...");

        }
    };

    public void setLoginListener(LoginListener loginListener) {
        this.loginListener = loginListener;
    }


    public interface LoginListener {
        void login(boolean loginSuccessful);
    }
}




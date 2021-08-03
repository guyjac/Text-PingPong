import com.google.gson.Gson;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class JavaClient_GUI {

    JLabel enter_comm, playerid, activePlayers, ballLabel, instruc, balldraw;
    private Socket socket;
    private final int port = 8888;
    private final String hostname = "localhost";
    private int id;
    private int ballLocation;
    private static boolean connected = false;
    private Set<Integer> currentPlayerSet = new HashSet<Integer>();


    public static void main(String[] args) {
        JavaClient_GUI jc_gui = new JavaClient_GUI();

        if(connected) {
            new Thread(jc_gui::RecieveMessage).start();
        }
    }

    public JavaClient_GUI(){
        drawGui();
        init();
        connected = true;
    }

    private void init() {
        try {
            socket = new Socket(hostname, port);
            instruc.setText("Instructions: To pass the ball to a player, use the command 'pass ID'");
            connected = true;
        }
        catch (Exception e) {
            //System.out.println(e);
            System.out.println("Unable to contact server. Program exiting.");
        }
    }

    /*
     * Responsible for receiving info from server.
     * Player ID is only transmitted once as it cant change.
     * All info is in GSON format.
     * If the received player set is different to client version, print out all info (thus new player info)
     */
    private void RecieveMessage() {
        try {

            Gson gson_reader = new Gson();
            Scanner reader = new Scanner(socket.getInputStream());

            id = gson_reader.fromJson(reader.nextLine(), int.class); //We always get the ID first.//We always get the ID first.
            playerid.setText("You are player [" + id + "]! \n");

            while (true) {

                int ball = gson_reader.fromJson(reader.nextLine(), int.class); //Fetch ball location
                if (ballLocation != ball) {
                    ballLabel.setText("Player " + ball + " has the ball!");
                    ballLocation = ball;
                }

                Set cps = gson_reader.fromJson(reader.nextLine(), Set.class); //Fetch players online

                if (!currentPlayerSet.equals(cps)) { //Print players out if different.

                    String t = SetToString(cps);
                    activePlayers.setText("Players online [ID]: " + t);
                    currentPlayerSet = cps;
                }

                //Paint GUI
                if(ballLocation == id){
                    balldraw.setForeground(Color.green);
                    balldraw.setAlignmentX(Component.LEFT_ALIGNMENT);
                }
                else{
                    balldraw.setForeground(Color.red);
                    balldraw.setAlignmentX(Component.RIGHT_ALIGNMENT);
                }
                balldraw.revalidate();
                balldraw.repaint();
            }
        }
        catch (Exception e) {
            System.out.println(e);
            System.out.println("Connection with server lost. Exiting program.");
            //System.exit(0);
        }
    }

    /*
     * Sends messages to the server. Takes input through console and converts to JSON
     */
    public void SendMessage(String usr_in) {
        try {

            Gson gson_writer = new Gson();
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            String usr_json = gson_writer.toJson(usr_in);
            writer.println(usr_json);

        }

        catch (Exception e) {
            //System.out.println(e);
            System.out.println("Unable to send message to server.");
        }
    }

    /*
     * GSON converts all hashsets ints to double, so we need to print them back as ints...
     */
    private String SetToString(Set<Double> x) {
        String output = "[";

        for (Double vals : x) {
            if(output.length() != 1) //Check if there is more than one player, in which case we need commas
                output += ", ";

            output += (int)Math.round(vals);
        }

        output += "]";
        return output;
    }

    private void drawGui() {
        JFrame frame = new JFrame("JavaClient GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(410,300);

        //Panels
        JPanel bot_panel = new JPanel();
        JPanel top_panel = new JPanel();
        JPanel ball_panel = new JPanel();


        //send button and text field
        JTextField userInput = new JTextField(10);
        JButton send = new JButton("Send");

        //Button listener. send command and clear text
        send.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                SendMessage(userInput.getText());
                userInput.setText("");
            }
        });

        // Shows server output
        enter_comm = new JLabel();
        playerid = new JLabel();
        activePlayers = new JLabel();
        instruc = new JLabel();
        ballLabel = new JLabel();
        //ball gui
        balldraw = new JLabel("●"); // or letter O
        balldraw.setFont (balldraw.getFont ().deriveFont (18.0f));

        //center text
        playerid.setAlignmentX(Component.CENTER_ALIGNMENT);
        instruc.setAlignmentX(Component.CENTER_ALIGNMENT);
        instruc.setBorder(new EmptyBorder(0,0,30,0)); //padding
        activePlayers.setAlignmentX(Component.CENTER_ALIGNMENT);
        ballLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Create layout
        top_panel.setPreferredSize(new Dimension(410,200));
        ball_panel.setPreferredSize(new Dimension(310, 20));
        BoxLayout boxlayout = new BoxLayout(top_panel, BoxLayout.Y_AXIS);
        top_panel.setLayout(boxlayout);
        BoxLayout ballBox = new BoxLayout(ball_panel, BoxLayout.Y_AXIS);
        ball_panel.setLayout(ballBox);

        //Set colour
        top_panel.setOpaque(true);
        top_panel.setBackground(Color.white);

        //Add to panels
        ball_panel.add(balldraw);
        bot_panel.add(enter_comm);
        bot_panel.add(userInput);
        bot_panel.add(send);
        top_panel.add(instruc);
        top_panel.add(playerid);
        top_panel.add(activePlayers);
        top_panel.add(ballLabel);

        //Adding Components to the frame.
        frame.getContentPane().add(BorderLayout.NORTH, top_panel);
        frame.getContentPane().add(BorderLayout.CENTER, ball_panel);
        frame.getContentPane().add(BorderLayout.SOUTH, bot_panel);
        frame.pack();
        frame.setVisible(true);

    }

}

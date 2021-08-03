import com.google.gson.Gson;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class JavaClient {


    private Socket socket;
    private final int port = 8888;
    private final String hostname = "localhost";
    private int id;
    private int ballLocation;
    private static boolean connected = false;
    private Set<Integer> currentPlayerSet = new HashSet<Integer>();

    /*
    * Creates a new client object.
    * Create a thread for sending information outbound
    * Creates a thread for receiving information.
    */
    public static void main(String[] args) {
        JavaClient jc = new JavaClient();

        if(connected) {
            new Thread(jc::SendMessage).start();
            new Thread(jc::RecieveMessage).start();
        }

    }

    public JavaClient() {
        init();
    }

    /*
    * Attempts to initiate a connection.
    */
    private void init() {
        try {
            socket = new Socket(hostname, port);
            System.out.println("Instructions: To pass the ball to a player, use the command 'pass ID'");
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
            System.out.println("You are player [" + id + "]! \n");

            while (true) {

                int ball = gson_reader.fromJson(reader.nextLine(), int.class); //Fetch ball location
                if (ballLocation != ball) {
                    System.out.println("Player " + ball + " has the ball!");
                    ballLocation = ball;
                }

                Set cps = gson_reader.fromJson(reader.nextLine(), Set.class); //Fetch players online

                if (!currentPlayerSet.equals(cps)) { //Print players out if different.

                    String t = SetToString(cps);
                    System.out.println("Players online [ID]: " + t);
                    currentPlayerSet = cps;
                }

            }
        }
        catch (Exception e) {
            //System.out.println(e);
            System.out.println("Connection with server lost. Exiting program.");
            System.exit(0);
        }
    }

    /*
    * Sends messages to the server. Takes input through console and converts to JSON
    */
    public void SendMessage() {
        try {
            Gson gson_writer = new Gson();
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            while (true) {
                Scanner usr_in = new Scanner(System.in);
                String usr_json = gson_writer.toJson(usr_in.nextLine());
                writer.println(usr_json);
            }
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

}


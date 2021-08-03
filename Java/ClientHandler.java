import com.google.gson.Gson;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class ClientHandler {

    private final Socket socket;
    Scanner reader;
    private int playerID;
    private boolean RunThread = true;
    private Game game;
    static int pcounter;


    /*
     * Create our ClientHandler when a client connects. Runs addplayer to init player
     */
    public ClientHandler(Socket socket, Game game) {
        this.socket = socket;
        this.game = game;
        AddPlayer();
    }

    /**
     * Assigns a unique player ID from pccounter, a sync static var.
     * Adds the player to the the set of current players, and checks if they are the first player to join.
     */
    private synchronized void AddPlayer() {
        this.playerID = ++pcounter;
        game.addPlayer(this.playerID);
        System.out.println("Player " + playerID + " has joined the game!");
        firstPlayer();
    }

    /*
    * Checks if it is the first player to join
    */
    private synchronized void firstPlayer() {
        if (game.getBallLocation() == -1) {
            game.setBallLocation(playerID);
        }
    }

    /*
     * Sends data to client constantly. ID is only sent once.
     * Information is passed through GSON
     * Stops executing when RunThread is false. This bool is handled by ReceiveMessage.
     */
    public void SendMessage() {
        try {
            Gson gson_writer = new Gson();
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            String pid = gson_writer.toJson(playerID);
            writer.println(pid); //Sends playerID

            while (RunThread) {
                String ball_loc = gson_writer.toJson(game.getBallLocation());
                writer.println(ball_loc); //Sends ball location
                String play_set = gson_writer.toJson(game.getPlayerSet());
                writer.println(play_set); //Send current set of players, in GSON.
                Thread.sleep(1000);
            }
        }
        catch (Exception e) {
            //System.out.println(e);
            System.out.println("Unable to send message to client");
        }
    }


    /*
     * Responsible for fetching information from clients.
     * User inputs are placed into substrings for processing.
     * Prints new joining clients to server console.
     * Kills send message (by setting RunThread to false) if a client dirty quits.
     */
    public void ReceiveMessage() {

        try {
            while (RunThread) {
                Gson gson_reader = new Gson();
                reader = new Scanner(socket.getInputStream());
                String input = gson_reader.fromJson(reader.nextLine(), String.class);

                try {

                    String[] substrings = input.split(" ");

                    String command = substrings[0].toLowerCase();
                    int destination; //Set the destination to 0 by default.

                    if (substrings[1].matches("[0-9]+")) { //Use regex to check if the destination is an int
                        destination = Integer.parseInt(substrings[1]);
                    }
                    else{
                        throw new ArithmeticException(); //If its not an int, throw an exception
                    }

                    switch (command) {
                        case "pass":
                            if (playerID == game.getBallLocation()) { //Only allow passing if they have the ball
                                game.setBallLocation(destination);
                            } else {
                                System.out.println("Player " + playerID + " isnt allowed to move the ball");
                            }
                            break;
                    }
                }
                catch (Exception e){
                    System.out.println("Invalid command received");
                }
            }
        }
        catch (Exception e) {
            RunThread = false; //If client quits, then this goes to false. Stops SendMessage thread too.
            System.out.println("Player " + playerID + " has disconnected!");
            killPlayer();
            System.out.println("Players online [ID]: " + game.getPlayerSet()); //Print current players
        }
    }




    /*
     * Invoked by ReceiveMessage on exception. Removes a player from the set.
     * Also checks if they had the ball. If so, random re-assign.
     */
    public void killPlayer() {

        game.removePlayer(playerID);
        game.PlayerLeft(playerID); //Verify disconnected player didn't have the ball...

    }

}


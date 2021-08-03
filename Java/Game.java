import java.util.*;

public class Game {

    private int ballLocation = -1;
    private Set<Integer> currentPlayerSet = new HashSet<Integer>();

    /*
    * Set ball location. Checks if the recipient ID is actually in the game.
    * If so, pass the ball. Else, reject movement.
    */
    public synchronized void setBallLocation(int id) {
        if (currentPlayerSet.contains(id)) {
            ballLocation = id;
            System.out.println("Player " + id + " has the ball");
        }
        else {
            System.out.println("Invalid id received.");
        }
    }

    /*
    * Used to reset the ball in the event that all players quit the game.
    */
    public synchronized void resetBall(){
        ballLocation = -1;
        System.out.println("All have clients left. Ball reset.");
    }


    /*
    * Validates if the leaving player had the ball. If so, moves to a random player.
    * If no players are left, the ball will remain at -1 and thus will reset.
    */
    public void PlayerLeft(int id) {
        if (ballLocation == id) { //Check if the player holding the ball left the game!
            System.out.println("Player " + id + " left. Ball automatically moved.");

            int random_player = -1;

            for (int x : currentPlayerSet) {
                random_player = x;
            }

            if(random_player != -1){ //Pass the ball to a random player!
                setBallLocation(random_player);
            }
            else {
                resetBall();//If no players are found, reset the ball.
            }
        }
    }

    //Getters
    public int getBallLocation() {
        return ballLocation;
    }

    public Set<Integer> getPlayerSet() {
        return currentPlayerSet;
    }

    //Setters
    public void addPlayer(int a) {
        if(a > 0) {
            currentPlayerSet.add(a);
        }
    }

    public void removePlayer(int a) {
        currentPlayerSet.remove(a);
    }
}

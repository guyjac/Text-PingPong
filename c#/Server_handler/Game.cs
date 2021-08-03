using System;
using System.Collections.Generic;


namespace ass1
{
    public class Game
    {

        private int ballLocation = -1;
        private HashSet<int> currentPlayerSet = new HashSet<int>();
        static object Lock = new object();

        /*
        * Set ball location. Checks if the recipient ID is actually in the game.
        * If so, pass the ball. Else, reject movement.
        */ //Todo: sync this thread
        public void setBallLocation(int id)
        {
            lock (Lock)
            {
                if (currentPlayerSet.Contains(id))
                {
                    ballLocation = id;
                    Console.WriteLine("Player " + id + " has the ball");
                }
                else
                {
                    Console.WriteLine("Invalid pass received");
                }
            }
        }

        /*
        * Used to reset the ball in the event that all players quit the game.
        */
        public void resetBall()
        {
            ballLocation = -1;
            Console.WriteLine("All have clients left. Ball reset.");
        }

        /*
         * Validates if the leaving player had the ball. If so, moves to a random player.
         * If no players are left, the ball will remain at -1 and thus will reset.
         */
        public void PlayerLeft(int id)
        {
            if (ballLocation == id)
            {
                Console.WriteLine("Player " + id + " left. Ball automatically moved.");
                int random_player = -1;

                foreach (int x in currentPlayerSet) {
                    random_player = x;
                }

                if (random_player != -1)
                {
                    setBallLocation(random_player);
                }
                else
                {
                    resetBall();
                }
            }
        }


        //getters
        public int getBallLocation()
        {
            return ballLocation;
        }

        public HashSet<int> getPlayerSet()
        {
            return currentPlayerSet;
        }

        //setters
        public void addPlayer(int a)
        {
            if (a > 0)
            {
                currentPlayerSet.Add(a);
            }
        }

        public void removePlayer(int a)
        {
            currentPlayerSet.Remove(a);
        }
    }
}

using System;
using System.IO;
using System.Net.Sockets;
using System.Text.RegularExpressions;
using System.Threading;
using Newtonsoft.Json;

namespace ass1
{
    public class ClientHandler
    {

        private TcpClient tcpclient;

        private int playerID;
        private Boolean RunThread = true;
        private Game game;

        static object Lock = new object();

        private static int pcounter;

        /*
         * Create our ClientHandler when a client connects. Runs addplayer to init player
         */
        public ClientHandler(TcpClient tcpclient, Game game)
        {
            this.tcpclient = tcpclient;
            this.game = game;
            AddPlayer();
        }

        /**
         * Assigns a unique player ID from pccounter, a sync static var.
         * Adds the player to the the set of current players, and checks if they are the first player to join.
         */
        public void AddPlayer()
        {
            lock (Lock)
            {
                this.playerID = ++pcounter;
                game.addPlayer(this.playerID);
                Console.WriteLine("Player " + playerID + " has joined the game!");
                FirstPlayer();
            }
        }

        /*
        * Checks if it is the first player to join
        */
        public void FirstPlayer()
        {
            lock (Lock)
            {
                if (game.getBallLocation() == -1)
                {
                    game.setBallLocation(playerID);
                }
            }
        }
        /*
        * Invoked by ReceiveMessage on exception. Removes a player from the set.
        * Also checks if they had the ball. If so, random re-assign.
        */
        public void KillPlayer()
        {
            game.removePlayer(playerID);
            game.PlayerLeft(playerID); 
        }

        /*
          * Sends data to client constantly. ID is only sent once.
          * Information is passed through GSON
          * Stops executing when RunThread is false. This bool is handled by ReceiveMessage.
          */
        public void SendMessage()
        {

            try
            {
                StreamWriter writer = new StreamWriter(tcpclient.GetStream());
                string json_pid = JsonConvert.SerializeObject(playerID);
                writer.WriteLine(json_pid);

                while (RunThread)
                {
                    string ball_loc = JsonConvert.SerializeObject(game.getBallLocation());
                    writer.WriteLine(ball_loc);
                    string play_Set = JsonConvert.SerializeObject(game.getPlayerSet());
                    writer.WriteLine(play_Set);
                    writer.Flush();
                    Thread.Sleep(1000);
                }
               

            }
            catch (Exception e)
            {
                //Console.WriteLine(e);
                Console.WriteLine("Unable to send message to client");
            }
        }

        /*
         * Responsible for fetching information from clients.
         * User inputs are placed into substrings for processing.
         * Prints new joining clients to server console.
         * Kills send message (by setting RunThread to false) if a client dirty quits.
         */
        public void ReceiveMessage()
        {

            try
            {

                while (RunThread)
                {

                    StreamReader reader = new StreamReader(tcpclient.GetStream());
                    String input = JsonConvert.DeserializeObject<string>(reader.ReadLine());

                    try
                    {

                        String[] subStrings = input.Split(' ');

                        String command = subStrings[0].ToLower();
                        int destination;

                        //Ensure we only allow integers for the second substr
                        Regex int_only = new Regex("^[0-9]+$");
                        if (int_only.IsMatch(subStrings[1]))
                        {
                            destination = int.Parse(subStrings[1]);
                        }
                        else
                        {
                            throw new ArithmeticException();
                        }

                        switch (command)
                        {
                            case "pass":
                                if (playerID == game.getBallLocation())
                                {
                                    game.setBallLocation(destination);
                                }
                                else
                                {
                                    Console.WriteLine("Player " + playerID + " isnt allowed to move the ball");
                                }
                                break;
                        }
                    }
                    catch (Exception e)
                    {
                        Console.WriteLine("Invalid command received");
                    }

                }

            }

            catch (Exception e)
            {
                RunThread = false;
                Console.WriteLine("Player " + playerID + " has disconnected!");
                KillPlayer();
                Console.WriteLine("Players online [ID]: [" + String.Join(", ", game.getPlayerSet()) + "]"); //Prints out set to string
            }

        }




    }
}

using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;

namespace ass1
{

    /*
    * Declare our port server and create an instance of Game. Launch server in PSVM.
    */
    class Server
    {
        private const int port = 8888;
        private static readonly Game game = new Game();

        static void Main(string[] args)
        {
            RunServer();
        }

        /*
         * Launches server and listens for incoming connections.
         * Places new clients into an arrayList, and creates two threads. One for incoming and one for outgoing.
         */
        private static void RunServer()
        {
            TcpListener listener = new TcpListener(IPAddress.Loopback, port);
            listener.Start();

            Console.WriteLine("Waiting for incoming connections...");

            List<ClientHandler> th = new List<ClientHandler>();
            int i = 0;

            while (true)
            {
                TcpClient tcpClient = listener.AcceptTcpClient();
                th.Add(new ClientHandler(tcpClient, game));
                new Thread(th[i].ReceiveMessage).Start();
                new Thread(th[i].SendMessage).Start();
                i++;

            }

        }

        public static void console_out(string x)
        {
            Console.WriteLine(x);
        }


    }
}

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/*
* Declare our port server and create an instance of Game. Launch server in PSVM.
*/
public class Server {

    private final static int port = 8888;
    private static final Game game = new Game();

    public static void main(String[] args) {
        RunServer();
    }

/*
 * Launches server and listens for incoming connections.
 * Places new clients into an arrayList, and creates two threads. One for incoming and one for outgoing.
 */
    private static void RunServer() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Waiting for incoming connections... \n");

            List<ClientHandler> th = new ArrayList<ClientHandler>();
            int i = 0;

            while (true) {
                Socket socket = serverSocket.accept();

                th.add(new ClientHandler(socket, game));
                new Thread(th.get(i)::SendMessage).start();
                new Thread(th.get(i)::ReceiveMessage).start();
                i++;

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

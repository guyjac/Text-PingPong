- It is a client-server application. The client(s) initiates the connection to the server.
- The protocol used for data transmission (JSON) is text based. This allows for cross platform talk
- The server initiates and maintains a constant stream of data to the user. The user may issue commands to the server at any point during the connection. Clients issuing commands may alter the response from the server to the client
- The protocol does not include any authentication. It could be used in the real world (as it is used in a game), but would be susceptible to manipulation
- Each client command consists of a single line of text, serialised in JSON
- Server data transmitted to the client consists binary object types serialised into text-based JSON
- In the case of an error, the server prints out &quot;Invalid&quot;

The following is sent to the client upon connection (sent only once):

| Server sent | Expected behaviour: | Object type |
| --- | --- | --- |
| (On connection) Player ID | Connecting player is assigned an ID. Both player and server output the connecting ID to the console. The ID is added to  CurrentPlayerSet | Integer |

Client commands and responses:

| Client request | Expected behaviour: | Object type |
| --- | --- | --- |
| Pass  Player ID | The server will change the location of the ball to the received ID. The server and all connected clients will be informed of the pass. | String |
| Pass   Player ID z\* | The ball will not change location. The server will output a warning message. | String |

The following is sent to all client(s) every 1000ms:

| Server sent | Expected behaviour: | Object type |
| --- | --- | --- |
|  CurrentPlayerSet | All connected client(s) will output the contents  CurrentPlayerSet if the value is changed (Eg New player connection) | Array (JSON) |
|  BallLocation | All connected client(s) will output  BallLocation if the value is changed (Eg the ball has been passed) | Integer |

\*z representing an invalid player ID (E.g attempting to pass to someone not in the game)

# Server threads

There are four separate threads running in the Server class:

- The Main thread
  - Initiates when the server program is launched
  - Creates a single instance of Object game
  - Initiates a ServerSocket and listens for incoming clients
  - Any incoming clients are accepted and added to an ArrayList of clients
  - Creates an instance of ClientHandler for each connecting client and assigns the connecting player a unique ID and adds it to a Set within the instance of game
  - Is responsible for starting SendMessage and ReceiveMessage threads
  - Terminates when the application is killed
- SendMessage Thread
  - An instance of the SendMessage thread is created for each connecting user, following the creation of a ClientHandler instance for the user
  - The main body of the method is in a while loop, allowing continuous output
  - Opens a writer on a socket
  - Sends the integer variable playerID to the client upon connection
  - Sends the variables BallLocation (integer representing location of ball) and CurrentPlayerSet (Set of connected players) every 1000ms
  - Terminates when Boolean RunThread is false
- ReceiveMessage Thread
  - An instance of the ReceiveMessage thread is created for each connecting user, following the creation of a ClientHandler instance for the user
  - The main body of the method is in a while loop, allowing continuous input
  - Opens a scanner on a socket
  - Listens for incoming commands from the client and executes said commands once received
  - Throws exceptions upon client disconnect, and sets Boolean RunThread to false, killing SendMessage and ReceiveMessage threads. Also removes player from CurrentPlayerSet upon exception
  - Terminates following exception (from client disconnect).

# Client threads

There are three separate threads running in the Client class:

- The Main thread
  - Initiates when the Client program is launched
  - Creates a socket connection to the specified server
- SendMessage Thread
  - The SendMessage thread is started following a successful connection to a server
  - A writer is opened on the socket
  - The main body of the method is in a while loop, allowing continuous output
  - Thread enters a blocking state whilst awaiting for the user to input a command
  - Entered commands are serialised in JSON and sent over the writer
  - Terminates following the closure of program
- ReceiveMessage Thread
  - The ReceiveMessage thread is started following a successful connection to a server
  - The main body of the method is in a while loop, allowing continuous input
  - Listens for incoming data from the server
  - Receives the PlayerID variable from the server (received only once) and assigns it to the client
  - Receives the BallLocation and CurrentPlayerSet variables every 1000ms
  - Prints aforementioned variables on clients console if the variables data have changed (since the last received version)
  - Terminates following closure of program

<!--
# Project review

Overall, I found the project to be perfectly balanced; it was not easy enough that I could complete it in a day, nor was it too hard that completion was unattainable.

The majority of the tasks assigned within the project, such as setting up network readers and writers were complimented by the materials that was studied in both the lectures and labs. This ensured that I was already familiar and comfortable with implementing the required specifications.

The area that I found particularly challenging was ensuring that the client always received up-to-date information from the server. When I first started designing my client program, I had imagined it having a single thread for both sending and receiving data. However, whilst a scanner is waiting for an input, it causes a blocking state. This led to my client not receiving new data from the server. It took a considerable amount of reading to realise that my design was flawed and that I needed to change my code. In the end, I managed to resolve the issue by separating sending and receiving into separate threads.

I am particularly excited by the fact that my program works cross-platform, that is, a Java client can connect to a C# server. I was able to achieve this functionality by serialising all outgoing and incoming data with JSON within my projects.

As far as I am aware, there are no issues with the quality or functionality of my program. I have made it as concise and legible as possible, and made use of comments where they are required.

If I was to undertake the project again, I would first plan out the structure of the program using a UML class diagram. This would allow me to plan how methods, variables and sockets would communicate between classes and processes without the need for trial and error. I would also utilise a software such as Jira to help manage and keep track of tasks.

Overall I believe this assignment has been a success and I am very proud of the work I have achieved. It has been a great learning experience to introduce me to both C# and multi-threading.

# References

[1] _802-2014 - IEEE Standard for Local and Metropolitan Area Networks: Overview and Architecture - IEEE Standard_. [Online]. Available: https://ieeexplore.ieee.org/document/6847097. [Accessed: 25-Nov-2019].

-->

package Server;

import Game.DebugLogger;
import Game.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Thread for each client, in which the requested input is recieved by server and
 *  sent to Common for processing in the critical section
 * ClientHandler is also saved as an ArrayList in Server to iterate through, for the
 *  server to broadcast to each client
 */
class ClientHandler implements Runnable {

    //TODO: handling of when a client leaves the game.
    //TODO: Next time, plan for having to do the entire project myself...

    final Socket clientSocket;
    private final BufferedReader in;
    private final PrintWriter out;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void run() {
        try {
            while (true){

                String inputLine;
                while ((inputLine = in.readLine()) != null) {

                    if(Server.clientMap.get(clientSocket) != null)
                        DebugLogger.logServer("[" + Server.clientMap.get(clientSocket).getName() + "]: " + inputLine);

                    Server.receiveMessage(inputLine, clientSocket);
                }
            }
        }
        catch (IOException e) {
            DebugLogger.log(e.getMessage());
        }
        finally {
            try {
                in.close();
                out.close();
                clientSocket.close();
            }
            catch (IOException e) {
                DebugLogger.log(e.getMessage());
            }
        }
    }

}

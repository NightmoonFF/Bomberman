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

    final Socket clientSocket;
    private final BufferedReader in;
    private final PrintWriter out;
    public String clientName; //todo: to be removed? get player.name
    public Player clientPlayer; //todo: to be moved to map on server

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        this.clientName = socket.getInetAddress().toString();
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
                    DebugLogger.logServer("[" + clientName + "]: " + inputLine);
                    System.out.println("[" + clientName + "]: " + inputLine);
                    Server.receiveMessage(inputLine, clientSocket);

                    //on join, set playerName
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

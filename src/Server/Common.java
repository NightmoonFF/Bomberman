package Server;

import Game.DebugLogger;
import Game.GameLogic;
import Game.Player;
import Game.PlayerPosition;

import java.net.Socket;
import java.util.Objects;

public class Common {
    private static Common instance;
    private Common() {} // private constructor for singleton


    /**
     * Ensures a singleton pattern
     * @return the instance if not already claimed
     */
    public static synchronized Common getInstance() {
        if (instance == null) {
            instance = new Common();
        }
        return instance;
    }


    public static synchronized void handleInputRequest(String input, Socket clientSocket) {
        // Process the input
        processInput(input);
        // Apply input to the server's game
        updateGame(input, clientSocket);
        // Broadcast input to all clients
        broadcastInput(input);
    }


    /**
     * Method to process input such as input validation
     * @param input
     */
    private static void processInput(String input) {
        if (Server.gameStarted) {

        }
        //TODO: check if desired spawn location is valid on JOIN (not occupied by other player), or dont request position - only recieve by server
    }


    /**
     * Applies the client's input to the Game, <br>
     * according to protocol outlined in <br>
     * client_server_protocol.txt <br>
     * parts[0] - command <br>
     * parts[1] - parameter 1 <br>
     * parts[2] - parameter 2 <br>
     * Possibly entirely identical with updateGame() in Client <br>
     * @param input the clients input request
     */
    private static void updateGame(String input, Socket clientSocket) {

        String[] parts = input.split(" "); //Split the input into command and parameters
        String command = parts[0];

        switch (command) {
            case "JOIN":
                DebugLogger.logServer("Attempting to create player: " + parts[1]);

                Player clientPlayer = GameLogic.makePlayer(parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
                Server.addClient(clientSocket, clientPlayer);

                //TODO: for each existing player already connected, have new client create those as well
                //targeted message, or broadcast with if statement on client-side? broadcast more secure,
                //in case existing client has misssed a message for creation previously
                break;
            case "MOVE":
                switch(parts[1]){
                    //TODO: remove double-switch
                    case "up": GameLogic.updatePlayer(Objects.requireNonNull(GameLogic.getPlayerByName(parts[2])), 0, -1, "up");
                    break;
                    case "down": GameLogic.updatePlayer(Objects.requireNonNull(GameLogic.getPlayerByName(parts[2])), 0, +1, "down");
                    break;
                    case "left": GameLogic.updatePlayer(Objects.requireNonNull(GameLogic.getPlayerByName(parts[2])), -1, 0, "left");
                    break;
                    case "right": GameLogic.updatePlayer(Objects.requireNonNull(GameLogic.getPlayerByName(parts[2])), +1, 0, "right");
                    break;
                }
                break;
            case "BOMB":

                break;

            default:
                DebugLogger.logServer("Warning: Unknown Message Received! \"" + input + "\"");
                break;
        }
    }


    private static void broadcastInput(String input) {
        Server.broadcast(input);
    }

}

package Server;

import Game.DebugLogger;
import Game.GameLogic;
import Game.PlayerPosition;

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


    public static synchronized void handleInputRequest(String input, ClientHandler clientHandler) {
        // Process the input
        processInput(input);
        // Apply input to the server's game
        updateGame(input, clientHandler);
        // Broadcast input to all clients
        broadcastInput(input);
    }


    /**
     * Method to process input such as input validation
     * @param input
     */
    private static void processInput(String input) {

    }


    /**
     * Applies the client's input to the Game, <br>
     * according to protocol outlined in <br>
     * client_server_protocol.txt <br>
     * parts[0] - command <br>
     * parts[1] - name <br>
     * parts[2] - parameter 1 <br>
     * Possibly entirely identical with updateGame() in Client <br>
     * @param input the clients input request
     */
    private static void updateGame(String input, ClientHandler clientHandler) {

        String[] parts = input.split(" "); //Split the input into command and parameters
        String command = parts[0];

        switch (command) {
            case "JOIN":
                DebugLogger.logServer("Attempting to create player: " + parts[1]);

                PlayerPosition p = GameLogic.getRandomFreePosition();
                clientHandler.clientPlayer = GameLogic.makePlayer(parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));

                clientHandler.clientName = parts[1];

                //TODO: for each existing player already connected, have new client create those as well

                break;
            case "MOVE":
                switch(parts[1]){
                    case "up": GameLogic.updatePlayer(clientHandler.clientPlayer, 0, -1, "up");
                    case "down": GameLogic.updatePlayer(clientHandler.clientPlayer, 0, +1, "down");
                    case "left": GameLogic.updatePlayer(clientHandler.clientPlayer, -1, 0, "left");
                    case "right": GameLogic.updatePlayer(clientHandler.clientPlayer, +1, 0, "right");
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

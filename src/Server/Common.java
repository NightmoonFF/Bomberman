package Server;

import Game.DebugLogger;
import Game.GameLogic;

public class Common {
    private static Common instance;


    private Common() {}


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
     * parts[0] - client name <br>
     * parts[1] - command <br>
     * parts[2] - parameter <br>
     * Possibly entirely identical with updateGame() in Client <br>
     * @param input
     */
    private static void updateGame(String input, ClientHandler clientHandler) {

        String[] parts = input.split(" "); //Split the input into command and parameters
        String command = parts[0];
        switch (command) {
            case "JOIN":
                System.out.println("Attempting to create player: " + parts[1]);

                clientHandler.clientPlayer = GameLogic.makePlayer(parts[1]);
                clientHandler.clientName = parts[1];

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
                DebugLogger.logServer("Unknown Message: " + input);
                break;
        }
    }


    private static void broadcastInput(String input) {
        Server.broadcast(input);
    }

}

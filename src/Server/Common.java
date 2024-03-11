package Server;

import Game.DebugLogger;
import Game.GameLogic;
import Game.Gui;
import Game.Player;

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


    public static synchronized void handleInputRequest(String input) {
        // Process the input
        processInput(input);
        // Apply input to the server's game
        updateGame(input);
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
    private static void updateGame(String input) {

        String[] parts = input.split(" "); //Split the input into command and parameters
        String command = parts[0];
        switch (command) {
            case "JOIN":
                System.out.println("Attempting to create player: " + parts[1]);
                GameLogic.makePlayer(parts[1]);

                break;
            case "MOVE":

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

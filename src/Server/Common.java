package Server;

import Game.DebugLogger;
import Game.GameLogic;
import Game.Player;

import java.net.Socket;
import java.util.Objects;

/**
 * This class is intended to be where the game's input is handled - as in, the specific instructions
 * that makes things happen, such as moving, or placing bombs. This class is identical between the
 * servers instance of the game, and the clients' instance of the game - hence the name "common".
 * It has been made into a singleton with the idea of ensuring it cannot be instantiated multiple times,
 * but is not needed due to being a static utility class - TODO: remove singleton pattern?
 */
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

    /**
     * Pre-requisite for allowing Clients to access updateGame                      <br>
     * Handles the incoming game requests by clients in seperate phases:            <br>
 *                                                                                  <p>
     * -Processing/Validation                                                       <br>
     * -Updating the servers Game Instance                                          <br>
     * -Broadcasting the Game input to everyone, allowing them to apply the update  <br>
     *
     * @param input incoming Request
     * @param clientSocket the Client who is requesting it
     */
    public static synchronized void handleInputRequest(String input, Socket clientSocket) {
        // Process the input
        processInput(input);
        // Apply input to the server's game
        updateGame(input, clientSocket);
        // Broadcast input to all clients
        broadcastInput(input);
    }


    /**
     * Method to process input such as doing validation
     * @param input
     */
    private static void processInput(String input) {
        //TODO: check if player is trying to move/bomb while dead, if yes, deny
        //TODO: similar checks
    }


    /**
     * Applies the client's request to the Game, according to protocol outlined in "client_server_protocol.txt" <br>
     *                                                                                                          <p>
     * parts[0] - command                                                                                       <br>
     * parts[x] - parameter x                                                                                   <br>
     *
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
                //in case existing client has missed a message for creation previously
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
                GameLogic.placeBomb(GameLogic.getPlayerByName(parts[1]));
                break;

            default:
                System.out.println("Warning: Unknown Message Received! \"" + input + "\"");
                DebugLogger.log("Warning: Unknown Message Received! \"" + input + "\"");
                break;
        }
    }

    /**
     * Sends the instructions applied to the game by the server to each client
     * @param input
     */
    private static void broadcastInput(String input) {
        Server.broadcast(input);
    }

}
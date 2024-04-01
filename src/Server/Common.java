package Server;

import Game.*;

import java.net.Socket;
import java.util.Objects;

/**
 * This class is intended to be where the game's input is handled - as in, the specific instructions
 * that makes things happen, such as moving, or placing bombs. This class is identical between the
 * servers instance of the game, and the clients' instance of the game - hence the name "common".
 * The setup of this is a little experimental, as we did not know the proper handling of this architechture.
 * Perhaps only the "updateGame" method should be located here, and the rest could be handled somewhere else.
 */
public class Common {

    /**
     * Used by Server                                                               <br>
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
        processInput(input, clientSocket);
        // Apply input to the server's game
        updateGame(input);
        // Broadcast input to all clients
        broadcastInput(input);
    }


    /**
     * Used by Server <br>
     * Method to process input such as doing validation
     * @param input input
     */
    private static void processInput(String input, Socket clientSocket) {

        String[] parts = input.split(" "); //Split the input into command and parameters
        String command = parts[0];

        switch (command) {
            case "JOIN":
                DebugLogger.log("Attempting to create player: " + parts[1]);

                Player clientPlayer = GameLogic.makePlayer(parts[1]);
                Server.addClient(clientSocket, clientPlayer);

                //TODO: broadcast spawn
                Position freePos = GameLogic.getRandomFreePosition();
                broadcastInput("SPAWN" + " " + freePos.getX() + " " + freePos.getY());

                //TODO: for each existing player already connected, have new client create those as well
                //targeted message, or broadcast with if statement on client-side? broadcast more secure,
                //in case existing client has missed a message for creation previously
                break;
        }

        //TODO: Check if requested player Username is already used - would break game due to getPlayerByName()
        //TODO: check if player is trying to make input requests while dead, if yes, deny
        //TODO: similar checks
    }



    /**
     * Used by Client + Server                                                                                  <br>
     * Applies the client's request to the Game, according to protocol outlined in "client_server_protocol.txt" <br>
     *                                                                                                          <p>
     * parts[0] - command                                                                                       <br>
     * parts[x] - parameter x                                                                                   <br>
     *
     * @param input the clients input request
     */
    public static void updateGame(String input) {

        String[] parts = input.split(" "); //Split the input into command and parameters
        String command = parts[0];

        switch (command) {
            case "SPAWN":
                GameLogic.spawnPlayer(GameLogic.getPlayerByName(parts[1]), new Position(Integer.parseInt(parts[2]), Integer.parseInt(parts[3])));

            case "MOVE":
                switch(parts[1]){
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
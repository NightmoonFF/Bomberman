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
        if (!processInput(input, clientSocket)) return;
        // Apply input to the server's game
        updateGame(input);
        // Broadcast input to all clients
        Server.broadcast(input);

    }


    /**
     * Used by Server <br>
     * Method to process input such as doing validation
     * @param input input
     */
    private static boolean processInput(String input, Socket clientSocket) {

        String[] parts = input.split(" "); //Split the input into command and parameters
        String command = parts[0];
        if (command.equals("JOIN")) {

            // Add client & player to server
            Player clientPlayer = GameLogic.makePlayer(parts[1]);

            Position freePos = GameLogic.getRandomFreePosition();
            clientPlayer.setPosition(freePos);
            clientPlayer.reset();

            Server.addClient(clientSocket, clientPlayer);

            // Notify all clients of new player and spawn
            Server.broadcast("MAKE" + " " + parts[1]);
            Server.broadcast("SPAWN" + " " + parts[1] + " " + freePos.getX() + " " + freePos.getY());

            // Send existing player data to the newly joined player
            for (Player otherPlayer : GameLogic.players) {
                if (!otherPlayer.equals(clientPlayer)) { // Exclude the newly joined player

                    Server.sendMessage("MAKE" + " " + otherPlayer.getName(), clientSocket);

                    System.out.println("Sending Make & Spawn instructions to " + clientPlayer.getName() + "for: " + otherPlayer.getName());
                }
            }

            return false;
        }



        // Deny game input requests if player is dead
        return !Server.clientMap.get(clientSocket).isDead();

        //TODO: Check if requested player Username is already used - would break game due to getPlayerByName()
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
            case "MAKE":
                GameLogic.makePlayer(parts[1]);
                break;
            case "SPAWN":
                GameLogic.spawnPlayer(GameLogic.getPlayerByName(parts[1]), new Position(Integer.parseInt(parts[2]), Integer.parseInt(parts[3])));
                break;
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
            case "START":
                Gui.setCanMove(true);
                break;
            case "COUNTER":
                Gui.setLabelText(parts[1]);
                break;
            default:
                System.out.println("Warning: Unknown Message Received! \"" + input + "\"");
                DebugLogger.log("Warning: Unknown Message Received! \"" + input + "\"");
                break;
        }
    }

}
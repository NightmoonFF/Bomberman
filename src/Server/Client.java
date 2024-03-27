package Server;

import Game.*;
import javafx.application.Application;
import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Objects;

/**
 * - Prompts user for name (not currently implemented) <br>
 * - Establishes connection to server <br>
 * - Starts a continuous input thread from server <br>
 * - Starts a continuous output thread to server <br>
 * - Starts JavaFx Game Thread <br>
 * - currently sends console userinput, not game input <br>
 */
public class Client {
    static Socket socket;
    static BufferedReader in;
    public static PrintWriter out;
    static BufferedReader consoleInput;
    static String ip;

    public Client(String ipAddress) {
        System.out.println(ipAddress);
        ipAddress = ip;
        initializeSocket();

        //Continuous thread for incoming messages by Server
        Thread inputThread = new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println("[SERVER]: " + line);
                    System.out.println("IP: " + Client.socket.getInetAddress()); //test
                    DebugLogger.log(line);

                    //processInput(line);
                    updateGame(line);
                }
            } catch (IOException e) {
                DebugLogger.log(e.getMessage());
            }
        });
        inputThread.start();

        //Continuous thread for outgoing messages to Server
        Thread outputThread = new Thread(() -> {
            try {
                String userInputLine;
                while ((userInputLine = consoleInput.readLine()) != null) {
                    out.println(userInputLine);
                    DebugLogger.log("@[Server]: " + userInputLine);
                }
            } catch (IOException e) {
                DebugLogger.log(e.getMessage());
            }
        });
        outputThread.start();
    }

    //-----------------------------------------------------------------------------------------------------------------

    private static void initializeSocket() {
        try {
//            if (ip == null) {
//                throw new IllegalArgumentException("most have ip garl");
//            }
            socket = new Socket(ip, 4969);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //From server
            out = new PrintWriter(socket.getOutputStream(), true); //From console input to server
            consoleInput = new BufferedReader(new InputStreamReader(System.in)); //From console input
            DebugLogger.log("Connection Established to host: " + socket.getInetAddress());

        } catch (IOException e) {
            DebugLogger.log(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //-----------------------------------------------------------------------------------------------------------------

    //TODO: make client use the Common class, which is what it's intended for? this method is a duplicate of Common.updateGame
    private void updateGame(String input){
        String[] parts = input.split(" "); //Split the input into command and parameters
        String command = parts[0];
        switch (command) {
            case "JOIN":
                DebugLogger.log("Attempting to create player: " + parts[1]);
                GameLogic.makePlayer(parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
                break;
            case "MOVE":
                switch(parts[1]){
                    //TODO: remove double-switch?
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
                DebugLogger.logServer("Unknown Message: " + input);
                break;
        }
    }

    public void sendMessage(String message){
        out.println(message);
    }
}

package Server;

import Game.DebugLogger;
import Game.GameLogic;
import Game.Gui;
import Game.Player;
import javafx.application.Application;
import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * - Prompts user for name (not currently implemented) <br>
 * - Establishes connection to server <br>
 * - Starts a continuous input thread from server <br>
 * - Starts a continuous output thread to server <br>
 * - Starts JavaFx Game Thread <br>
 * - currently sends console userinput, not game input <br>
 */
public class Client {
    public static String clientName = "Elias";
    static Socket socket;
    static BufferedReader in;
    public static PrintWriter out;
    static BufferedReader consoleInput;

    static {
        try{
            socket = new Socket("localhost", 4969);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //From server
            out = new PrintWriter(socket.getOutputStream(), true); //From console input to server
            consoleInput = new BufferedReader(new InputStreamReader(System.in)); //From console input
            DebugLogger.log("Connection Established to host: " + socket.getInetAddress());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {

        //Continuous thread for incoming messages by Server
        Thread inputThread = new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println("[SERVER]: " + line);
                    DebugLogger.log(line);

                    updateGame(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
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
                e.printStackTrace();
            }
        });
        outputThread.start();

        launchGame();
    }

    private static void launchGame(){
        boolean isServerInstance = false;
        boolean isDebugEnabled = true;


        String[] arguments = {String.valueOf(isServerInstance), String.valueOf(isDebugEnabled)};
        Application.launch(Gui.class, arguments);
    }

    private static void updateGame(String input){
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

    public static void sendMessage(String message){

    }
}

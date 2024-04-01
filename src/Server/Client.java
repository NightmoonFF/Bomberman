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

    static {
        try{
            socket = new Socket("localhost", 4969); //TODO: move to launcher prompt
            in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //From server
            out = new PrintWriter(socket.getOutputStream(), true); //From console input to server
            consoleInput = new BufferedReader(new InputStreamReader(System.in)); //From console input
            DebugLogger.log("Connection Established to host: " + socket.getInetAddress());

        } catch (IOException e) {
            DebugLogger.log(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Client() {

        //Continuous thread for incoming messages by Server
        Thread inputThread = new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    DebugLogger.log(line);

                    Common.updateGame(line);

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

    public void sendMessage(String message){
        out.println(message);
    }
}

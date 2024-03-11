package Server;

import Game.DebugLogger;
import Game.Gui;
import javafx.application.Application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * (Todo) Prompts user for name
 * Establishes connection to server
 * Starts a continuous input thread from server
 * Starts a continuous output thread to server
 * Starts JavaFx Game Thread
 *
 * currently sends console userinput, not game input
 */
public class Client {
    private static String clientName = "TodoUser"; //TODO
    public static void main(String[] args) {
        try {

            //connection
            Socket socket = new Socket("localhost", 4969);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //From server
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true); //From console input to server
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in)); //From console input
            if(socket.isConnected())DebugLogger.log("Connection Established to host: " + socket.getInetAddress());

            //Continuous thread for incoming messages by Server
            Thread inputThread = new Thread(() -> {
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        System.out.println("[SERVER]: " + line);
                        DebugLogger.log(line);
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
                    while ((userInputLine = userInput.readLine()) != null) {
                        out.println(userInputLine);
                        DebugLogger.log("@[Server]: " + userInputLine);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            outputThread.start();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        boolean isServerInstance = false;
        boolean isDebugEnabled = true;

        DebugLogger.log("Starting Game Application...");
        if(isDebugEnabled)DebugLogger.log("Running with Debugging Enabled");
        if(isServerInstance)DebugLogger.logServer("Running Application as Server");

        String[] arguments = {String.valueOf(isServerInstance), String.valueOf(isDebugEnabled)};
        Application.launch(Gui.class, arguments);

    }
}

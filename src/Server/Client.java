package Server;

import Game.Gui;
import javafx.application.Application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Establishes connection to server
 * Starts a continuous input thread from server
 * Starts a continuous output thread to server
 * Starts JavaFx Game Thread
 */
public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 4969);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //From server
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true); //From console input to server
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in)); //From console input

            //Continuous thread for incoming messages by Server
            Thread inputThread = new Thread(() -> {
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        System.out.println("Server says: " + line);
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
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            outputThread.start();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Application.launch(Gui.class);

    }
}

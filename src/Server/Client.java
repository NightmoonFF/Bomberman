package Server;

import Game.Gui;
import javafx.application.Application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("10.10.134.13", 4969);
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Send data to the server

            // Name Prompt
/*            String nameIn;
            while ((nameIn = userInput.readLine()) != null) {
                out.println(nameIn);
                Application.launch(Gui.class);
            }*/

            Application.launch(Gui.class);

            // Game Input
            String line;
            while ((line = userInput.readLine()) != null) {
                out.println(line);
                // Receive response from server and display it in the GUI
                String response = in.readLine();
                System.out.println("Server response: " + response); // Update GUI with response


            }
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

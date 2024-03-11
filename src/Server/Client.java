package Server;

import Server.Game.GameLogic;
import Server.Game.Player;
import javafx.application.Platform;

import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 4969);
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String message = in.readLine();
            System.out.println(message);



            while (true) {

                // Receive response from server and display it in the GUI
                String response = in.readLine();
                System.out.println("Server response: " + response); // Update GUI with response

                new movement(socket).start();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    static class movement extends Thread {

        Socket socket;

        public movement(Socket socket) {
            this.socket = socket;


            try {
                BufferedReader inFromOther = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                while (true) {
                    System.out.println("waiting for input");
                    String input = inFromOther.readLine();
                    System.out.println("Input received: " + input); // MOVE x y direction player_index

                    String[] parts = input.split(" ");

                    if (parts.length >= 4 && parts[0].equalsIgnoreCase("MOVE")) {
                        int x = Integer.parseInt(parts[1]);
                        int y = Integer.parseInt(parts[2]);
                        String direction = parts[3];
                        int playerIndex = Integer.parseInt(parts[4]);

                        Player player = GameLogic.getPlayerAt(x, y);


                        Platform.runLater(() -> {
                            try {
                                GameLogic.updatePlayer(player, x, y, direction);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });
                    } else {
                        System.out.println("Invalid input format");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


// Send data to the server
//            String line;
//            String nameIn = in.readLine();
//            System.out.println(nameIn);
//
//            while ((line = userInput.readLine()) != null) {
//                out.println(line);
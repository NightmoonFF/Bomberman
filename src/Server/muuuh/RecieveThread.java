package Server.muuuh;

import Game.GameLogic;
import Game.Player;
import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class RecieveThread extends Thread {
    Socket connSocket;
    DataOutputStream outToClient;

    public RecieveThread(Socket connSocket) {
        this.connSocket = connSocket;
    }
    public void run() {
        try {
            BufferedReader inFromOther = new BufferedReader(new InputStreamReader(connSocket.getInputStream()));

            while (true) {
                System.out.println("ReceiveThread waiting for input");
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

    public void sendMessage(String message) throws IOException { // MOVE x y direction player_index
        outToClient.writeBytes(message + '\n');
    }

//    public synchronized void broadcastMessage(String message) {
//        for (Serverthread thread : Server.serverthreads) {
//            try {
//                thread.sendMessage(message); // MOVE x y direction player_index
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
      //  }
   // }
}
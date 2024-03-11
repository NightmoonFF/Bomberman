package Server;

import Game.App;
import Game.GameLogic;
import Game.Gui;
import Game.Player;
import javafx.application.Application;

import java.io.*;
import java.net.*;
import java.util.ArrayList;


public class Server {

    private static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(4969)) {
            System.out.println("Server running..");

            while (true) {
                Socket clientSocket = serverSocket.accept(); // clients connect
                System.out.println("Client connect: " + clientSocket);


                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandlers.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void broadcastMessage(String message) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                clientHandler.sendMessage(message); // MOVE x y direction player_index
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


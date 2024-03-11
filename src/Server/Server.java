package Server;
import Game.App;
import Game.DebugLogger;
import Game.GameLogic;
import Game.Gui;
import javafx.application.Application;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server {

	static ArrayList<ClientHandler> clientThreads = new ArrayList<>();
	static ServerSocket serverSocket;

    static {
        try {
            serverSocket = new ServerSocket(4969);
			DebugLogger.logServer("Server Socket Established");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
		DebugLogger.logServer("Starting...");

		//Continuous thread for incoming Connections
		Thread connectionThread = new Thread(() -> {
			try {
				while (true) {
					Socket clientSocket = serverSocket.accept(); // clients connect

					//Initialize Thread for the connecting client
					ClientHandler clientHandler;
					clientThreads.add(clientHandler = new ClientHandler(clientSocket));
					new Thread(clientHandler).start();

					broadcast(clientSocket.getInetAddress() + " Has Connected to the game");
					DebugLogger.logServer(clientSocket.getInetAddress() + " Has Connected to the game");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		connectionThread.start();

		Application.launch(Gui.class);
    }

	/**
	 * Sends a message to each client
	 * @param message
	 */
	public static void broadcast(String message) {

		for (ClientHandler clientThread : clientThreads) {
			try {
				PrintWriter out = new PrintWriter(clientThread.clientSocket.getOutputStream(), true);
				out.println(message);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}


}


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
	static Common common;

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
		common = Common.getInstance();

		//Continuous thread for incoming Connections
		Thread connectionThread = new Thread(() -> {
			try {
				while (true) {
					//todo: maybe a map to also have desired name added

					Socket clientSocket = serverSocket.accept(); // wait for a client to connect
					ClientHandler clientHandler; //Initialize Thread for the client
					clientThreads.add(clientHandler = new ClientHandler(clientSocket));
					new Thread(clientHandler).start();


					broadcast(clientSocket.getInetAddress() + " Has Connected to the Server");
					DebugLogger.logServer(clientSocket.getInetAddress() + " Has Connected to the Server");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		connectionThread.start();

		boolean isServerInstance = true;
		boolean isDebugEnabled = true;
		DebugLogger.logServer("Starting Game Application...");
		if(isDebugEnabled)DebugLogger.logServer("Running with Debugging Enabled");
		if(isServerInstance)DebugLogger.logServer("Running Application as Server");
		String[] arguments = {String.valueOf(isServerInstance), String.valueOf(isDebugEnabled)};
		Application.launch(Gui.class, arguments);
    }

	/**
	 * Takes a message to be processed in Common
	 * @param message
	 * @param clientHandler
	 */
	public static void receiveMessage(String message, ClientHandler clientHandler){
		Common.handleInputRequest(message, clientHandler);
	}

	/**
	 * Sends a message to each client
	 * @param message
	 */
	public static void broadcast(String message) {

		for (ClientHandler clientThread : clientThreads) {
			try {
				//TODO: maybe send all at once
				PrintWriter out = new PrintWriter(clientThread.clientSocket.getOutputStream(), true);
				out.println(message);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}




}


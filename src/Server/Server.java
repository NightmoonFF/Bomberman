package Server;
import Game.*;
import javafx.application.Application;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Server {

	static ArrayList<ClientHandler> clientThreads = new ArrayList<>();
	static Map<Socket, Player> clientMap = new HashMap<>();
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
					Socket clientSocket = serverSocket.accept(); // wait for a client to connect
					ClientHandler clientHandler; //Initialize Thread for the client
					clientThreads.add(clientHandler = new ClientHandler(clientSocket));
					new Thread(clientHandler).start();

					broadcast(clientSocket.getInetAddress() + " Has Connected to the Server");
					DebugLogger.logServer(clientSocket.getInetAddress() + " Has Connected to the Server");
				}
			} catch (IOException e) {
				DebugLogger.logServer(e.getMessage());
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

	private static void init(){

	}


	/**
	 * Takes a message to be processed in Common
	 * @param message the message from client
	 * @param clientSocket the origin client
	 */
	public static void receiveMessage(String message, Socket clientSocket){
		Common.handleInputRequest(message, clientSocket);
	}


	/**
	 * Sends a message to each client
	 * @param message
	 */
	public static void broadcast(String message) {
		//TODO: way to send to all clients at once, instead of iterating? (not important)
		for (Socket socket : clientMap.keySet()) {
			try {
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				out.println(message);
			} catch (IOException e) {
				DebugLogger.logServer(e.getMessage());
			}
		}
	}

	/**
	 * Adds a newly connected client to the clientMap
	 * @param clientSocket connecting IP Socket
	 * @param player the created player object
	 */
	public static void addClient(Socket clientSocket, Player player){
		clientMap.put(clientSocket, player);
	}


}


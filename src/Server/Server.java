package Server;
import Game.App;
import Game.GameLogic;
import Game.Gui;
import javafx.application.Application;

import java.io.*;
import java.net.*;
public class Server {
	public static void main(String[] args) {
		try (ServerSocket serverSocket = new ServerSocket(4969)) {
			System.out.println("Server running..");

			Application.launch(Gui.class);


			while (true) {
				Socket clientSocket = serverSocket.accept(); // clients connect
				System.out.println("Client connect: " + clientSocket);

				App.me= GameLogic.makePlayer(clientSocket.getInetAddress().toString());

				//Threads for clients
				ClientHandler clientHandler = new ClientHandler(clientSocket);
				new Thread(clientHandler).start();
			}
		} catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

class ClientHandler implements Runnable {
	private final Socket clientSocket;
	private final BufferedReader in;
	private final PrintWriter out;
	private GameLogic gameLogic;

	public String clientName;

	public ClientHandler(Socket socket) {
		this.clientSocket = socket;
		this.clientName = socket.getInetAddress().toString();

		try {
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			out = new PrintWriter(clientSocket.getOutputStream());
		} catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
	@Override
	public void run() {
		try {


			// Name Prompt
/*
			PrintWriter nameOut = new PrintWriter(clientSocket.getOutputStream(), true);
			nameOut.println("Indtast spillernavn");

			while ((clientName = in.readLine()) != null) {

				System.out.println("New Player: " + clientName);
				//App.me= GameLogic.makePlayer(navn);
				//GameLogic.makeVirtualPlayer;
			}*/



			// Game Input
			String inputLine;
			while ((inputLine = in.readLine()) != null) {

				System.out.println("Received from client: " + inputLine);


			}
		} catch (IOException e) {
            e.printStackTrace();
        } finally {
			try {
				in.close();
				out.close();
				clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
            }
        }
    }
}

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

			while (true) {
				Socket clientSocket = serverSocket.accept(); // clients connect
				System.out.println("Client connect: " + clientSocket);

				//Threds for clients
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

	private String clientName;

	public ClientHandler(Socket socket) {
		this.clientSocket = socket;
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
			PrintWriter nameOut = new PrintWriter(clientSocket.getOutputStream(), true);
			nameOut.println("Indtast spillernavn");

			String inputLine;


			while ((inputLine = in.readLine()) != null) {

				System.out.println("Received from client: " + inputLine);

				String navn = inputLine;
				App.me= GameLogic.makePlayer(navn);
				GameLogic.makeVirtualPlayer();


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

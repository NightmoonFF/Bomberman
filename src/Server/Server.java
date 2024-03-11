package Server;
import Game.App;
import Game.GameLogic;
import Game.Gui;
import javafx.application.Application;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server {

	static ArrayList<ClientHandler> clientThreads;
	static Common common = new Common();

	public static void main(String[] args) {
		try (ServerSocket serverSocket = new ServerSocket(4969)) {
			System.out.println("Server running..");

			Application.launch(Gui.class);


			while (true) {
				Socket clientSocket = serverSocket.accept(); // clients connect
				System.out.println("Client connect: " + clientSocket);

				//Threads for clients
				ClientHandler clientHandler;
				clientThreads.add(clientHandler = new ClientHandler(clientSocket, common));
				new Thread(clientHandler).start();

			}



		} catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}


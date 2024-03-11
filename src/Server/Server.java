package Server;

import Game.App;
import Game.GameLogic;
import Game.Gui;
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

class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final BufferedReader in;
    private final DataOutputStream out;
	Server server;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
				System.out.println("Waiting for input");
				String input = in.readLine();
				System.out.println("Input: " + input);
                server.broadcastMessage((input));
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

    public void sendMessage(String message) throws IOException { // MOVE x y direction player_index
        out.writeBytes(message + '\n');
    }
}


//			String inputLine;
//			while ((inputLine = in.readLine()) != null) {
//				System.out.println("Received from client: " + inputLine);
//				String navn = inputLine;
//				App.me= GameLogic.makePlayer(navn);
//				GameLogic.makeVirtualPlayer();
//				Application.launch(Gui.class);
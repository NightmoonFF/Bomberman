package Server;

import Game.GameLogic;
import Game.Gui;
import Game.Player;
import javafx.application.Application;

import java.io.*;
import java.net.Socket;

class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final BufferedReader in;
    private final DataOutputStream out;
    private Player me;
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

                PrintWriter nameOut = new PrintWriter(clientSocket.getOutputStream(), true);


                System.out.println("Test");
                /*String navnSpiller = in.readLine();*/

                System.out.println("Test");
                me= GameLogic.makePlayer("test");

                Application.launch(Gui.class);




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

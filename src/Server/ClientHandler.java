package Server;

import Server.Game.GameLogic;
import Server.Game.Gui;
import Server.Game.Player;
import javafx.application.Application;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final BufferedReader in;
    private final BufferedReader name;
    private final DataOutputStream out;
	Server server;
    public static Player me;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            name = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 4; i++) {

                while (true) {
                    //PrintWriter nameOut = new PrintWriter(clientSocket.getOutputStream(), true);
                    //nameOut.println("Indtast spillernavn");
                    //System.out.println("name: " + name);

                    me = GameLogic.makePlayer("navn" + i);
                    GameLogic.makeVirtualPlayer(); // to be removed
                    Application.launch(Gui.class);

                    System.out.println("Waiting for input");
                    String input = in.readLine();
                    System.out.println("Input: " + input);
                    server.broadcastMessage((input));

                }
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

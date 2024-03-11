package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Klientens tråd, som er hver spillers forbindelse til serveren. Det er denne som sender en klients
 * beskeder frem og tilbage til serveren.
 */
class ClientHandler implements Runnable {

    final Socket clientSocket;
    private final BufferedReader in;
    private final PrintWriter out;
    public String clientName;



    public ClientHandler(Socket socket) {

        this.clientSocket = socket;
        this.clientName = socket.getInetAddress().toString();

        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void run() {

        try {


            String inputLine;
            while ((inputLine = in.readLine()) != null) {

                System.out.println("Received from client: " + inputLine);

            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                in.close();
                out.close();
                clientSocket.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

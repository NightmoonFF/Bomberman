package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Thread for each client, in which the requested input is recieved by server
 * ClientHandler is also saved as an ArrayList to iterate through, for the
 * server to broadcast to each client
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


            while (true){
                String inputLine;
                while ((inputLine = in.readLine()) != null) {

                    System.out.println("Received from client: " + inputLine);
                }
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

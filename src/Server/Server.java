package Server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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

	public ClientHandler(Socket socket) {
		this.clientSocket = socket;
		try {
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			out = new PrintWriter(clientSocket.getOutputStream(), true);
		} catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
	@Override
	public void run() {
		try {
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				System.out.println("Received from client: " + inputLine);
				// data? (game logic)
			}
		} catch (IOException e) {
            throw new RuntimeException(e);
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

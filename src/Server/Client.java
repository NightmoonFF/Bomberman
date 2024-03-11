package Server;

import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 4969);
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String message = in.readLine();
            System.out.println(message);



            while (true ) {

                // Receive response from server and display it in the GUI
                String response = in.readLine();
                System.out.println("Server response: " + response); // Update GUI with response


            }
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}




// Send data to the server
//            String line;
//            String nameIn = in.readLine();
//            System.out.println(nameIn);
//
//            while ((line = userInput.readLine()) != null) {
//                out.println(line);
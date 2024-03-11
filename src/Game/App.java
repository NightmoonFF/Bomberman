package Game;

import java.net.*;
import java.io.*;
import javafx.application.Application;;

public class App {
	public static Player me;
	public static void main(String[] args) throws Exception{	
/*		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Indtast spillernavn");
		String navn = inFromUser.readLine();
		*/
		//me = GameLogic.makePlayer("testMe");



		//Added parameters to choose what mode we want the game to run as
		boolean isServerInstance = false;
		boolean isDebugEnabled = true;
		String[] arguments = {String.valueOf(isServerInstance), String.valueOf(isDebugEnabled)};
		Application.launch(Gui.class, arguments);

		//TODO: have 2 options here, startClient, startServer
		// move code from Client and Server here for neater appearance. Give params for launching above code
	}
}
;
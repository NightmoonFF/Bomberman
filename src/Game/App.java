package Game;

import java.net.*;
import java.io.*;
import javafx.application.Application;;

public class App {
	/*public static Player me;*/
	public static void main(String[] args) throws Exception{	
/*		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Indtast spillernavn");
		String navn = inFromUser.readLine();
		me = GameLogic.makePlayer("testMe");
		*/
		boolean isServerInstance = false; //visual distinction and no game controls
		boolean isDebugEnabled = true; //adds button to show/hide debug log
		String[] arguments = {String.valueOf(isServerInstance), String.valueOf(isDebugEnabled)};
		Application.launch(Gui.class, arguments);
	}
}
;
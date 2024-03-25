package Game;

import java.net.*;
import java.io.*;
import javafx.application.Application;;

public class App {
	/*public static Player me;*/

	static String username = "Elias"; //TODO: prompt in launcher window
	static String ServerIP = "localhost";

	public static void main(String[] args) {

		boolean isDebugEnabled = true; //adds button to show/hide debug log
		String[] arguments = {String.valueOf(false), String.valueOf(isDebugEnabled), username};
		Application.launch(Gui.class, arguments);
	}

}
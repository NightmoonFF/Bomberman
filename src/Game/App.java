package Game;

import javafx.application.Application;

public class App {

	static String username = "Elias"; //TODO: prompt in launcher window
	static String ServerIP = "";

	public static void main(String[] args) {
		boolean isDebugEnabled = true; //adds button to show/hide debug log´´
		String[] arguments = {String.valueOf(false), String.valueOf(isDebugEnabled)};
		Application.launch(Gui.class, arguments);
	}
}
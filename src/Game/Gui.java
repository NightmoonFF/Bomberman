package Game;

import Server.Client;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static Game.DebugLogger.LOG_FILE_PATH;

public class Gui extends Application {

	private Client client;
	private boolean isServerInstance;
	private String username;

	private Scene primaryScene;
	private Stage primaryStage;
	private GridPane primaryPane;

	private Text gameLabel;
	private TextArea scoreList;

	public static final int fieldImageSize = 35;
	public static final int scene_height = fieldImageSize * 20 + 75;
	public static final int scene_width = fieldImageSize * 20 + 200;


	private static final GridPane fieldGridBottom = new GridPane();
	private static final GridPane fieldGridMid= new GridPane();
	private static final GridPane fieldGridBomb = new GridPane();
	private static final GridPane fieldGridExplosion = new GridPane();
	private static Label[][] fieldsBottom;
	private static Label[][] fieldsMid;
	private static Label[][] fieldsBomb;
	private static Label[][] fieldsExplosion;

	public static Image image_floor;
	public static Image image_wall;
	public static Image hero_right_red, hero_left_red, hero_up_red, hero_down_red;
	public static Image hero_right_blue, hero_left_blue, hero_up_blue, hero_down_blue;
	public static Image hero_right_green, hero_left_green, hero_up_green, hero_down_green;
	public static Image hero_right_pink, hero_left_pink, hero_up_pink, hero_down_pink;


	@Override
	public void init() {
		Parameters parameters = getParameters();
		if (!parameters.getRaw().isEmpty()) {
			String[] args = parameters.getRaw().toArray(new String[0]);
			if (args.length >= 2) {
				isServerInstance = Boolean.parseBoolean(args[0]);
				isDebugEnabled = Boolean.parseBoolean(args[1]);
			}
		}
	}


	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		clearLogFile();
		DebugLogger.log("Starting Game Application...");

		UsernamePopup usernamePopup = new UsernamePopup(primaryStage);
		username = usernamePopup.getUsername();

		initResources();
		initGUI();
		initClient();

		scoreList.setText(getScoreList()); //TODO: move for new player instantiation system
	}




	private void initGUI(){
			primaryPane = new GridPane();
			primaryPane.setHgap(10);
			primaryPane.setVgap(10);
			primaryPane.setPadding(new Insets(0, 10, 0, 10));

			gameLabel = new Text("Bomberman:");
			gameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

			Text scoreLabel = new Text("Score:");
			scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

			scoreList = new TextArea();
			scoreList.setEditable(false);

			initFields();

			// Wrapping the layers in a stackPane
			StackPane stackPane = new StackPane();
			stackPane.getChildren().add(fieldGridBottom);
			stackPane.getChildren().add(fieldGridMid);
			stackPane.getChildren().add(fieldGridBomb);
			stackPane.getChildren().add(fieldGridExplosion);

			primaryPane.add(gameLabel,  0, 0);
			primaryPane.add(scoreLabel, 1, 0);
			primaryPane.add(stackPane, 0, 1);
			primaryPane.add(scoreList,  1, 1);

			primaryScene = new Scene(primaryPane, scene_width, scene_height);
			primaryStage.setScene(primaryScene);
			primaryStage.show();
	}


	/**
	 * Instantiates the Fields (tiles) that makes up the "board/map" of the game, in separate grids for each layer.
	 * Each Field or tile represents a valid position in the grid for players to be located at, as (X/Y) co-ordinates.
	 * The board design is created as specified by the String "board", in the class "Generel".
	 * <p>
	 * Bottom Layer    - Map Assets and Player Sprites  <br>
	 * Box Layer       - Destructible Tiles             <br>
	 * Bomb Layer      - Bomb Sprites                   <br>
	 * Explosion Layer - Explosion/Fire Sprites
	 * <p>
	 * Ideally, the player sprites would be on their own layer, but has not been changed from the
	 * provided codebase that we were tasked to implement, due to the scope of the project and its timeframe.
	 */
	private void initFields(){
		try {

			// Instantiate Bottom Layer
			fieldsBottom = new Label[20][20];
			for (int j=0; j<20; j++) {
				for (int i=0; i<20; i++) {
					switch (Generel.board[j].charAt(i)) {
						case 'w':
							fieldsBottom[i][j] = new Label("", new ImageView(image_wall));
							break;
						case ' ':
							fieldsBottom[i][j] = new Label("", new ImageView(image_floor));
							break;
						default: throw new Exception("Illegal field value: " + Generel.board[j].charAt(i) );
					}
					fieldGridBottom.add(fieldsBottom[i][j], i, j);
				}
			}

			//TODO: Instantiate Box Layer. Possibly replace W with B or something, and have that spawn destructibles
			/*fieldsMid = new Label[20][20];
			for (int j=0; j<20; j++) {
				for (int i=0; i<20; i++) {
					switch (Generel.board[j].charAt(i)) {
						case 'w':
							fieldsBot[i][j] = new Label("", new ImageView(image_wall));
							break;
						case ' ':
							fieldsBot[i][j] = new Label("", new ImageView(image_floor));
							break;
						default: throw new Exception("Illegal field value: " + Generel.board[j].charAt(i) );
					}
					fieldGridBot.add(fieldsBot[i][j], i, j);
				}
			}*/

			// Instantiate Bomb Layer
			fieldsBomb = new Label[20][20];
			for (int j = 0; j < 20; j++) {
				for (int i = 0; i < 20; i++) {
					ImageView imageView = new ImageView();
					imageView.setFitWidth(fieldImageSize);
					imageView.setFitHeight(fieldImageSize);
					fieldsBomb[i][j] = new Label("", imageView);
					fieldGridBomb.add(fieldsBomb[i][j], i, j);
				}
			}

			// Instantiate Explosion Layer
			fieldsExplosion = new Label[20][20];
			for (int j = 0; j < 20; j++) {
				for (int i = 0; i < 20; i++) {
					ImageView imageView = new ImageView();
					imageView.setFitWidth(fieldImageSize);
					imageView.setFitHeight(fieldImageSize);
					fieldsExplosion[i][j] = new Label("", imageView);
					fieldGridExplosion.add(fieldsExplosion[i][j], i, j);
				}
			}


		} catch(Exception e) {
			DebugLogger.log(e.getMessage());
		}
	}


	private void initResources(){
		image_wall  = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/wall4.png")), fieldImageSize, fieldImageSize, false, false);
		image_floor = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/floor1.png")), fieldImageSize, fieldImageSize, false, false);

		hero_right_red  = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/heroRightRed.png")), fieldImageSize, fieldImageSize, false, false);
		hero_left_red   = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/heroLeftRed.png")), fieldImageSize, fieldImageSize, false, false);
		hero_up_red     = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/heroUpRed.png")), fieldImageSize, fieldImageSize, false, false);
		hero_down_red   = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/heroDownRed.png")), fieldImageSize, fieldImageSize, false, false);

		hero_right_blue  = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/heroRightBlue.png")), fieldImageSize, fieldImageSize, false, false);
		hero_left_blue   = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/heroLeftBlue.png")), fieldImageSize, fieldImageSize, false, false);
		hero_up_blue     = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/heroUpBlue.png")), fieldImageSize, fieldImageSize, false, false);
		hero_down_blue   = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/heroDownBlue.png")), fieldImageSize, fieldImageSize, false, false);

		hero_right_green  = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/heroRightGreen.png")), fieldImageSize, fieldImageSize, false, false);
		hero_left_green   = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/heroLeftGreen.png")), fieldImageSize, fieldImageSize, false, false);
		hero_up_green     = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/heroUpGreen.png")), fieldImageSize, fieldImageSize, false, false);
		hero_down_green   = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/heroDownGreen.png")), fieldImageSize, fieldImageSize, false, false);

		hero_right_pink  = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/heroRightPink.png")), fieldImageSize, fieldImageSize, false, false);
		hero_left_pink   = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/heroLeftPink.png")), fieldImageSize, fieldImageSize, false, false);
		hero_up_pink     = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/heroUpPink.png")), fieldImageSize, fieldImageSize, false, false);
		hero_down_pink   = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/heroDownPink.png")), fieldImageSize, fieldImageSize, false, false);
	}


	private void initClient(){

		if(isDebugEnabled){

			if(!isServerInstance)DebugLogger.log("Running with Debugging Enabled");
			else DebugLogger.logServer("Running with Debugging Enabled");
			setupDebug(primaryStage, primaryPane);
		}
		if(isServerInstance){

			DebugLogger.logServer("Running Application as Server");
			gameLabel.setText("SERVER INSTANCE");
			primaryPane.setStyle("-fx-background-color: lightblue;");
		}
		else{

			client = new Client();
			primaryScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
				switch (event.getCode()) {
					case UP:
						client.sendMessage("MOVE" + " " + "up" + " " + username);
						DebugLogger.log("CLICKED UP");
						break;
					case DOWN:
						client.sendMessage("MOVE" + " " + "down" + " " + username);
						DebugLogger.log("CLICKED DOWN");
						break;
					case LEFT:
						client.sendMessage("MOVE" + " " + "left" + " " + username);
						DebugLogger.log("CLICKED LEFT");
						break;
					case RIGHT:
						client.sendMessage("MOVE" + " " + "right" + " " + username);
						DebugLogger.log("CLICKED RIGHT");
						break;
					case SPACE:
						client.sendMessage("BOMB" + " " + username);
						DebugLogger.log("BOMBED");
						break;
					case ESCAPE:
						System.exit(0);
						break;
					default:
						break;
				}
			});

			//TODO: Client make player request. To be changed for lobby system or countdown system?
			Position playerPosition = GameLogic.getRandomFreePosition();
			client.sendMessage("JOIN" + " " + username + " " + playerPosition.x + " " + playerPosition.y);
		}
	}


	public static void removePlayerOnScreen(Position oldPos) {
		Platform.runLater(() -> {
			fieldsBottom[oldPos.getX()][oldPos.getY()].setGraphic(new ImageView(image_floor));
			});
	}


	public static void placePlayerOnScreen(Position newPos, String direction, PlayerColor playerColor) {
		//WHY DID THE PERSON WHO MADE THIS DECIDE ON 4 SEPARATE IMAGES INSTEAD OF ROTATING THE SAME ONE
		Platform.runLater(() -> {
			int newx = newPos.getX();
			int newy = newPos.getY();
			if (direction.equals("right")) {
				switch (playerColor){
					case RED -> fieldsBottom[newx][newy].setGraphic(new ImageView(hero_right_red));
					case BLUE -> fieldsBottom[newx][newy].setGraphic(new ImageView(hero_right_blue));
					case GREEN -> fieldsBottom[newx][newy].setGraphic(new ImageView(hero_right_green));
					case PINK -> fieldsBottom[newx][newy].setGraphic(new ImageView(hero_right_pink));
				}
			}
			if (direction.equals("left")) {
				switch (playerColor){
					case RED -> fieldsBottom[newx][newy].setGraphic(new ImageView(hero_left_red));
					case BLUE -> fieldsBottom[newx][newy].setGraphic(new ImageView(hero_left_blue));
					case GREEN -> fieldsBottom[newx][newy].setGraphic(new ImageView(hero_left_green));
					case PINK -> fieldsBottom[newx][newy].setGraphic(new ImageView(hero_left_pink));
				}
			}
			if (direction.equals("up")) {
				switch (playerColor){
					case RED -> fieldsBottom[newx][newy].setGraphic(new ImageView(hero_up_red));
					case BLUE -> fieldsBottom[newx][newy].setGraphic(new ImageView(hero_up_blue));
					case GREEN -> fieldsBottom[newx][newy].setGraphic(new ImageView(hero_up_green));
					case PINK -> fieldsBottom[newx][newy].setGraphic(new ImageView(hero_up_pink));
				}
			}
			if (direction.equals("down")) {
				switch (playerColor){
					case RED -> fieldsBottom[newx][newy].setGraphic(new ImageView(hero_down_red));
					case BLUE -> fieldsBottom[newx][newy].setGraphic(new ImageView(hero_down_blue));
					case GREEN -> fieldsBottom[newx][newy].setGraphic(new ImageView(hero_down_green));
					case PINK -> fieldsBottom[newx][newy].setGraphic(new ImageView(hero_down_pink));
				}
			}
		});
	}


	public static void placeBombOnScreen(Bomb bomb){
		Platform.runLater(() -> {
			fieldsBomb[bomb.getPosition().getX()][bomb.getPosition().getY()].setGraphic(bomb.getBombImageView());
		});
	}


	public static void removeBombOnScreen(Position pos) {
		Platform.runLater(() -> {
			ImageView imageView = new ImageView();
			imageView.setFitWidth(fieldImageSize);
			imageView.setFitHeight(fieldImageSize);
			fieldsBomb[pos.getX()][pos.getY()].setGraphic(imageView);
		});
	}

	public static void placeExplosionOnScreen(Position pos, ImageView expView){
		Platform.runLater(() -> {
		fieldsExplosion[pos.getX()][pos.getY()].setGraphic(expView);
		});
	}


	public static void removeExplosionOnScreen(Position pos){
		Platform.runLater(() -> {
			ImageView imageView = new ImageView();
			imageView.setFitWidth(fieldImageSize);
			imageView.setFitHeight(fieldImageSize);
			fieldsExplosion[pos.getX()][pos.getY()].setGraphic(imageView);
		});
	}

	public static void movePlayerOnScreen(Position oldPos, Position newPos, String direction, PlayerColor playerColor) {
		removePlayerOnScreen(oldPos);
		placePlayerOnScreen(newPos,direction, playerColor);
	}

	public void updateScoreTable() {
		//TODO: use this instead of getScoreList?
		Platform.runLater(() -> {
			scoreList.setText(getScoreList());
			});
	}


	public String getScoreList() {
		StringBuffer b = new StringBuffer(100);
		for (Player p : GameLogic.players) {
			b.append(p+"\r\n");
		}
		return b.toString();
	}

	public String getUsername() {
		return username;
	}

	public static GridPane getFieldGridBottom(){ return fieldGridBottom; }


	//region Debugging
	static TextArea debugTA = new TextArea();
	private boolean isDebugEnabled;
	static boolean isShowingDebugLog = false;
	public void setupDebug(Stage primaryStage, GridPane grid){
		Button button = new Button("Debug");
		grid.add(button, 1, 2);

		debugTA.setEditable(false);
		debugTA.clear();
		debugTA.setPrefHeight(primaryStage.getHeight());
		debugTA.setPrefWidth(300);

		button.setOnAction(e -> toggleDebugGUI(primaryStage, grid, debugTA));

		Thread logReaderThread = new Thread(this::readLogFile);
		logReaderThread.setDaemon(true); // So that the thread stops when the application is closed
		logReaderThread.start();
	}
	private void readLogFile() {
		try {
			File logFile = new File(LOG_FILE_PATH);
			long lastFileSize = 0;

			while (true) {
				long fileSize = logFile.length();

				if (fileSize > lastFileSize) {
					try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
						// Skip to the end of the file
						reader.skip(lastFileSize);
						String line;
						while ((line = reader.readLine()) != null) {
							// Append new log lines to the TextArea
							String finalLine = line;
							Platform.runLater(() -> debugTA.appendText(finalLine + "\n"));
						}
						lastFileSize = fileSize;
					} catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

				// Wait for 1 second before checking the file again
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
			DebugLogger.log(e.getMessage());
		}
	}
	public static void clearLogFile(){
		try {
			Files.write(Paths.get(LOG_FILE_PATH), new byte[0]);
		} catch (IOException e) {
			DebugLogger.log(e.getMessage());
		}
	}
	public static void toggleDebugGUI(Stage primaryStage, GridPane grid, TextArea debugTA) {
		if(isShowingDebugLog){
			grid.getChildren().remove(debugTA);
			primaryStage.setWidth(primaryStage.getWidth() - 300);
			isShowingDebugLog = false;
		}
		else{
			grid.add(debugTA, 1, 1, 2, 1);
			primaryStage.setWidth(primaryStage.getWidth() + 300);
			isShowingDebugLog = true;
		}
	}
	//endregion
}


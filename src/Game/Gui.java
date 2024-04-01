package Game;

import Server.Client;

import Server.Server;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.*;
import javafx.util.Duration;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import javafx.scene.image.ImageView;

import static Game.DebugLogger.LOG_FILE_PATH;

public class Gui extends Application {

	private Client client;
	private boolean isServerInstance;

	private Scene primaryScene;
	private Stage primaryStage;
	private GridPane primaryPane;
	private static VBox playerList;

	public static final int fieldImageSize = 35;
	public static final int scene_height = fieldImageSize * 20 + 95;
	public static final int scene_width = fieldImageSize * 20 + 200;


	private static final GridPane fieldGridBottom = new GridPane();
	private static final GridPane fieldGridMid= new GridPane();
	private static final GridPane fieldGridBomb = new GridPane();
	private static final GridPane fieldGridExplosion = new GridPane();
	private static Label[][] fieldsBottom;
	private static Label[][] fieldsBomb;
	private static Label[][] fieldsExplosion;

	private static ImageView gameLabelView;

	public static Image gameLabel;
	public static Image skull;
	public static Image heart;
	public static Image image_floor, image_wall;

	public static Image hero_right_red, hero_left_red, hero_up_red, hero_down_red;
	public static Image hero_right_blue, hero_left_blue, hero_up_blue, hero_down_blue;
	public static Image hero_right_green, hero_left_green, hero_up_green, hero_down_green;
	public static Image hero_right_pink, hero_left_pink, hero_up_pink, hero_down_pink;

	private static Label infoLabel = new Label();
	private static boolean canMove = false;
	private static Timeline timeline = new Timeline();


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

		initResources();
		initGUI();
		initClient();
	}


	//region [ Initialization ]

	/**
	 * Main GUI Initialization
	 */
	private void initGUI(){

		primaryPane = new GridPane();
		primaryPane.setHgap(10);
		primaryPane.setVgap(10);
		primaryPane.setPadding(new Insets(0, 10, 0, 10));
		primaryPane.setStyle("-fx-background-color: #9b9b9b");

		gameLabelView = new ImageView(gameLabel);

		Button btnStart = new Button("Start");
		btnStart.setOnAction(e -> startGame());
		btnStart.setVisible(isServerInstance);


		initFields();


		// Player List
		playerList = new VBox();
		playerList.setVisible(false);
		VBox.setVgrow(playerList, Priority.NEVER);
		StackPane playerListContainer = new StackPane();
		playerListContainer.setMaxHeight(Region.USE_PREF_SIZE);
		StackPane.setAlignment(playerListContainer, Pos.BASELINE_CENTER);

		BorderStroke borderStroke= new BorderStroke(Color.valueOf("#7c7d7c"), BorderStrokeStyle.SOLID, null, new BorderWidths(6));
		Border border = new Border(borderStroke);

		playerListContainer.setMaxWidth(200);
		playerListContainer.setMinHeight(primaryPane.getHeight());
		playerListContainer.setBorder(border);
		playerList.setMaxWidth(200);
		playerList.setFillWidth(true);
		playerListContainer.getChildren().add(playerList);

		infoLabel.setFont(Font.font(18));
		infoLabel.setText("Waiting for players");

		// Wrapping the game-board layers in a stackPane
		StackPane stackPane = new StackPane();
		stackPane.getChildren().add(fieldGridBottom);
		stackPane.getChildren().add(fieldGridMid);
		stackPane.getChildren().add(fieldGridBomb);
		stackPane.getChildren().add(fieldGridExplosion);


		primaryPane.add(gameLabelView,  0, 0, 1, 1);
		primaryPane.add(infoLabel, 1, 0);
		primaryPane.add(stackPane, 0, 1);
		primaryPane.add(playerListContainer,  1, 1, 1, 2);
		primaryPane.add(btnStart, 0, 2);

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


	/**
	 * Loads the Game Assets into fields from a resources path
	 */
	private void initResources(){
		gameLabel        = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/UI/cooltext.png")),419, 78, true, true );
		skull            = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/UI/deadNotBigSurprise.png")), fieldImageSize, fieldImageSize, false, false);
		heart            = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/UI/heart.png")), fieldImageSize, fieldImageSize, false, false);

		image_wall       = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/board/crates_study_x2_0.png")), fieldImageSize, fieldImageSize, false, false);
		image_floor      = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/board/floor1.png")), fieldImageSize, fieldImageSize, false, false);

		hero_right_red   = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/hero/heroRightRed.png")), fieldImageSize, fieldImageSize, false, false);
		hero_left_red    = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/hero/heroLeftRed.png")), fieldImageSize, fieldImageSize, false, false);
		hero_up_red      = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/hero/heroUpRed.png")), fieldImageSize, fieldImageSize, false, false);
		hero_down_red    = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/hero/heroDownRed.png")), fieldImageSize, fieldImageSize, false, false);

		hero_right_blue  = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/hero/heroRightBlue.png")), fieldImageSize, fieldImageSize, false, false);
		hero_left_blue   = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/hero/heroLeftBlue.png")), fieldImageSize, fieldImageSize, false, false);
		hero_up_blue     = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/hero/heroUpBlue.png")), fieldImageSize, fieldImageSize, false, false);
		hero_down_blue   = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/hero/heroDownBlue.png")), fieldImageSize, fieldImageSize, false, false);

		hero_right_green = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/hero/heroRightGreen.png")), fieldImageSize, fieldImageSize, false, false);
		hero_left_green  = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/hero/heroLeftGreen.png")), fieldImageSize, fieldImageSize, false, false);
		hero_up_green    = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/hero/heroUpGreen.png")), fieldImageSize, fieldImageSize, false, false);
		hero_down_green  = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/hero/heroDownGreen.png")), fieldImageSize, fieldImageSize, false, false);

		hero_right_pink  = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/hero/heroRightPink.png")), fieldImageSize, fieldImageSize, false, false);
		hero_left_pink   = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/hero/heroLeftPink.png")), fieldImageSize, fieldImageSize, false, false);
		hero_up_pink     = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/hero/heroUpPink.png")), fieldImageSize, fieldImageSize, false, false);
		hero_down_pink   = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/hero/heroDownPink.png")), fieldImageSize, fieldImageSize, false, false);
	}

	public static void setLabelText(String s) {
		Platform.runLater(() -> {
			infoLabel.setText(s);
		});
	}

	public static void setCanMove(boolean canMove) {
		Gui.canMove = canMove;
	}


	public void startGame() {
	//TODO: move this away from GUI Class
		final int countdownTime = 5;

		for (int i = countdownTime; i >= 0; i--) {
			final int remainingSeconds = i;

			KeyFrame keyFrame = new KeyFrame(
					Duration.seconds(countdownTime - i),
					event -> {

						infoLabel.setText("Starting game in " + remainingSeconds + " seconds"); // Only on server

						Server.broadcast("COUNTER" + " " + "Starting:" + remainingSeconds);

						if (remainingSeconds == 0) {
							Server.broadcast("START");
							Server.broadcast("COUNTER" + " " + "Game-Started!");
						}
					}
			);
			timeline.getKeyFrames().add(keyFrame);
		}
		timeline.play();
	}



	/**
	 * Instantiates a new Client Object for the connection started by the App class,
	 * and attempts to establish a connection to the requested IP-Address of the server
	 */
	private void initClient(){

		if(isDebugEnabled){
			if(!isServerInstance)DebugLogger.log("Running with Debugging Enabled");
			else DebugLogger.logServer("Running with Debugging Enabled");
			setupDebug();

			// Enables Debug GUI Change Stuff with F1 key (
			primaryScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
				if (event.getCode() == KeyCode.F1) {
					toggleDebugGUI(primaryStage, primaryPane, debugTA);
				}
			});
		}

		if(isServerInstance){

			DebugLogger.logServer("Running Application as Server");
			primaryPane.getChildren().remove(gameLabelView);
			Label srvLabel = new Label("SERVER");
			srvLabel.setFont(Font.font(35));
			primaryPane.add(srvLabel, 0, 0);
		}
		else{

			client = new Client();
			primaryScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
				if(canMove){
					switch (event.getCode()) {
						case UP:
							client.sendMessage("MOVE" + " " + "up" + " " + App.username);
							DebugLogger.log("CLICKED UP");
							break;
						case DOWN:
							client.sendMessage("MOVE" + " " + "down" + " " + App.username);
							DebugLogger.log("CLICKED DOWN");
							break;
						case LEFT:
							client.sendMessage("MOVE" + " " + "left" + " " + App.username);
							DebugLogger.log("CLICKED LEFT");
							break;
						case RIGHT:
							client.sendMessage("MOVE" + " " + "right" + " " + App.username);
							DebugLogger.log("CLICKED RIGHT");
							break;
						case SPACE:
							client.sendMessage("BOMB" + " " + App.username);
							DebugLogger.log("BOMBED");
							break;
						case ESCAPE:
							System.exit(0);
							break;
						default:
							break;
					}
				}

			});

			client.sendMessage("JOIN" + " " + App.username);
		}
	}
	//endregion


	//region [ Player ]

	/**
	 * Removes a player visually from a given positional co-ordinate
	 * @param oldPos the position in which to remove the player sprite
	 */
	public static void removePlayerOnScreen(Position oldPos) {
		Platform.runLater(() -> fieldsBottom[oldPos.getX()][oldPos.getY()].setGraphic(new ImageView(image_floor)));
	}


	/**
	 * Places a Player object visually on the GUI at a given position, in a direction, using the player's assigned color
	 * @param newPos position being moved to
	 * @param direction the facing direction
	 * @param playerColor the player's color
	 */
	public static void placePlayerOnScreen(Position newPos, String direction, PlayerColor playerColor) {
		//WHY DID THE PERSON WHO MADE THIS DECIDE ON 4 SEPARATE IMAGES INSTEAD OF ROTATING THE SAME ONE
		Platform.runLater(() -> {
			int newX = newPos.getX();
			int newY = newPos.getY();
			if (direction.equals("right")) {
				switch (playerColor){
					case RED -> fieldsBottom[newX][newY].setGraphic(new ImageView(hero_right_red));
					case BLUE -> fieldsBottom[newX][newY].setGraphic(new ImageView(hero_right_blue));
					case GREEN -> fieldsBottom[newX][newY].setGraphic(new ImageView(hero_right_green));
					case PINK -> fieldsBottom[newX][newY].setGraphic(new ImageView(hero_right_pink));
				}
			}
			if (direction.equals("left")) {
				switch (playerColor){
					case RED -> fieldsBottom[newX][newY].setGraphic(new ImageView(hero_left_red));
					case BLUE -> fieldsBottom[newX][newY].setGraphic(new ImageView(hero_left_blue));
					case GREEN -> fieldsBottom[newX][newY].setGraphic(new ImageView(hero_left_green));
					case PINK -> fieldsBottom[newX][newY].setGraphic(new ImageView(hero_left_pink));
				}
			}
			if (direction.equals("up")) {
				switch (playerColor){
					case RED -> fieldsBottom[newX][newY].setGraphic(new ImageView(hero_up_red));
					case BLUE -> fieldsBottom[newX][newY].setGraphic(new ImageView(hero_up_blue));
					case GREEN -> fieldsBottom[newX][newY].setGraphic(new ImageView(hero_up_green));
					case PINK -> fieldsBottom[newX][newY].setGraphic(new ImageView(hero_up_pink));
				}
			}
			if (direction.equals("down")) {
				switch (playerColor){
					case RED -> fieldsBottom[newX][newY].setGraphic(new ImageView(hero_down_red));
					case BLUE -> fieldsBottom[newX][newY].setGraphic(new ImageView(hero_down_blue));
					case GREEN -> fieldsBottom[newX][newY].setGraphic(new ImageView(hero_down_green));
					case PINK -> fieldsBottom[newX][newY].setGraphic(new ImageView(hero_down_pink));
				}
			}
		});
	}


	/**
	 * Removes, then places a player from one position to another facing the appropriate direction, using the right color
	 * @param oldPos position being moved from
	 * @param newPos position being moved to
	 * @param direction the facing direction
	 * @param playerColor the player's color
	 */
	public static void movePlayerOnScreen(Position oldPos, Position newPos, String direction, PlayerColor playerColor) {
		removePlayerOnScreen(oldPos);
		placePlayerOnScreen(newPos,direction, playerColor);
	}
	//endregion


	//region [ Bomb & Explosion ]
	public static void placeBombOnScreen(Bomb bomb){
		Platform.runLater(() -> fieldsBomb[bomb.getPosition().getX()][bomb.getPosition().getY()].setGraphic(bomb.getBombImageView()));
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
		Platform.runLater(() -> fieldsExplosion[pos.getX()][pos.getY()].setGraphic(expView));
	}
	public static void removeExplosionOnScreen(Position pos){
		Platform.runLater(() -> {
			ImageView imageView = new ImageView();
			imageView.setFitWidth(fieldImageSize);
			imageView.setFitHeight(fieldImageSize);
			fieldsExplosion[pos.getX()][pos.getY()].setGraphic(imageView);
		});
	}
	//endregion


	//region [ PlayerList ]
	public static void updatePlayerList(){

		Platform.runLater(() -> {

			playerList.setVisible(true);

			for(Player p : GameLogic.players){

				VBox vbx = new VBox();
				playerList.getChildren().add(vbx);

				// Name Display
				Text playerText = new Text(p.getName());

				// Player Name Scaling
				vbx.setMaxWidth(150);
				vbx.setPrefWidth(150);

				// Set initial font size
				double fontSize = 20; // Initial font size
				playerText.setFont(Font.font("Arial", FontWeight.BOLD, fontSize));

				// Calculate the width of the text
				double textWidth = playerText.getLayoutBounds().getWidth();

				// Adjust font size to fit within the available width
				double maxWidth = 150;
				if (textWidth > maxWidth) {
					fontSize *= maxWidth / textWidth; // Scale down font size
					playerText.setFont(Font.font("Arial", FontWeight.BOLD, fontSize));
				}


				vbx.getChildren().add(playerText);

				// Health Bar
				vbx.getChildren().add(p.getHealthBar());

				// Main Styling
				vbx.setAlignment(Pos.BASELINE_CENTER);
				vbx.setStyle("-fx-background-color: #8e8c8c");
				vbx.setPadding(new Insets(5, 10, 5, 10));


				playerText.setStyle(
						"-fx-fill: black;" +
						"-fx-font-weight: bold;" +
						"-fx-effect: dropshadow(gaussian, derive(" + p.getPlayerColor() + ", 60%), 10, 0.0, 2, 2);" +
						"-fx-stroke: derive(" + p.getPlayerColor() + ", -20%);" +
						"-fx-rotate: 355;");
				playerText.setTextAlignment(TextAlignment.CENTER);

			}
		});
	}

	/**
	 * updates the GUI when a player takes damage
	 * @param p player
	 */
	public static void updatePlayerDamage(Player p){
		Platform.runLater(() -> {

			if (p.getHealthBar().getChildren().size() > p.getCurrentHealth()) {
				Node node = p.getHealthBar().getChildren().get(p.getCurrentHealth());
				if (node instanceof ImageView imageView) {

					// has to be initialized here for the gif to start from beginning
					Image gifImage = new Image("/UI/heartbreak.gif", fieldImageSize, fieldImageSize, false, false);

					// the heartbreak .Gif is 2.9 seconds long
					double duration = 2900;

					imageView.setImage(gifImage);

					Timeline stopTimeline = new Timeline(new KeyFrame(Duration.millis(duration), e -> {

						if(p.getCurrentHealth() == 0){
							imageView.setImage(skull);
						}
						else{
							imageView.setImage(null);
						}

					}));
					stopTimeline.play();

				}
				else {
					DebugLogger.log("Node at current health index is not an ImageView");
					throw new RuntimeException("Node at current health index is not an ImageView");
				}
			}
			else {
				throw new IndexOutOfBoundsException();
			}
		});
	}
	//endregion


	//region [ Debugging ]
	static TextArea debugTA = new TextArea();
	private boolean isDebugEnabled;
	static boolean isShowingDebugLog = false;
	public void setupDebug(){

		debugTA.setEditable(false);
		debugTA.clear();
		debugTA.setPrefHeight(primaryStage.getHeight());
		debugTA.setPrefWidth(330);

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
			primaryStage.setWidth(primaryStage.getWidth() - 100);
			isShowingDebugLog = false;
		}
		else{
			grid.add(debugTA, 1, 1, 2, 1);
			primaryStage.setWidth(primaryStage.getWidth() + 100);
			isShowingDebugLog = true;
		}
	}
	//endregion
}


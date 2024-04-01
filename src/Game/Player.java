package Game;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.Timer;
import java.util.TimerTask;

public class Player {

	private final String name;
	private Position position;
	private String direction;

	private boolean isDead;
	private final int startHealth;
	private int currentHealth;
	private final HBox healthBar = new HBox();

	private static PlayerColor lastAssignedColor = null;
	private final PlayerColor playerColor;

	private double bombCooldown = 3;
	private boolean isBombActivated;
	private final Timer bombTimer = new Timer();


	/**
	 * Constructor
	 * @param name name
	 * @param direction direction
	 */
	public Player(String name, String direction) {
		this.name = name;
		this.direction = direction;
		this.startHealth = 3;
		this.currentHealth = startHealth;
		this.playerColor = getNextAvailableColor();
		this.isDead = false;
	}


	//region Health System
	/**
	 * Creates the HBox that contains the heart Images, for this specific player
	 */
	private void initHealthBar(){
		healthBar.getChildren().clear();
		for (int i = 0; i < getStartHealth(); i++) {
			healthBar.getChildren().add(new ImageView(Gui.heart));
		}
		healthBar.setPadding(new Insets(17, 7, 7, 7));

		healthBar.setAlignment(Pos.BASELINE_CENTER);
	}

	/**
	 * Resets the player to be able to play again.
	 * Gives full health and displays player sprite on GUI
	 */
	public void reset() {
		isDead = false;
		currentHealth = 3;
		initHealthBar();
	}

	/**
	 * lowers currentHealth, and determines if player has died
	 */
	public void takeDamage() {
		currentHealth--;
		if(currentHealth == 0){
			isDead = true;
		}
	}
	//endregion


	//region Bomb System
	/**
	 * Starts a cooldown for the player when placing a bomb,
	 * preventing additional being spawned for the specified duration.
	 */
	public void startBombCooldownTimer() { //TODO: Not working
		bombTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (bombCooldown > 0) {
					bombCooldown -= 1.0;
				} else {
					// Cooldown finished
					isBombActivated = false;
					bombTimer.cancel();
				}
			}
		}, 1000, 1000); // Run task once per second
	}
	public boolean isBombActivated() { return isBombActivated; }
	//endregion


	//region Getters & Setters
	public String getName() { return name; }
	public int getCurrentHealth() { return currentHealth; }
	public int getStartHealth() { return startHealth; }
	public HBox getHealthBar() { return healthBar; }
	public PlayerColor getPlayerColor() { return playerColor; }
	public Position getPosition() { return this.position; }
	public String getDirection() { return direction; }
	public void setPosition(Position p) { this.position = p; }
	public void setDirection(String direction) { this.direction = direction; }
	public int getX() { return position.x; }
	public int getY() { return position.y; }
	public boolean isDead() { return isDead; }
	//endregion


	/**
	 * Used by this constructor.
	 * Fetches the next valid color for the created player.
	 * Could possibly be Assigned inside/with the enum, and not here, but this currently works,
	 * and is de-prioritized due to project time restraints.
	 * @return the next available color in the sequence of Red, Blue, Green, Pink
	 */
	private PlayerColor getNextAvailableColor() {
		if (lastAssignedColor == null) {
			lastAssignedColor = PlayerColor.RED;
		} else {
			switch (lastAssignedColor) {
				case RED:
					lastAssignedColor = PlayerColor.BLUE;
					break;
				case BLUE:
					lastAssignedColor = PlayerColor.GREEN;
					break;
				case GREEN:
					lastAssignedColor = PlayerColor.PINK;
					break;
				case PINK:
					throw new RuntimeException("Only 4 players can be made");
			}
		}
		return lastAssignedColor;
	}


	public String toString() { return name; }

}

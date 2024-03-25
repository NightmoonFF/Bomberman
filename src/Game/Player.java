package Game;

import java.util.Timer;
import java.util.TimerTask;

public class Player {

	private final String name;
	private Position position;
	private int point;
	String direction;
	int health;

	private static PlayerColor lastAssignedColor = null;
	private final PlayerColor playerColor;

	private double bombCooldown = 3;
	private boolean isBombActivated;
	private Timer bombTimer = new Timer();



	public Player(String name, Position pos, String direction) {
		this.name = name;
		this.position = pos;
		this.direction = direction;
		this.point = 0;
		this.health = 3;
		this.playerColor = getNextAvailableColor();
	};

	//-----------------------------------------------------------------------------------------------------------------

	/**
	 * Starts a cooldown for the player when placing a bomb,
	 * preventing additional being spawned for the specified duration.
	 */
	public void startBombCooldownTimer() {
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

	//-----------------------------------------------------------------------------------------------------------------

	/**
	 * To be used in constructor.
	 * Fetches the color for the created player.
	 * @return the next color in the sequence of Red, Blue, Green, Pink
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

	//-----------------------------------------------------------------------------------------------------------------

	public String getName() { return name; }
	public int getHealth() { return health; }
	public PlayerColor getPlayerColor() { return playerColor; }
	public Position getPosition() { return this.position; }
	public String getDirection() { return direction; }
	public int getX() { return position.x; }
	public int getY() { return position.y; }

	//-----------------------------------------------------------------------------------------------------------------

	public boolean isBombActivated() { return isBombActivated; }
	public int takeDamage() { return health--; }
	public void resetHeath() { health = 3; }
	public void addPoints(int p) { point += p; }

	//-----------------------------------------------------------------------------------------------------------------

	public void setPosition(Position p) { this.position = p; }
	public void setX(int x) { this.position.x = x; }
	public void setY(int y) { this.position.y = y; }
	public void setDirection(String direction) { this.direction = direction; }

	//-----------------------------------------------------------------------------------------------------------------

	public String toString() { return name+": "+point; }

}

package Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static Game.Generel.board;

/**
 * Class that handles Game Flow and Rules.
 * Mostly contains methods that perform in-game instructions.
 */
public class GameLogic {
	public static List<Player> players = new ArrayList<>();
	public static List<Bomb> bombs = new ArrayList<>();


	/**
	 * Instantiates a player character in the game,
	 * but does not spawn them(show them on GUI).
	 * @param name desired name of the new player
	 * @return the created player
	 */
	public static Player makePlayer(String name) {

		Player player;
		player = new Player(name,"up");
		players.add(player);
		Gui.updatePlayerList();

		System.out.println("Created Player: " + name);

		return player;
	}

	/**
	 * Spawns this player in the GUI
	 * @param player player to spawn
	 * @param pos position to spawn
	 */
	public static void spawnPlayer(Player player, Position pos){
		player.reset();
		player.setPosition(pos);
		Gui.placePlayerOnScreen(pos, "up", player.getPlayerColor());
	}


	/**
	 * Used to handle player movement
	 * @param player the player being moved
	 * @param delta_x difference on x axis
	 * @param delta_y difference on y axis
	 * @param direction new facing direction of the player
	 */
	public static void updatePlayer(Player player, int delta_x, int delta_y, String direction) {

		DebugLogger.log(player.getName() + ": " + player.getDirection() + " to " + direction);
		player.setDirection(direction);

		int x = player.getX(),y = player.getY();

		if (board[y + delta_y].charAt(x + delta_x) != 'w') {

			// collision detection
			Player p = getPlayerAt(x + delta_x,y + delta_y);
			if (p!=null) {

              //update the other player
              Position pos = getRandomFreePosition();
              p.setPosition(pos);
              Position oldPos = new Position(x + delta_x, y + delta_y);
              Gui.movePlayerOnScreen(oldPos, pos, p.getDirection(), player.getPlayerColor());
			  System.out.println("PLAYER COLLISION");

			}
			else {
				Position oldPos = player.getPosition();
				Position newPos = new Position(x + delta_x, y + delta_y);
				Gui.movePlayerOnScreen(oldPos,newPos,direction, player.getPlayerColor());
				player.setPosition(newPos);

				DebugLogger.log(player.getName() + ": " + "(" + oldPos.getX() + "/" + oldPos.getY() + ")" + " to (" + newPos.getX() + "/" + newPos.getY() + ")");
			}

		}
		
		
	}


	/**
	 * Places a bomb on the players current tile.  <br>
	 * TODO: When moving out of the tile, the tile becomes occupied as if a player was there. Possibly handled in updatePlayer?
	 * @param player the player to place a bomb
	 */
	public static void placeBomb(Player player){

		/* TODO: cooldown not working:
		if(player.isBombActivated()) return;
		player.startBombCooldownTimer(); */

		Bomb bomb = new Bomb(player);
		bombs.add(bomb);
		Gui.placeBombOnScreen(bomb);

		DebugLogger.log("Bomb Placed by " + player.getName() + " (" + player.getX() + "/" + player.getY() + ")");

	}


	/**
	 * Called by explosion() in the Bomb class, to handle what happens when <br>
	 * a player is caught inside a bomb's range when it explodes
	 * @param p the explosions currently processed tile
	 */
	public static void damagePlayer(Player p){
		if(p.getCurrentHealth() == 1){
			Gui.removePlayerOnScreen(p.getPosition());
		}
		Gui.updatePlayerDamage(p);
		p.takeDamage();
	}


	/**
	 * Verifies if a given position is within bounds of the Game Board
	 * @param x PosX
	 * @param y PosY
	 * @return true if within bounds
	 */
	public static boolean isValidPosition(int x, int y) { return x >= 0 && x < board[0].length() && y >= 0 && y < board.length; }


	/**
	 * Provides a Position Object (int x, int y) that is random, and not inside wall or other player
	 * @return the position
	 */
	public static Position getRandomFreePosition() {
		int x = 1;
		int y = 1;
		boolean foundFreePos = false;
		while  (!foundFreePos) {
			Random r = new Random();
			x = Math.abs(r.nextInt() % 18) + 1;
			y = Math.abs(r.nextInt() % 18) + 1;
			if (board[y].charAt(x) == ' ') // er det gulv ?
			{
				foundFreePos = true;
				for (Player p: players) {

					if (p.getPosition() != null){ // spilleren ER spawnet i GUI
						if (p.getX() == x && p.getY() == y){ // pladsen optaget af en anden
							foundFreePos = false;
						}
					}

				}

			}
		}

		return new Position(x, y);
	}


	/**
	 * Provides the Player Object that is found at a given position
	 * @param x PosX
	 * @param y PosY
	 * @return the Player Object if one was found, otherwise returns null
	 */
	public static Player getPlayerAt(int x, int y) {
		for (Player p : players) {
			if (p.getX()==x && p.getY()==y) {
				return p;
			}
		}
		return null;
	}


	/**
	 * Provides a player Object, identified by its playername/username
	 * @param name the name to find the player object for
	 * @return the player object if found, otherwise null
	 */
	public static Player getPlayerByName(String name) {
		for (Player player : players) {
			if (player.getName().equalsIgnoreCase(name)) {
				return player;
			}
		}
		DebugLogger.log("Error! Player not found: " + name);
		return null;
	}
	

}

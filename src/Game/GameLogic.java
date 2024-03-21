package Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static Game.Generel.board;

/**
 * Mostly contains methods that perform in-game instructions, usually by the Client/Server
 */
public class GameLogic {
	public static List<Player> players = new ArrayList<>();
	public static List<Bomb> bombs = new ArrayList<>();


	/**
	 * Instantiates a player character in the game
	 * @param name desired name of the new player
	 * @return the created player
	 */
	public static Player makePlayer(String name, int x, int y) {

		Position playerPosition = new Position(x, y);
		Player player;
		player = new Player(name, playerPosition,"up");
		players.add(player);
		Gui.placePlayerOnScreen(new Position(player.getX(), player.getY()), "up", player.getPlayerColor());

		System.out.println("Created Player: " + name + " x" + player.getX() + "/y" + player.getY());
		//TODO: player is not added to scoreboard at the moment
		//TODO: convert scoreboard into life(health)
		return player;
	};


	/**
	 * @return a Position Object (int x, int y) that is random, and not inside wall or other player
	 */
	public static Position getRandomFreePosition() {
		int x = 1;
		int y = 1;
		boolean foundfreepos = false;
		while  (!foundfreepos) {
			Random r = new Random();
			x = Math.abs(r.nextInt() % 18) + 1;
			y = Math.abs(r.nextInt() % 18) + 1;
			if (board[y].charAt(x) == ' ') // er det gulv ?
			{
				foundfreepos = true;
				for (Player p: players) {
					if (p.getX() == x && p.getY() == y) //pladsen optaget af en anden
						foundfreepos = false;
				}
				
			}
		}

		return new Position(x, y);
	}


	public static void updatePlayer(Player player, int delta_x, int delta_y, String direction) {

		DebugLogger.log(player.getName() + ": " + player.direction + " to " + direction);
		player.direction = direction;

		int x = player.getX(),y = player.getY();

		if (board[y+delta_y].charAt(x+delta_x)=='w') {
			player.addPoints(-1); //TODO: remove?
		} 
		else {
			// collision detection
			Player p = getPlayerAt(x + delta_x,y + delta_y);
			if (p!=null) {
              player.addPoints(10);
              //update the other player
              p.addPoints(-69);
              Position pos = getRandomFreePosition();
              p.setPosition(pos);
              Position oldpos = new Position(x + delta_x, y + delta_y);
              Gui.movePlayerOnScreen(oldpos, pos, p.direction, player.getPlayerColor());
			  System.out.println("PLAYER COLLISION");

			}
			else {
				player.addPoints(1); //TODO: remove?
				Position oldpos = player.getPosition();
				Position newpos = new Position(x + delta_x, y + delta_y);
				Gui.movePlayerOnScreen(oldpos,newpos,direction, player.getPlayerColor());
				player.setPosition(newpos);

				DebugLogger.log(player.getName() + ": " + "(" + oldpos.getX() + "/" + oldpos.getY() + ")" + " to (" + newpos.getX() + "/" + newpos.getY() + ")");
			}

		}
		
		
	}


	/**
	 * Places a bomb on the players current tile.  <br>
	 * TODO: When moving out of the tile, the tile becomes occupied as if a player was there
	 * @param player the player to place a bomb
	 */
	public static void placeBomb(Player player){

		//TODO: cooldown not working
		//if(player.isBombActivated()) return;
		//player.startBombCooldownTimer();
		Bomb bomb = new Bomb(player);
		bombs.add(bomb);
		Gui.placeBombOnScreen(bomb);

		System.out.println("Bomb Placed by " + player.getName() + " (" + player.getX() + "/" + player.getY() + ")");

	}


	public static boolean isValidPosition(int x, int y) { return x >= 0 && x < board[0].length() && y >= 0 && y < board.length; }


	public static Player getPlayerAt(int x, int y) {
		for (Player p : players) {
			if (p.getX()==x && p.getY()==y) {
				return p;
			}
		}
		return null;
	}


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

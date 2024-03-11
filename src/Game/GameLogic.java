package Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;



public class GameLogic {
public static List<Player> players = new ArrayList<Player>();

	/**
	 * Can no longer be called before application launches
	 * @param name
	 * @return
	 */
	public static Player makePlayer(String name) {
		Player player;
		//TODO: take this out, currently makes the players spawn randomly each client
		playerPosition p=getRandomFreePosition();

		player = new Player(name, p,"up");
		players.add(player);
		System.out.println("Created Player: " + name + " x" + player.getXpos() + "/y" + player.getYpos());


		Gui.placePlayerOnScreen(new playerPosition(player.getXpos(), player.getYpos()), "up");

		return player;
	};

	/**
	 * @return playerPosition that is random, and not inside wall or other player
	 */
	public static playerPosition getRandomFreePosition() {
		int x = 1;
		int y = 1;
		boolean foundfreepos = false;
		while  (!foundfreepos) {
			Random r = new Random();
			x = Math.abs(r.nextInt()%18) +1;
			y = Math.abs(r.nextInt()%18) +1;
			if (Generel.board[y].charAt(x)==' ') // er det gulv ?
			{
				foundfreepos = true;
				for (Player p: players) {
					if (p.getXpos()==x && p.getYpos()==y) //pladsen optaget af en anden 
						foundfreepos = false;
				}
				
			}
		}
		playerPosition p = new playerPosition(x, y);
		return p;
	}
	
	public static void updatePlayer(Player player, int delta_x, int delta_y, String direction) {
		player.direction = direction;
		int x = player.getXpos(),y = player.getYpos();

		if (Generel.board[y+delta_y].charAt(x+delta_x)=='w') {
			player.addPoints(-1);
		} 
		else {
			// collision detection
			Player p = getPlayerAt(x+delta_x,y+delta_y);
			if (p!=null) {
              player.addPoints(10);
              //update the other player
              p.addPoints(-69);
              playerPosition pa = getRandomFreePosition();
              p.setLocation(pa);
              playerPosition oldpos = new playerPosition(x + delta_x, y + delta_y);
              Gui.movePlayerOnScreen(oldpos,pa,p.direction);
			} else 
				player.addPoints(1);
			playerPosition oldpos = player.getLocation();
			playerPosition newpos = new playerPosition(x + delta_x, y + delta_y);
			Gui.movePlayerOnScreen(oldpos,newpos,direction);
			player.setLocation(newpos);
		}
		
		
	}
	
	public static Player getPlayerAt(int x, int y) {
		for (Player p : players) {
			if (p.getXpos()==x && p.getYpos()==y) {
				return p;
			}
		}
		return null;
	}
	
	
	

}

package Game;

public class Player {

	String name;
	PlayerPosition location;
	int point;
	String direction;

	public Player(String name, PlayerPosition loc, String direction) {
		this.name = name;
		this.location = loc;
		this.direction = direction;
		this.point = 0;
	};

	public String getName() {

		return name;
	}
	public PlayerPosition getLocation() {
		return this.location;
	}
	public void setLocation(PlayerPosition p) {
		this.location=p;
	}
	public int getXpos() {
		return location.x;
	}
	public void setXpos(int xpos) {
		this.location.x = xpos;
	}
	public int getYpos() {
		return location.y;
	}
	public void setYpos(int ypos) {
		this.location.y = ypos;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public void addPoints(int p) {
		point+=p;
	}
	public String toString() {
		return name+":   "+point;
	}
}

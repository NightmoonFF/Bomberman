package Game;

public enum PlayerColor {
    RED("#870606"),
    BLUE("#065787"),
    GREEN("#067831"),
    PINK("#b92e7c");

    private final String hexCode;

    PlayerColor(String hexCode) { this.hexCode = hexCode; }

    public String getHexCode() { return hexCode; }
}

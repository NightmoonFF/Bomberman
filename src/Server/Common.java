package Server;

import Game.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Denne klasse er kommunikationsvejen fra hver tråd. Det er her, at klienttrådende sender beskeder
 * om hvad de har gjort i spillet - og opdateres fælles er i klassen. Dvs dette er serverens autoritære game-state
 * Dette er også "den kritiske sketion", altså her at trådende skal synkroniseres, eller i kø med en semafor.
 * Common - altså det som er til fælles for trådende/spillet
 */
public class Common {
    public static List<Player> players = new ArrayList<Player>();




}

package XMMT;
import java.util.Comparator;

public class GameComparator implements Comparator<Game> {
    public int compare(Game g1, Game g2) {      //This is used for the PriorityQueue to measure how important a game is relative to another
        return g2.getPriorityLevel() - g1.getPriorityLevel();
    }
}
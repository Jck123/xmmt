package XMMT;
import java.util.Comparator;

public class GameComparator implements Comparator<Game> {
    public int compare(Game g1, Game g2) {
        return g2.getPriorityLevel() - g1.getPriorityLevel();
    }
}
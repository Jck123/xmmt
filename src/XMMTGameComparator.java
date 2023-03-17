import java.util.Comparator;

public class XMMTGameComparator implements Comparator<XMMTGame> {
    public int compare(XMMTGame g1, XMMTGame g2) {
        return g2.getPriorityLevel() - g1.getPriorityLevel();
    }
}
import java.util.Comparator;

public class XMMTGameComparator implements Comparator<XMMTGame> {
    public int compare(XMMTGame g1, XMMTGame g2) {
        return g1.getPriorityLevel() - g2.getPriorityLevel();
    }
}
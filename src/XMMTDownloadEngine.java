import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

public class XMMTDownloadEngine {
    private PriorityQueue<XMMTGame> inPQueue;
    private PriorityQueue<XMMTGame> outPQueue;
    private ArrayList<XMMTGame> processingList;
    private HashMap<XMMTGame, XMMTDownloadThread> processingLists;

    public XMMTDownloadEngine() {
        inPQueue = new PriorityQueue<XMMTGame>(new XMMTGameComparator());
        outPQueue = new PriorityQueue<XMMTGame>(new XMMTGameComparator());
        processingList = new ArrayList<XMMTGame>();
        processingLists = new HashMap<XMMTGame, XMMTDownloadThread>();
    }

    public XMMTDownloadEngine(XMMTGame newGame) {
        inPQueue = new PriorityQueue<XMMTGame>(new XMMTGameComparator());
        outPQueue = new PriorityQueue<XMMTGame>(new XMMTGameComparator());
        processingList = new ArrayList<XMMTGame>();
        processingLists = new HashMap<XMMTGame, XMMTDownloadThread>();
        inPQueue.add(newGame);
    }

    public void runAll() {
        
    }

    public void pauseAll() {
        
    }

    public void stopAll() {

    }

    public boolean addToQueue(XMMTGame newGame) {
        return inPQueue.add(newGame);
    }

    public boolean addToQueue(String strURL) {
        return inPQueue.add(new XMMTGame(strURL));
    }

    public boolean removeFromQueue(XMMTGame game) {
        if (processingList.contains(game)) {
            //TODO: Add thread stopping
            return true;
        }
        return inPQueue.remove(game);
    }



    public double[] GetProgress(){
        //TODO: Add progress tracking ability
        return new double[1];
    }

    public double GetProgress(int index) {
        //TODO: Add progress tracking ability(single element)
        return 0;
    }
}
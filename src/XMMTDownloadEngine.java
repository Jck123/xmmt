import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

public class XMMTDownloadEngine {
    private XMMTDownloadEngineThread dT;
    private PriorityQueue<XMMTGame> inPQueue;
    private PriorityQueue<XMMTGame> outPQueue;
    private ArrayList<XMMTDownloadThread> processingList;
    private int DOWNLOAD_LIMIT = 4;

    public XMMTDownloadEngine() {
        inPQueue = new PriorityQueue<XMMTGame>(new XMMTGameComparator());
        outPQueue = new PriorityQueue<XMMTGame>(new XMMTGameComparator());
        processingList = new ArrayList<XMMTDownloadThread>();
    }

    public XMMTDownloadEngine(int downLimit) {
        inPQueue = new PriorityQueue<XMMTGame>(new XMMTGameComparator());
        outPQueue = new PriorityQueue<XMMTGame>(new XMMTGameComparator());
        processingList = new ArrayList<XMMTDownloadThread>();
        DOWNLOAD_LIMIT = downLimit;
    }

    public XMMTDownloadEngine(XMMTGame newGame) {
        inPQueue = new PriorityQueue<XMMTGame>(new XMMTGameComparator());
        outPQueue = new PriorityQueue<XMMTGame>(new XMMTGameComparator());
        processingList = new ArrayList<XMMTDownloadThread>();
        inPQueue.add(newGame);
    }

    public XMMTDownloadEngine(XMMTGame newGame, int downLimit) {
        inPQueue = new PriorityQueue<XMMTGame>(new XMMTGameComparator());
        outPQueue = new PriorityQueue<XMMTGame>(new XMMTGameComparator());
        processingList = new ArrayList<XMMTDownloadThread>();
        DOWNLOAD_LIMIT = downLimit;
        inPQueue.add(newGame);
    }

    public void start() {
        if (dT == null)
            dT = new XMMTDownloadEngineThread(this);
        dT.start();
    }

    public void pauseAll() {
        for(XMMTDownloadThread t : processingList) {
            t.pauseThread();
        }
        dT.pauseThread();
    }

    public boolean pause(XMMTGame g) {
        for(XMMTDownloadThread t : processingList) {
            if (t.getGame().equals(g)) {
                t.pauseThread();
                return true;
            }
        }
        return false;
    }

    public void stopAll() {
        for(XMMTDownloadThread t : processingList) {
            t.interrupt();
            t.getGame().getCompressedPath().toFile().delete();
            inPQueue.add(t.getGame());
        }
        processingList.clear();
        dT.stopThread();

    }

    public boolean addToQueue(XMMTGame newGame) {
        return inPQueue.add(newGame);
    }

    public boolean addToQueue(String strURL) {
        return inPQueue.add(new XMMTGame(strURL));
    }

    public boolean removeFromQueue(XMMTGame game) {
        for(XMMTDownloadThread t : processingList) {
            XMMTGame g = t.getGame();
            if (g.equals(game)) {
                t.interrupt();
                g.getCompressedPath().toFile().delete();
                return true;
            }
        }
        return inPQueue.remove(game);
    }

    public HashMap<XMMTGame, Double> GetProgress(){
        HashMap<XMMTGame, Double> progress = new HashMap<XMMTGame, Double>();
        for(XMMTDownloadThread t : processingList) {
            progress.put(t.getGame(), t.GetProgess());
        }
        return progress;
    }

    public double GetProgress(XMMTGame game) {
        for(XMMTDownloadThread t : processingList) {
            if (t.getGame().equals(game)) {
                return t.GetProgess();
            }
        }
        return -1;
    }

    public void setDownloadLimit(int num) {
        DOWNLOAD_LIMIT = num;
    }

    public void refresh() {
        for(XMMTDownloadThread t : processingList) {
            //System.out.println("Got to XMMTDownloadEngine.java:117");
            if (!t.isAlive()) {
                //System.out.println("Got to XMMTDownloadEngine.java:119");
                outPQueue.add(t.getGame());
                processingList.remove(t);
            }
        }
        while(!inPQueue.isEmpty() && processingList.size() < DOWNLOAD_LIMIT) {
            XMMTDownloadThread t = new XMMTDownloadThread(inPQueue.poll());
            processingList.add(t);
            t.start();
        }
    }
}
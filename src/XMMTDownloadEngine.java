import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

public class XMMTDownloadEngine implements XMMTEngineInterface{
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
        refresh();
    }

    public void pauseAll() {
        for(XMMTDownloadThread t : processingList) {
            t.pauseThread();
        }
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
            t.getGame().deleteLocalFiles();
            inPQueue.add(t.getGame());
        }
        processingList.clear();
    }

    public boolean addToQueue(XMMTGame newGame) {
        boolean rc = inPQueue.add(newGame);
        this.refresh();
        return rc;
    }

    public boolean addToQueue(String strURL) {
        boolean rc = inPQueue.add(new XMMTGame(strURL));
        this.refresh();
        return rc;
    }

    public boolean removeFromQueue(XMMTGame game) {
        for(XMMTDownloadThread t : processingList) {
            XMMTGame g = t.getGame();
            if (g.equals(game)) {
                t.interrupt();
                processingList.remove(t);
                g.deleteLocalFiles();
                this.refresh();
                return true;
            }
        }
        boolean rc = inPQueue.remove(game);
        this.refresh();
        return rc;
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
        refresh();
    }

    public void refresh() {
        while(!inPQueue.isEmpty() && processingList.size() < DOWNLOAD_LIMIT) {
            XMMTDownloadThread t = new XMMTDownloadThread(inPQueue.poll(), this);
            processingList.add(t);
            t.start();
        }
    }

    public void completeDownload(XMMTThread t) {
        XMMTGame g = t.getGame();
        processingList.remove(t);
        outPQueue.add(g);
        refresh();
    }
}
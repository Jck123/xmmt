import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

public class XMMTThreadEngine<T extends XMMTThread> implements XMMTEngineInterface{
    private PriorityQueue<XMMTGame> inPQueue;
    private PriorityQueue<XMMTGame> outPQueue;
    private ArrayList<T> processingList;
    private int DOWNLOAD_LIMIT = 4;
    private Class<T> clazz;

    public XMMTThreadEngine(Class<T> clz) {
        inPQueue = new PriorityQueue<XMMTGame>(new XMMTGameComparator());
        outPQueue = new PriorityQueue<XMMTGame>(new XMMTGameComparator());
        processingList = new ArrayList<T>();
        clazz = clz;
    }

    public XMMTThreadEngine(Class<T> clz, int downLimit) {
        inPQueue = new PriorityQueue<XMMTGame>(new XMMTGameComparator());
        outPQueue = new PriorityQueue<XMMTGame>(new XMMTGameComparator());
        processingList = new ArrayList<T>();
        DOWNLOAD_LIMIT = downLimit;
        clazz = clz;
    }

    public XMMTThreadEngine(Class<T> clz, XMMTGame newGame) {
        inPQueue = new PriorityQueue<XMMTGame>(new XMMTGameComparator());
        outPQueue = new PriorityQueue<XMMTGame>(new XMMTGameComparator());
        processingList = new ArrayList<T>();
        inPQueue.add(newGame);
        clazz = clz;
    }

    public XMMTThreadEngine(Class<T> clz, XMMTGame newGame, int downLimit) {
        inPQueue = new PriorityQueue<XMMTGame>(new XMMTGameComparator());
        outPQueue = new PriorityQueue<XMMTGame>(new XMMTGameComparator());
        processingList = new ArrayList<T>();
        DOWNLOAD_LIMIT = downLimit;
        inPQueue.add(newGame);
        clazz = clz;
    }

    public void start() {
        refresh();
    }

    public void pauseAll() {
        for(T t : processingList) {
            t.pauseThread();
        }
    }

    public boolean pause(XMMTGame g) {
        for(XMMTThread t : processingList) {
            if (t.getGame().equals(g)) {
                t.pauseThread();
                return true;
            }
        }
        return false;
    }

    public void stopAll() {
        for(XMMTThread t : processingList) {
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
        for(XMMTThread t : processingList) {
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
        for(XMMTThread t : processingList) {
            progress.put(t.getGame(), t.GetProgess());
        }
        return progress;
    }

    public double GetProgress(XMMTGame game) {
        for(XMMTThread t : processingList) {
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
            try {
                T t = clazz.getDeclaredConstructor(XMMTGame.class, XMMTEngineInterface.class).newInstance(inPQueue.poll(), this);
                processingList.add(t);
                t.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void completeDownload(XMMTThread t) {
        XMMTGame g = t.getGame();
        processingList.remove(t);
        outPQueue.add(g);
        refresh();
    }
}
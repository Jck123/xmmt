package XMMT;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

public class ThreadEngine<T extends XMMTThread> implements EngineInterface{
    private PriorityQueue<Game> inPQueue;
    private PriorityQueue<Game> outPQueue;
    private ArrayList<T> processingList;
    private int DOWNLOAD_LIMIT = 4;
    private Class<T> clazz;
    private String destPath;

    public ThreadEngine(Class<T> clz) {
        inPQueue = new PriorityQueue<Game>(new GameComparator());
        outPQueue = new PriorityQueue<Game>(new GameComparator());
        processingList = new ArrayList<T>();
        clazz = clz;
        destPath = "";
    }

    public ThreadEngine(Class<T> clz, int downLimit) {
        inPQueue = new PriorityQueue<Game>(new GameComparator());
        outPQueue = new PriorityQueue<Game>(new GameComparator());
        processingList = new ArrayList<T>();
        DOWNLOAD_LIMIT = downLimit;
        clazz = clz;
        destPath = "";
    }

    public ThreadEngine(Class<T> clz, int downLimit, String path) {
        inPQueue = new PriorityQueue<Game>(new GameComparator());
        outPQueue = new PriorityQueue<Game>(new GameComparator());
        processingList = new ArrayList<T>();
        DOWNLOAD_LIMIT = downLimit;
        clazz = clz;
        destPath = path;
    }

    public ThreadEngine(Class<T> clz, Game newGame) {
        inPQueue = new PriorityQueue<Game>(new GameComparator());
        outPQueue = new PriorityQueue<Game>(new GameComparator());
        processingList = new ArrayList<T>();
        inPQueue.add(newGame);
        clazz = clz;
        destPath = "";
    }

    public ThreadEngine(Class<T> clz, Game newGame, String path) {
        inPQueue = new PriorityQueue<Game>(new GameComparator());
        outPQueue = new PriorityQueue<Game>(new GameComparator());
        processingList = new ArrayList<T>();
        inPQueue.add(newGame);
        clazz = clz;
        destPath = path;
    }

    public ThreadEngine(Class<T> clz, Game newGame, int downLimit) {
        inPQueue = new PriorityQueue<Game>(new GameComparator());
        outPQueue = new PriorityQueue<Game>(new GameComparator());
        processingList = new ArrayList<T>();
        DOWNLOAD_LIMIT = downLimit;
        inPQueue.add(newGame);
        clazz = clz;
        destPath = "";
    }

    public ThreadEngine(Class<T> clz, Game newGame, String path, int downLimit) {
        inPQueue = new PriorityQueue<Game>(new GameComparator());
        outPQueue = new PriorityQueue<Game>(new GameComparator());
        processingList = new ArrayList<T>();
        DOWNLOAD_LIMIT = downLimit;
        inPQueue.add(newGame);
        clazz = clz;
        destPath = path;
    }

    public void start() {
        refresh();
    }

    public void pauseAll() {
        for(T t : processingList) {
            t.pauseThread();
        }
    }

    public boolean pause(Game g) {
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
            t.getGame().deleteAllLocalFiles();
            inPQueue.add(t.getGame());
        }
        processingList.clear();
    }

    public boolean addToQueue(Game newGame) {
        boolean rc = inPQueue.add(newGame);
        this.refresh();
        return rc;
    }

    public boolean addToQueue(String strURL) {
        boolean rc = inPQueue.add(new Game(strURL));
        this.refresh();
        return rc;
    }

    public boolean removeFromQueue(Game game) {
        for(XMMTThread t : processingList) {
            Game g = t.getGame();
            if (g.equals(game)) {
                
                t.interrupt();
                processingList.remove(t);
                g.deleteAllLocalFiles();
                this.refresh();
                return true;
            }
        }
        boolean rc = inPQueue.remove(game);
        this.refresh();
        return rc;
    }

    public HashMap<Game, Double> GetProgress(){
        HashMap<Game, Double> progress = new HashMap<Game, Double>();
        for(XMMTThread t : processingList) {
            progress.put(t.getGame(), t.GetProgess());
        }
        return progress;
    }

    public double GetProgress(Game game) {
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
                T t = clazz.getDeclaredConstructor(Game.class, EngineInterface.class, String.class).newInstance(inPQueue.poll(), this, destPath);
                processingList.add(t);
                t.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void completeDownload(XMMTThread t) {
        Game g = t.getGame();
        processingList.remove(t);
        outPQueue.add(g);
        refresh();
    }
}
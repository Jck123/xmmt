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
    private String[] args;
    private boolean paused = true;
    private boolean joined = false;
    private EngineInterface linkedEngine = null;

    public ThreadEngine(Class<T> clz) {
        inPQueue = new PriorityQueue<Game>(new GameComparator());
        outPQueue = new PriorityQueue<Game>(new GameComparator());
        processingList = new ArrayList<T>();
        clazz = clz;
        args = null;
    }

    public ThreadEngine(Class<T> clz, String... a) {
        inPQueue = new PriorityQueue<Game>(new GameComparator());
        outPQueue = new PriorityQueue<Game>(new GameComparator());
        processingList = new ArrayList<T>();
        clazz = clz;
        args = a;
    }

    public ThreadEngine(Class<T> clz, int downLimit) {
        inPQueue = new PriorityQueue<Game>(new GameComparator());
        outPQueue = new PriorityQueue<Game>(new GameComparator());
        processingList = new ArrayList<T>();
        DOWNLOAD_LIMIT = downLimit;
        clazz = clz;
        args = null;
    }

    public ThreadEngine(Class<T> clz, int downLimit, String... a) {
        inPQueue = new PriorityQueue<Game>(new GameComparator());
        outPQueue = new PriorityQueue<Game>(new GameComparator());
        processingList = new ArrayList<T>();
        DOWNLOAD_LIMIT = downLimit;
        clazz = clz;
        args = a;
    }

    public ThreadEngine(Class<T> clz, Game newGame) {
        inPQueue = new PriorityQueue<Game>(new GameComparator());
        outPQueue = new PriorityQueue<Game>(new GameComparator());
        processingList = new ArrayList<T>();
        inPQueue.add(newGame);
        clazz = clz;
        args = null;
    }

    public ThreadEngine(Class<T> clz, Game newGame, String... a) {
        inPQueue = new PriorityQueue<Game>(new GameComparator());
        outPQueue = new PriorityQueue<Game>(new GameComparator());
        processingList = new ArrayList<T>();
        inPQueue.add(newGame);
        clazz = clz;
        args = a;
    }

    public ThreadEngine(Class<T> clz, Game newGame, int downLimit) {
        inPQueue = new PriorityQueue<Game>(new GameComparator());
        outPQueue = new PriorityQueue<Game>(new GameComparator());
        processingList = new ArrayList<T>();
        DOWNLOAD_LIMIT = downLimit;
        inPQueue.add(newGame);
        clazz = clz;
        args = null;
    }

    public ThreadEngine(Class<T> clz, Game newGame, int downLimit, String... a) {
        inPQueue = new PriorityQueue<Game>(new GameComparator());
        outPQueue = new PriorityQueue<Game>(new GameComparator());
        processingList = new ArrayList<T>();
        DOWNLOAD_LIMIT = downLimit;
        inPQueue.add(newGame);
        clazz = clz;
        args = a;
    }

    public void startAll() {
        paused = false;
        for(T t : processingList) {
            t.resumeThread();
        }
        refresh();
    }

    public boolean start(Game g) {
        for(XMMTThread t : processingList) {
            if (t.getGame().equals(g)) {
                t.resumeThread();
                return true;
            }
        }
        return false;
    }

    public void pauseAll() {
        paused = true;
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
        paused = true;
        for(XMMTThread t : processingList) {
            t.interrupt();
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
                this.refresh();
                return true;
            }
        }
        boolean rc = inPQueue.remove(game);
        this.refresh();
        return rc;
    }

    public void clearAll() {
        stopAll();
        inPQueue.clear();
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
        if (paused)
            return;
        if (inPQueue.isEmpty() && processingList.isEmpty())
            paused = true;
        while(!inPQueue.isEmpty() && processingList.size() < DOWNLOAD_LIMIT) {
            try {
                T t = null;
                if (args == null)
                    t = clazz.getDeclaredConstructor(Game.class, EngineInterface.class).newInstance(inPQueue.poll(), this);
                else
                    t = clazz.getDeclaredConstructor(Game.class, EngineInterface.class, String[].class).newInstance(inPQueue.poll(), this, args);
                processingList.add(t);
                t.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void join() throws InterruptedException{
        joined = true;
        while (!processingList.isEmpty() && joined) {
            Thread.sleep(1000);
        }
    }

    public Game poll() {
        return outPQueue.poll();
    }

    public Game peek() {
        return outPQueue.peek();
    }

    public void completeProcess(XMMTThread t) {
        Game g = t.getGame();
        processingList.remove(t);
        if (linkedEngine != null) {
            linkedEngine.addToQueue(g);
            linkedEngine.startAll();
        }
        else
            outPQueue.add(g);
        refresh();
        joined = false;
    }

    public void failProcess(XMMTThread t) {
        processingList.remove(t);
        refresh();
        joined = false;
    }

    public void setPriorityLevel(Game g, int priLvl) {
        if (g == null)
            return;
        g.setPriorityLevel(priLvl);
        if (inPQueue.contains(g)) {
            inPQueue.remove(g);
            inPQueue.add(g);
        } else if (outPQueue.contains(g)) {
            outPQueue.remove(g);
            outPQueue.add(g);
        }
    }

    public void linkEngine(EngineInterface engine) {
        linkedEngine = engine;
    }
}
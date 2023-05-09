package XMMT;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

public class ThreadEngine<T extends XMMTThread> implements EngineInterface{
    private PriorityQueue<Game> inPQueue;
    private PriorityQueue<Game> outPQueue;
    private ArrayList<T> processingList;
    private int CONCURRENT_LIMIT = 4;
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
        CONCURRENT_LIMIT = downLimit;
        clazz = clz;
        args = null;
    }

    public ThreadEngine(Class<T> clz, int downLimit, String... a) {
        inPQueue = new PriorityQueue<Game>(new GameComparator());
        outPQueue = new PriorityQueue<Game>(new GameComparator());
        processingList = new ArrayList<T>();
        CONCURRENT_LIMIT = downLimit;
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
        CONCURRENT_LIMIT = downLimit;
        inPQueue.add(newGame);
        clazz = clz;
        args = null;
    }

    public ThreadEngine(Class<T> clz, Game newGame, int downLimit, String... a) {
        inPQueue = new PriorityQueue<Game>(new GameComparator());
        outPQueue = new PriorityQueue<Game>(new GameComparator());
        processingList = new ArrayList<T>();
        CONCURRENT_LIMIT = downLimit;
        inPQueue.add(newGame);
        clazz = clz;
        args = a;
    }

    //Starts all threads
    public void startAll() {
        paused = false;
        for(T t : processingList) {
            t.resumeThread();
        }
        refresh();
    }

    //Starts only one thread/game, only if it's in progress, returns if successful
    public boolean start(Game g) {
        for(XMMTThread t : processingList) {
            if (t.getGame().equals(g)) {
                t.resumeThread();
                return true;
            }
        }
        return false;
    }

    //Pauses entire engine
    public void pauseAll() {
        paused = true;
        for(T t : processingList) {
            t.pauseThread();
        }
    }

    //Pauses a single thread/game, returns if successful
    public boolean pause(Game g) {
        for(XMMTThread t : processingList) {
            if (t.getGame().equals(g)) {
                t.pauseThread();
                return true;
            }
        }
        return false;
    }

    //Stops all threads, returns them to input Pqueue and clears processinglist
    public void stopAll() {
        paused = true;
        for(XMMTThread t : processingList) {
            t.interrupt();
            inPQueue.add(t.getGame());
        }
        processingList.clear();
    }

    //Adds game to input queue and refreshes to see if game can be added to processing list, returns if addition was successful
    public boolean addToQueue(Game newGame) {
        boolean rc = inPQueue.add(newGame);
        this.refresh();
        return rc;
    }

    //Same as above, but accepts just URL and makes game from that
    public boolean addToQueue(String strURL) {
        boolean rc = inPQueue.add(new Game(strURL));
        this.refresh();
        return rc;
    }

    //Removes game from queue, stopping it if it's currently being processed. Returns if successful
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

    //Empties entire engine of games not yet full processed
    public void clearAll() {
        stopAll();
        inPQueue.clear();
    }

    //Gets progress of all games currently being processed, returns hashmap of game object and the current progress
    public HashMap<Game, Double> GetProgress(){
        HashMap<Game, Double> progress = new HashMap<Game, Double>();
        for(XMMTThread t : processingList) {
            progress.put(t.getGame(), t.GetProgess());
        }
        return progress;
    }

    //Gets progress of single game, returns as double or -1 if game not found
    public double GetProgress(Game game) {
        for(XMMTThread t : processingList) {
            if (t.getGame().equals(game)) {
                return t.GetProgess();
            }
        }
        return -1;
    }

    //Sets maximum number of allowed threads to run simultaneously
    public void setConcurrentLimit(int num) {
        CONCURRENT_LIMIT = num;
        refresh();
    }

    //Refreshes current processingList
    public void refresh() {
        if (paused)     //Skips if engine is paused
            return;
        if (inPQueue.isEmpty() && processingList.isEmpty())     //Automatically pauses if input queue and processing list is empty
            paused = true;
        while(!inPQueue.isEmpty() && processingList.size() < CONCURRENT_LIMIT) {    //Creates a new thread for each game that it can
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

    public void join() throws InterruptedException{     //Works similar to Thread.join(), except you wait for the next game to either pass or fail
        joined = true;
        while (!processingList.isEmpty() && joined) {
            Thread.sleep(1000);
        }
    }

    public Game poll() {                                //Gets next game output queue and removes it from output queue
        return outPQueue.poll();
    }

    public Game peek() {                                //Looks at next game in output queue, doesn't change queue
        return outPQueue.peek();
    }

    public void completeProcess(XMMTThread t) {         //Runs whenever a thread has completed their process successfully
        Game g = t.getGame();
        processingList.remove(t);       //Removes from processing list
        if (linkedEngine != null) {
            linkedEngine.addToQueue(g);     //Sends game directly to next engine IF LINKED
            linkedEngine.startAll();
        }
        else
            outPQueue.add(g);               //Otherwise adds to output queue
        refresh();
        joined = false;                     //Informs any joins they can leave now
    }

    public void failProcess(XMMTThread t) { //Runs whenever a thread has failed their process and removes them from processingList
        processingList.remove(t);
        refresh();
        joined = false;
    }

    public void setPriorityLevel(Game g, int priLvl) {      //Sets priority level of game within queue, allows pQueues to update the game's spot in the list
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

    public void linkEngine(EngineInterface engine) {        //Links two engines together, allowing one to send output directly into the other's input
        linkedEngine = engine;
    }
}
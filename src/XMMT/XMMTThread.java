package XMMT;
public abstract class XMMTThread extends Thread{
    Double progress = 0.0;
    boolean paused = false;
    Game game;
    EngineInterface engine;
    String[] args;

    public XMMTThread() {
        game = null;
        engine = null;
        args = null;
    }

    public XMMTThread(Game g, EngineInterface e) {
        game = g;
        engine = e;
        args = null;
    }

    public XMMTThread(Game g, EngineInterface e, String... a) {
        game = g;
        engine = e;
        args = a;
    }
    
    public abstract void run();
    
    public final double GetProgess() {
        return progress;
    }

    //Sends signal for thread to pause
    public final void pauseThread() {
        paused = true;
    }

    //Send signal for thread to resume
    public final void resumeThread() {
        paused = false;
    }

    //Returns if thread is currently paused
    public final boolean paused() {
        return paused;
    }

    //Returns game thread is working on
    public final Game getGame() {
        return game;
    }

    //Sets args of thread
    public final void setArgs(String... a) {
        args = a;
    }

    //Sends flag to engine that thread is complete
    public final void sendCompleteFlag() {
        engine.completeProcess(this);
    }

    //Sends flag to engine that thread has failed
    public final void sendFailFlag() {
        engine.failProcess(this);
    }
}
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

    public final void pauseThread() {
        paused = true;
    }

    public final void resumeThread() {
        paused = false;
    }

    public final boolean paused() {
        return paused;
    }

    public final Game getGame() {
        return game;
    }

    public final void setArgs(String... a) {
        args = a;
    }

    public final void sendCompleteFlag() {
        engine.completeProcess(this);
    }

    public final void sendFailFlag() {
        engine.failProcess(this);
    }
}
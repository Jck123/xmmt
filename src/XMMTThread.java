public abstract class XMMTThread extends Thread{
    Double progress = 0.0;
    boolean paused = false;
    XMMTGame game;
    XMMTEngineInterface engine;
    String destPath;

    public XMMTThread() {
        game = null;
        engine = null;
        destPath = "";
    }

    public XMMTThread(XMMTGame g, XMMTEngineInterface e) {
        game = g;
        engine = e;
        destPath = "";
    }

    public XMMTThread(XMMTGame g, XMMTEngineInterface e, String p) {
        game = g;
        engine = e;
        destPath = p;
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

    public final XMMTGame getGame() {
        return game;
    }

    public final void setDestPath(String newPath) {
        destPath = newPath;
    }

    public final void sendCompleteFlag() {
        engine.completeDownload(this);
    }
}
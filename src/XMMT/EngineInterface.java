package XMMT;
public interface EngineInterface {
    public boolean addToQueue(Game g);
    public void startAll();
    public void completeProcess(XMMTThread t);
    public void failProcess(XMMTThread t);
}

package XMMT;
public interface EngineInterface {
    //Please refer to ThreadEngine for notes on what these do
    public boolean addToQueue(Game g);
    public void startAll();
    public void completeProcess(XMMTThread t);
    public void failProcess(XMMTThread t);
}

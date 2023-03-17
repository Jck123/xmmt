import java.util.HashMap;

public class XMMTDownloadEngineThread extends Thread {
    private int state;
    private XMMTDownloadEngine engine;

    public XMMTDownloadEngineThread(XMMTDownloadEngine de) {
        engine = de;
        state = 1;
    }

    public void run() {
        //System.out.println("Got to XMMTDownloadEngineThread.java:13");
        while(state > 0) {
            while (state == 2)
                try {Thread.sleep(1000);} catch (Exception e) {e.printStackTrace();}
            //System.out.println("Got to XMMTDownloadEngineThread.java:17");
            engine.refresh();
            HashMap<XMMTGame, Double> out = engine.GetProgress();
            //System.out.println("Got to XMMTDownloadEngineThread.java:20");
            out.forEach((g, p) -> {
                System.out.println(g.getName() + ": " + p + "%");
            });
            try {Thread.sleep(1000);} catch (Exception e) {e.printStackTrace();}
        }
    }

    public void pauseThread() {state = 2;}
    public void resumeThread() {state = 1;}
    public void stopThread() {state = 0;}
}

package Tests;
import XMMT.Game;
import XMMT.ThreadEngine;
import XMMT.DownloadThread;

public class XMMTDownloadThreadTest {
    private static int passCount = 0;
    public static int totalPasses = 7;
    private static boolean verbose = false;

    public static int runTests(boolean v) throws InterruptedException {
        verbose = v;
        main(null);
        return passCount;
    }
    public static void main(String[] args) throws InterruptedException {        
        Game g1 = new Game("https://archive.org/download/xbox_eng_romset/AMF%20Bowling%202004%20%5B%21%5D.7z");
        ThreadEngine<DownloadThread> de = new ThreadEngine<DownloadThread>(DownloadThread.class);
        DownloadThread dT = new DownloadThread(g1, de, "Downloads/");
        DownloadThread dT2 = new DownloadThread(g1, de);


        if (dT != null && dT2 != null) {
            if (verbose)
                System.out.println("Object initialization:\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("Object initialization:\t\tFAILED");
        }

        if (dT.GetProgess() == 0) {
            if (verbose)
                System.out.println("GetProgress():\t\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("GetProgress():\t\t\tFAILED");
        }

        if (dT.getGame() == g1) {
            if (verbose)
                System.out.println("getGame():\t\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("getGame():\t\t\tFAILED");
        }

        dT.start();

        Thread.sleep(5000);

        if (dT.GetProgess() > 0 && g1.getCompressedPath().exists()) {
            if (verbose)
                System.out.println("start():\t\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("start():\t\t\tFAILED");
        }

        dT.pauseThread();
        Thread.sleep(100);
        double dTp = dT.GetProgess();

        if (dT.GetProgess() == dTp && dT.paused()) {
            if (verbose)
                System.out.println("pauseThread():\t\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("pauseThread():\t\t\tFAILED");
        }

        dT.resumeThread();
        Thread.sleep(1000);

        if (dT.GetProgess() > dTp && !dT.paused()) {
            if (verbose)
                System.out.println("resumeThread():\t\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("resumeThread():\t\t\tFAILED");
        }

        dT.interrupt();
        if (dT.isAlive())
            dT.join();

        if (!dT.isAlive() && !g1.getCompressedPath().exists()) {
            if (verbose)
                System.out.println("interrupt():\t\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("interrupt():\t\t\tFAILED");
        }

        System.out.println("DownloadThread pass count: " + passCount + " out of " + totalPasses);
    }
}

package Tests;
import XMMT.Game;
import XMMT.ExtractionThread;
import XMMT.DownloadThread;
import XMMT.ThreadEngine;

public class XMMTExtractionThreadTest {
    private static int passCount = 0;
    public static int totalPasses = 7;
    private static boolean verbose = false;

    public static int runTests(boolean v) throws InterruptedException{
        verbose = v;
        main(null);
        return passCount;
    }
    public static void main(String[] args) throws InterruptedException {        
        Game g1 = new Game("https://archive.org/download/xbox_eng_romset/AMF%20Bowling%202004%20%5B%21%5D.7z");
        ThreadEngine<ExtractionThread> ee = new ThreadEngine<ExtractionThread>(ExtractionThread.class);
        DownloadThread dT = new DownloadThread(g1, ee, "Downloads/");
        ExtractionThread eT = new ExtractionThread(g1, ee, "Extracts/");
        ExtractionThread eT2 = new ExtractionThread(g1, ee);
        
        if (verbose)
            System.out.println("Downloading game to test with, please hold...");

        dT.start();
        dT.join();

        if (eT != null && eT2 != null) {
            if (verbose)
                System.out.println("Object initialization:\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("Object initialization:\t\tFAILED");
        }

        if (eT.GetProgess() == 0) {
            if (verbose)
                System.out.println("GetProgress():\t\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("GetProgress():\t\t\tFAILED");
        }

        if (eT.getGame() == g1) {
            if (verbose)
                System.out.println("getGame():\t\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("getGame():\t\t\tFAILED");
        }

        eT.start();

        Thread.sleep(5000);

        if (eT.GetProgess() > 0 && g1.getDecompressedPath().exists()) {
            if (verbose)
                System.out.println("start():\t\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("start():\t\t\tFAILED");
        }

        eT.pauseThread();
        Thread.sleep(100);
        double eTp = eT.GetProgess();

        if (eT.GetProgess() == eTp && eT.paused()) {
            if (verbose)
                System.out.println("pauseThread():\t\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("pauseThread():\t\t\tFAILED");
        }

        eT.resumeThread();
        Thread.sleep(1000);

        if (eT.GetProgess() > eTp && !eT.paused()) {
            if (verbose)
                System.out.println("resumeThread():\t\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("resumeThread():\t\t\tFAILED");
        }

        eT.interrupt();
        if (eT.isAlive())
            eT.join();
        
        if (!eT.isAlive() && g1.getDecompressedPath() == null && g1.getCompressedPath().exists()) {
            if (verbose)
                System.out.println("interrupt():\t\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("interrupt():\t\t\tFAILED");
        }

        g1.deleteAllLocalFiles();

        System.out.println("ExtractionThread pass count: " + passCount + " out of " + totalPasses);
    }
}

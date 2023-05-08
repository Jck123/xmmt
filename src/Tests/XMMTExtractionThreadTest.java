package Tests;
import XMMT.Game;
import XMMT.ExtractionThread;
import XMMT.DownloadThread;
import XMMT.ThreadEngine;

public class XMMTExtractionThreadTest {
    public static void main(String[] args) throws InterruptedException {
        int passCount = 0;
        
        Game g1 = new Game("https://archive.org/download/xbox_eng_romset/AMF%20Bowling%202004%20%5B%21%5D.7z");
        ThreadEngine<ExtractionThread> ee = new ThreadEngine<ExtractionThread>(ExtractionThread.class);
        DownloadThread dT = new DownloadThread(g1, ee, "Downloads/");
        ExtractionThread eT = new ExtractionThread(g1, ee, "Extracts/");
        ExtractionThread eT2 = new ExtractionThread(g1, ee);
        
        System.out.println("Downloading game to test with, please hold...");

        dT.start();
        dT.join();

        if (eT != null && eT2 != null) {
            System.out.println("Object initialization:\tPASSED");
            passCount++;
        } else {
            System.out.println("Object initialization:\tFAILED");
        }

        if (eT.GetProgess() == 0) {
            System.out.println("GetProgress():\t\tPASSED");
            passCount++;
        } else {
            System.out.println("GetProgress():\t\tFAILED");
        }

        if (eT.getGame() == g1) {
            System.out.println("getGame():\t\tPASSED");
            passCount++;
        } else {
            System.out.println("getGame():\t\tFAILED");
        }

        eT.start();

        Thread.sleep(5000);

        if (eT.GetProgess() > 0 && g1.getDecompressedPath().exists()) {
            System.out.println("start():\t\tPASSED");
            passCount++;
        } else {
            System.out.println("start():\t\tFAILED");
        }

        eT.pauseThread();
        Thread.sleep(100);
        double eTp = eT.GetProgess();

        if (eT.GetProgess() == eTp && eT.paused()) {
            System.out.println("pauseThread():\t\tPASSED");
            passCount++;
        } else {
            System.out.println("pauseThread():\t\tFAILED");
        }

        eT.resumeThread();
        Thread.sleep(1000);

        if (eT.GetProgess() > eTp && !eT.paused()) {
            System.out.println("resumeThread():\t\tPASSED");
            passCount++;
        } else {
            System.out.println("resumeThread():\t\tFAILED");
        }

        eT.interrupt();
        if (eT.isAlive())
            eT.join();
        
        if (!eT.isAlive() && !g1.getDecompressedPath().exists() && g1.getCompressedPath().exists()) {
            System.out.println("interrupt():\t\tPASSED");
            passCount++;
        } else {
            System.out.println("interrupt():\t\tFAILED");
        }

        g1.deleteAllLocalFiles();

        System.out.println("Total pass count: " + passCount + " out of 7");
    }
}

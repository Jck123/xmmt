package Tests;
import XMMT.Game;
import XMMT.ThreadEngine;
import XMMT.DownloadThread;

public class XMMTDownloadThreadTest {
    public static void main(String[] args) throws InterruptedException {
        int passCount = 0;
        
        Game g1 = new Game("https://archive.org/download/xbox_eng_romset/AMF%20Bowling%202004%20%5B%21%5D.7z");
        ThreadEngine<DownloadThread> de = new ThreadEngine<DownloadThread>(DownloadThread.class);
        DownloadThread dT = new DownloadThread(g1, de, "Downloads/");
        DownloadThread dT2 = new DownloadThread(g1, de);


        if (dT != null && dT2 != null) {
            System.out.println("Object initialization:\tPASSED");
            passCount++;
        } else {
            System.out.println("Object initialization:\tFAILED");
        }

        if (dT.GetProgess() == 0) {
            System.out.println("GetProgress():\t\tPASSED");
            passCount++;
        } else {
            System.out.println("GetProgress():\t\tFAILED");
        }

        if (dT.getGame() == g1) {
            System.out.println("getGame():\t\tPASSED");
            passCount++;
        } else {
            System.out.println("getGame():\t\tFAILED");
        }

        dT.start();

        Thread.sleep(5000);

        if (dT.GetProgess() > 0 && g1.getCompressedPath().exists()) {
            System.out.println("start():\t\tPASSED");
            passCount++;
        } else {
            System.out.println("start():\t\tFAILED");
        }

        dT.pauseThread();
        Thread.sleep(100);
        double dTp = dT.GetProgess();

        if (dT.GetProgess() == dTp && dT.paused() == true) {
            System.out.println("pauseThread():\t\tPASSED");
            passCount++;
        } else {
            System.out.println("pauseThread():\t\tFAILED");
        }

        dT.resumeThread();
        Thread.sleep(1000);

        if (dT.GetProgess() > dTp && dT.paused() == false) {
            System.out.println("resumeThread():\t\tPASSED");
            passCount++;
        } else {
            System.out.println("resumeThread():\t\tFAILED");
        }

        dT.interrupt();
        dT.join();
        g1.deleteCompressedFile();

        if (!dT.isAlive()) {
            System.out.println("interrupt():\t\tPASSED");
            passCount++;
        } else {
            System.out.println("interrupt():\t\tFAILED");
        }

        System.out.println("Total pass count: " + passCount + " out of 7");
    }
}

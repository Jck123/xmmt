package Tests;
import XMMT.Game;
import XMMT.DownloadThread;
import XMMT.ExtractionThread;
import XMMT.ThreadEngine;

import java.util.HashMap;

public class XMMTThreadEngineTest {
    private static int passCount = 0;
    public static int totalPasses = 18;
    private static boolean verbose = true;

    public static int runTests(boolean v) throws InterruptedException{
        verbose = v;
        main(null);
        return passCount;
    }
    public static void main(String[] args) throws InterruptedException{
        Game g1 = new Game("https://archive.org/download/xbox_eng_romset/AMF%20Bowling%202004%20%5B%21%5D.7z");
        Game g2 = new Game("https://archive.org/download/xbox_eng_romset/Advent%20Rising%20%5B%21%5D.7z");
        Game g3 = new Game("https://archive.org/download/xbox_eng_romset/Aeon%20Flux%20%5B%21%5D.7z");
        Game g4 = new Game("https://archive.org/download/xbox_eng_romset/Aggressive%20Inline%20%5B%21%5D.7z");
        Game g5 = new Game("https://archive.org/download/xbox_eng_romset/Alias%20%5B%21%5D.7z");
        ThreadEngine<DownloadThread> dE = new ThreadEngine<DownloadThread>(DownloadThread.class, 1, "Downloads/");

        g1.setPriorityLevel(-2);
        g3.setPriorityLevel(2);
        g5.setPriorityLevel(3);

        dE.addToQueue(g1);
        dE.addToQueue(g2);
        dE.addToQueue(g3);
        dE.addToQueue(g4);
        dE.addToQueue(g5);


        dE.startAll();

        Thread.sleep(4000);

        double p = (double)dE.GetProgress().values().toArray()[0];

        if (p > 0) {
            if (verbose)
                System.out.println("addToQueue():\t\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("addToQueue():\t\t\tFAILED");
        }

        if (dE.GetProgress(g5) > 0 && dE.GetProgress(g1) == -1 && p > 0) {
            if (verbose)
                System.out.println("GetProgress():\t\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("GetProgress():\t\t\tFAILED");
        }

        if (dE.GetProgress(g5) > 0) {
            if (verbose)
                System.out.println("PriorityQueue:\t\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("PriorityQueue:\t\t\tFAILED");
        }

        if (dE.GetProgress().size() == 1) {
            if (verbose)
                System.out.println("CONCURRENT_LIMIT:\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("CONCURRENT_LIMIT:\t\tFAILED");
        }

        dE.setConcurrentLimit(2);
        Thread.sleep(4000);
        HashMap<Game, Double> prog = dE.GetProgress();

        if (prog.size() == 2 && prog.get(g5) > 0 && prog.get(g3) > 0) {
            if (verbose)
                System.out.println("setConcurrentLimit():\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("setConcurrentLimit():\t\tFAILED");
        }

        
        dE.pause(g5);
        Thread.sleep(1000);
        p = dE.GetProgress(g5);
        double p2 = dE.GetProgress(g3);
        Thread.sleep(1000);

        if (dE.GetProgress(g5) == p && dE.GetProgress(g3) > p2) {
            if (verbose)
                System.out.println("pause():\t\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("pause():\t\t\tFAILED");
        }

        dE.start(g5);
        Thread.sleep(2000);

        if (dE.GetProgress(g5) > p) {
            if (verbose)
                System.out.println("start():\t\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("start():\t\t\tFAILED");
        }

        dE.pauseAll();
        Thread.sleep(1000);
        prog = dE.GetProgress();
        Thread.sleep(1000);
        

        if (prog.equals(dE.GetProgress())) {
            if (verbose)
                System.out.println("pauseAll():\t\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("pauseAll():\t\t\tFAILED");
        }

        dE.startAll();
        Thread.sleep(2000);

        if (!dE.GetProgress().equals(prog)) {
            if (verbose)
                System.out.println("startAll():\t\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("startAll():\t\t\tFAILED");
        }

        if (dE.GetProgress(g5) > prog.get(g5) && dE.GetProgress(g3) > prog.get(g3)) {
            if (verbose)
                System.out.println("Multithreading:\t\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("Multithreading:\t\t\tFAILED");
        }

        dE.setPriorityLevel(g1, 10);
        dE.removeFromQueue(g5);
        dE.addToQueue(g5);
        Thread.sleep(4000);

        if (dE.GetProgress(g1) > 0 && dE.GetProgress(g5) == -1) {
            if (verbose)
                System.out.println("removeFromQueue():\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("removeFromQueue():\t\tFAILED");
        }

        if (verbose)
            System.out.println("Waiting for a game to finish downloading, please hold...");
        dE.join();
        Thread.sleep(2000);

        if (dE.peek() != null) {
            if (verbose)
                System.out.println("peek():\t\t\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("peek():\t\t\t\tFAILED");
        }

        Game tempG = dE.poll();

        if (dE.peek() == null && tempG != null) {
            if (verbose)
                System.out.println("poll():\t\t\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("poll():\t\t\t\tFAILED");
        }

        if (dE.GetProgress(g5) > 0 && tempG != null) {
            if (verbose)
                System.out.println("join():\t\t\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("join():\t\t\t\tFAILED");
        }

        dE.stopAll();
        Thread.sleep(1000);

        if(dE.GetProgress().isEmpty()) {
            if (verbose)
                System.out.println("stopAll():\t\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("stopAll():\t\t\tFAILED");
        }

        dE.setPriorityLevel(g4, -1);

        if(g4.getPriorityLevel() == -1) {
            if (verbose)
                System.out.println("setPriorityLevel():\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("setPriorityLevel():\t\tFAILED");
        }

        dE.clearAll();
        Thread.sleep(2000);

        if (dE.GetProgress().size() == 0) {
            if (verbose)
                System.out.println("clearAll():\t\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("clearAll():\t\t\tFAILED");
        }

        g1.deleteAllLocalFiles();
        dE.addToQueue(g1);
        ThreadEngine<ExtractionThread> eT = new ThreadEngine<ExtractionThread>(ExtractionThread.class, "Extracts/");
        dE.linkEngine(eT);
        dE.startAll();
        Thread.sleep(2000);
        dE.join();
        Thread.sleep(2000);

        if (eT.GetProgress(g1) > 0 && g1.getDecompressedPath().exists() && dE.peek() == null) {
            if (verbose)
                System.out.println("linkEngine():\t\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("linkEngine():\t\t\tFAILED");
        }

        dE.clearAll();
        eT.clearAll();
        Thread.sleep(2000);

        g1.deleteAllLocalFiles();
        g2.deleteAllLocalFiles();
        g3.deleteAllLocalFiles();
        g4.deleteAllLocalFiles();
        g5.deleteAllLocalFiles();
        System.out.println("ThreadEngine pass count: " + passCount + " out of " + totalPasses);
    }
}
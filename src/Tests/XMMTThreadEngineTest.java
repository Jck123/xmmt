package Tests;
import XMMT.Game;
import XMMT.DownloadThread;
import XMMT.ThreadEngine;

import java.util.HashMap;

public class XMMTThreadEngineTest {
    public static void main(String[] args) throws InterruptedException{
        int passCount = 0;
        
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
            System.out.println("addToQueue():\t\tPASSED");
            passCount++;
        } else {
            System.out.println("addToQueue():\t\tFAILED");
        }

        if (dE.GetProgress(g5) > 0 && dE.GetProgress(g1) == -1 && p > 0) {
            System.out.println("GetProgress():\t\tPASSED");
            passCount++;
        } else {
            System.out.println("GetProgress():\t\tFAILED");
        }

        if (dE.GetProgress(g5) > 0) {
            System.out.println("PriorityQueue:\t\tPASSED");
            passCount++;
        } else {
            System.out.println("PriorityQueue:\t\tFAILED");
        }

        if (dE.GetProgress().size() == 1) {
            System.out.println("DOWNLOAD_LIMIT:\t\tPASSED");
            passCount++;
        } else {
            System.out.println("DOWNLOAD_LIMIT:\t\tFAILED");
        }

        dE.setDownloadLimit(2);
        Thread.sleep(4000);
        HashMap<Game, Double> prog = dE.GetProgress();

        if (prog.size() == 2 && prog.get(g5) > 0 && prog.get(g3) > 0) {
            System.out.println("setDownloadLimit():\tPASSED");
            passCount++;
        } else {
            System.out.println("setDownloadLimit():\tFAILED");
        }

        
        dE.pause(g5);
        Thread.sleep(1000);
        p = dE.GetProgress(g5);
        double p2 = dE.GetProgress(g3);
        Thread.sleep(1000);

        if (dE.GetProgress(g5) == p && dE.GetProgress(g3) > p2) {
            System.out.println("pause():\t\tPASSED");
            passCount++;
        } else {
            System.out.println("pause():\t\tFAILED");
        }

        dE.start(g5);
        Thread.sleep(2000);

        if (dE.GetProgress(g5) > p) {
            System.out.println("start():\t\tPASSED");
            passCount++;
        } else {
            System.out.println("start():\t\tFAILED");
        }

        dE.pauseAll();
        Thread.sleep(1000);
        prog = dE.GetProgress();
        Thread.sleep(1000);
        

        if (prog.equals(dE.GetProgress())) {
            System.out.println("pauseAll():\t\tPASSED");
            passCount++;
        } else {
            System.out.println("pauseAll():\t\tFAILED");
        }

        dE.startAll();
        Thread.sleep(2000);

        if (!dE.GetProgress().equals(prog)) {
            System.out.println("startAll():\t\tPASSED");
            passCount++;
        } else {
            System.out.println("startAll():\t\tFAILED");
        }

        if (dE.GetProgress(g5) > prog.get(g5) && dE.GetProgress(g3) > prog.get(g3)) {
            System.out.println("Multithreading:\t\tPASSED");
            passCount++;
        } else {
            System.out.println("Multithreading:\t\tFAILED");
        }

        dE.setPriorityLevel(g1, 10);
        dE.removeFromQueue(g5);
        dE.addToQueue(g5);
        Thread.sleep(2000);

        if (dE.GetProgress(g1) > 0 && dE.GetProgress(g5) == -1) {
            System.out.println("removeFromQueue():\tPASSED");
            passCount++;
        } else {
            System.out.println("removeFromQueue():\tFAILED");
        }

        System.out.println("Waiting for a game to finish downloading, please hold...");
        dE.join();
        Thread.sleep(2000);

        if (dE.peek() != null) {
            System.out.println("peek():\t\t\tPASSED");
            passCount++;
        } else {
            System.out.println("peek():\t\t\tFAILED");
        }

        Game tempG = dE.poll();

        if (dE.peek() == null && tempG != null) {
            System.out.println("poll():\t\t\tPASSED");
            passCount++;
        } else {
            System.out.println("poll():\t\t\tFAILED");
        }

        if (dE.GetProgress(g5) > 0 && tempG != null) {
            System.out.println("join():\t\t\tPASSED");
            passCount++;
        } else {
            System.out.println("join():\t\t\tFAILED");
        }

        dE.stopAll();
        Thread.sleep(1000);

        if(dE.GetProgress().isEmpty()) {
            System.out.println("stopAll():\t\tPASSED");
            passCount++;
        } else {
            System.out.println("stopAll():\t\tFAILED");
        }

        dE.setPriorityLevel(g4, -1);

        if(g4.getPriorityLevel() == -1) {
            System.out.println("setPriorityLevel():\tPASSED");
            passCount++;
        } else {
            System.out.println("setPriorityLevel():\tFAILED");
        }

        dE.clearAll();
        Thread.sleep(2000);

        if (dE.GetProgress().size() == 0) {
            System.out.println("clearAll():\t\tPASSED");
            passCount++;
        } else {
            System.out.println("clearAll():\t\tFAILED");
        }

        g1.deleteAllLocalFiles();
        g2.deleteAllLocalFiles();
        g3.deleteAllLocalFiles();
        g4.deleteAllLocalFiles();
        g5.deleteAllLocalFiles();
        System.out.println("Total pass count: " + passCount + " out of 17");
    }
}
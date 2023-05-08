package Tests;
import XMMT.Game;

import java.io.File;
import java.net.URL;



public class XMMTGameTest {
    private static int passCount = 0;
    public static int totalPasses = 14;
    private static boolean verbose = false;

    public static int runTests(boolean v) {
        verbose = v;
        main(null);
        return passCount;
    }
    public static void main(String args[]) {
        Game g1 = new Game("Game 1", "https://archive.org/download/xbox_eng_romset/AMF%20Bowling%202004%20%5B%21%5D.7z");
        Game g2 = new Game("https://archive.org/download/xbox_eng_romset/Advent%20Rising%20%5B%21%5D.7z");
        Game g3 = new Game("Game 3", "https://archive.org/download/xbox_eng_romset/Advent%20Rising%20%5B%21%5D.7z", 2);

        if (g1 != null && g2 != null && g3 != null && new Game() != null) {
            if (verbose)
                System.out.println("Object initialization:\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("Object initialization:\t\tFAILED");
        }

        if (g1.getName().equals("Game 1") && g2.getName().equals("Advent Rising [!]") && g3.getName().equals("Game 3")) {
            if (verbose)
                System.out.println("getName():\t\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("getName():\t\t\tFAILED");
        }

        g3.setName("Cheese Game");

        if (g3.getName().equals("Cheese Game")) {
            if (verbose)
                System.out.println("setName():\t\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("setName():\t\t\tFAILED");
        }

        if(g2.equals(g3)) {
            if (verbose)
                System.out.println("equals():\t\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("equals():\t\t\tFAILED");
        }

        if (g1.getURL().toString().equals("https://archive.org/download/xbox_eng_romset/AMF%20Bowling%202004%20%5B%21%5D.7z") &&
            g2.getURL().toString().equals("https://archive.org/download/xbox_eng_romset/Advent%20Rising%20%5B%21%5D.7z") &&
            g3.getURL().toString().equals("https://archive.org/download/xbox_eng_romset/Advent%20Rising%20%5B%21%5D.7z")) {
                if (verbose)
                    System.out.println("getURL():\t\t\tPASSED");
                passCount++;
        }
        else {
            if (verbose)
                System.out.println("getURL():\t\t\tFAILED");
        }

        URL testURL = null;

        try {
            testURL = new URL("https://archive.org/download/xbox_eng_romset/Aeon%20Flux%20%5B%21%5D.7z");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        g1.setURL("https://archive.org/download/xbox_eng_romset/Aeon%20Flux%20%5B%21%5D.7z");
        g3.setURL(testURL);

        if (g1.getURL().toString().equals("https://archive.org/download/xbox_eng_romset/Aeon%20Flux%20%5B%21%5D.7z") &&
            g3.getURL().toString().equals("https://archive.org/download/xbox_eng_romset/Aeon%20Flux%20%5B%21%5D.7z") &&
            g1.equals(g3)) {
                if (verbose)
                    System.out.println("setURL():\t\t\tPASSED");
                passCount++;
        } else {
            if (verbose)
                System.out.println("setURL():\t\t\tFAILED");
        }

        if (g1.getPriorityLevel() == 0 && g2.getPriorityLevel() == 0 && g3.getPriorityLevel() == 2) {
            if (verbose)
                System.out.println("getPriorityLevel():\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("getPriorityLevel():\t\tFAILED");
        }

        g1.setPriorityLevel(-2);
        g2.setPriorityLevel(1);

        if (g1.getPriorityLevel() == -2 && g2.getPriorityLevel() == 1 && g3.getPriorityLevel() == 2) {
            if (verbose)
                System.out.println("setPriorityLevel():\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("setPriorityLevel():\t\tFAILED");
        }

        File testFile = new File("testFile.7z");
        try {
            testFile.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

        g1.setCompressedPath(testFile);
        g2.setCompressedPath("testFile.7z");

        if (g1.getCompressedPath().toString().equals(testFile.toString()) && 
            g2.getCompressedPath().toString().equals(testFile.toString())) {
                if (verbose)
                    System.out.println("get/setCompressedPath():\tPASSED");
                passCount++;
        } else {
            if (verbose)
                System.out.println("get/setCompressedPath():\tFAILED");
        }

        try {
            g1.setCompressedPath("Downloads/");
            if (verbose)
                System.out.println("setCompressedPath() exception:\tFAILED");
        } catch (IllegalArgumentException e) {
            if (verbose)
                System.out.println("setCompressedPath() exception:\tPASSED");
            passCount++;
        }

        testFile.delete();

        testFile = new File("test/game.txt");
        try {
            testFile.mkdirs();
            testFile.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

        g1.setDecompressedPath(testFile.getParentFile());
        g2.setDecompressedPath("test");

        if (g1.getDecompressedPath().toString().equals("test") &&
            g2.getDecompressedPath().toString().equals("test")) {
                if (verbose)
                    System.out.println("get/setDecompressedPath():\tPASSED");
                passCount++;
        } else {
            if (verbose)
                System.out.println("get/setDecompressedPath():\tFAILED");
        }

        testFile.delete();
        new File("test").delete();

        testFile = new File("testFile.7z");
        try {
            testFile.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        g1.setCompressedPath(testFile);

        g1.deleteCompressedFile();

        if (!testFile.exists()) {
            if (verbose)
                System.out.println("deleteCompressedFile():\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("deleteCompressedFile():\t\tFAILED");
            testFile.delete();
        }
        
        testFile = new File("test/test1/game.txt");
        try {
            testFile.getParentFile().mkdirs();
            testFile.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

        g1.setDecompressedPath(testFile.getParentFile());

        g1.deleteDecompressedFiles();
        
        if (!(testFile.exists() || testFile.getParentFile().exists())) {
            if (verbose)
                System.out.println("deleteDecompressedFiles():\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("deleteDecompressedFiles():\tFAILED");
            testFile.delete();
            testFile.getParentFile().delete();
        }

        testFile = new File("testFile.7z");
        try {
            testFile.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        g1.setCompressedPath(testFile);

        File testFile1 = new File("test/game.txt");
        try {
            testFile1.getParentFile().mkdir();
            testFile1.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        g1.setDecompressedPath(testFile1.getParentFile());
        
        g1.deleteAllLocalFiles();

        if (!(testFile.exists() || testFile1.getParentFile().exists() || testFile1.exists())) {
            if (verbose)
                System.out.println("deleteAllLocalFiles():\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("deleteAllLocalFiles():\t\tFAILED");
            testFile.delete();
            testFile1.delete();
            testFile1.getParentFile().delete();
        }

        System.out.println("Game pass count: " + passCount + " out of " + totalPasses);
    }
}

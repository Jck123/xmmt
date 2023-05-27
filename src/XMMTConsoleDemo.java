import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

import XMMT.ThreadEngine;
import XMMT.DownloadThread;
import XMMT.ExtractionThread;
import XMMT.FTPThread;
import XMMT.Game;

public class XMMTConsoleDemo {
    public static void main(String[] args) throws InterruptedException {
        Scanner input = new Scanner(System.in);
        
        String xbox_ip;
        String xbox_user;
        String xbox_pass;
        String xbox_dir;

        String tempDir;

        int maxProc;

        System.out.println("Welcome to the XMMT Demo!\nPlease enter the following information to get started!");
        System.out.print("XBOX IP: ");
        xbox_ip = input.nextLine();
        System.out.print("XBOX FTP Username: ");
        xbox_user = input.nextLine();
        System.out.print("XBOX FTP password: ");
        xbox_pass = input.nextLine();
        System.out.print("XBOX FTP Destination Directory: ");
        xbox_dir = input.nextLine();

        System.out.print("Local Temporary Directory: ");
        tempDir = input.nextLine();

        System.out.print("Maximum simultaneous processes: ");
        maxProc = input.nextInt();

        File tempFile = new File(tempDir + "/XMMT/");
        tempFile.mkdir();

        ThreadEngine<DownloadThread> downloadEngine = new ThreadEngine<DownloadThread>(DownloadThread.class, maxProc, tempFile.getPath() + "/Downloads/");
        ThreadEngine<ExtractionThread> extractionEngine = new ThreadEngine<ExtractionThread>(ExtractionThread.class, maxProc, tempFile.getPath() + "/Extracts/");
        ThreadEngine<FTPThread> FTPEngine = new ThreadEngine<FTPThread>(FTPThread.class, maxProc, xbox_ip, xbox_user, xbox_pass, xbox_dir);

        downloadEngine.linkEngine(extractionEngine);
        extractionEngine.linkEngine(FTPEngine);

        int gameSelect = 0;
        HashSet<Integer> gameSet = new HashSet<Integer>();

        String[] gameLinks = {  "https://archive.org/download/xbox_eng_romset/AMF%20Bowling%202004%20%5B%21%5D.7z", 
                                "https://archive.org/download/xbox_eng_romset/Advent%20Rising%20%5B%21%5D.7z",
                                "https://archive.org/download/xbox_eng_romset/Aeon%20Flux%20%5B%21%5D.7z",
                                "https://archive.org/download/xbox_eng_romset/Aggressive%20Inline%20%5B%21%5D.7z",
                                "https://archive.org/download/xbox_eng_romset/Alias%20%5B%21%5D.7z"};

        System.out.println("Please select the games you wish to install by typing in their value, then enter '6' to initiate the process");

        do {
            System.out.println("[1] AMF Bowling 2004\n[2] Advent Rising\n[3] Aeon Flux\n[4] Aggressive Inline\n[5] Alias");
            System.out.print("Enter which game would you like to add to the queue(6 to initiate): ");
            gameSelect = input.nextInt();
            if (gameSelect < 6 && gameSelect > 0)
                gameSet.add(gameSelect);
        } while (gameSelect != 6);

        ArrayList<Game> gameList = new ArrayList<Game>();
        for (int gameNum : gameSet) {
            Game g = new Game(gameLinks[gameNum - 1]);
            gameList.add(g);
            downloadEngine.addToQueue(g);
        }
        downloadEngine.startAll();
        Thread.sleep(5000);

        while(!downloadEngine.GetProgress().isEmpty() || !extractionEngine.GetProgress().isEmpty() || !FTPEngine.GetProgress().isEmpty()) {
            System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
            System.out.println("Download Progress:");
            downloadEngine.GetProgress().forEach((g, d) -> {
                System.out.println(g.getName() + ":\t\t\t" + d);
            });
            System.out.println("Extraction Progress:");
            extractionEngine.GetProgress().forEach((g, d) -> {
                System.out.println(g.getName() + ":\t\t\t" + d);
            });
            System.out.println("FTP Progress:");
            FTPEngine.GetProgress().forEach((g, d) -> {
                System.out.println(g.getName() + ":\t\t\t" + d);
            });
            Thread.sleep(1000);
        }
        System.out.println("All games have been successfully installed!");

        for(Game g : gameList)
            g.deleteAllLocalFiles();

        File f = new File(tempFile.getPath() + "/Downloads");
        f.delete();
        f = new File(tempFile.getPath() + "/Extracts");
        f.delete();
        tempFile.delete();

        input.close();
    }
}
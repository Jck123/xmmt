package Tests;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import XMMT.Game;
import XMMT.DownloadThread;
import XMMT.ExtractionThread;
import XMMT.FTPThread;
import XMMT.ThreadEngine;

public class XMMTFTPThreadTest {
    private static int passCount = 0;
    public static int totalPasses = 7;
    private static boolean verbose = false;

    public static int runTests(boolean v) throws InterruptedException, IOException {
        verbose = v;
        main(null);
        return passCount;
    }
    public static void main(String[] args) throws InterruptedException, IOException {
        final String XBOX_IP = "10.42.0.2";
        final String XBOX_USER = "xbox";
        final String XBOX_PASS = "xbox";
        final String XBOX_DIR = "/F/Games/";
        
        FTPClient client = new FTPClient();
        
        try {
            client.connect(XBOX_IP);
            client.login(XBOX_USER, XBOX_PASS);
            client.changeWorkingDirectory(XBOX_DIR);
        } catch (Exception e) {
            System.out.println("Failed to connect to FTP Client");
            System.out.println("Please make sure the connection details are accurate");
            return;
        }

        Game g1 = new Game("https://archive.org/download/xbox_eng_romset/AMF%20Bowling%202004%20%5B%21%5D.7z");
        ThreadEngine<FTPThread> fe = new ThreadEngine<FTPThread>(FTPThread.class);
        DownloadThread dT = new DownloadThread(g1, fe, "Downloads/");
        ExtractionThread eT = new ExtractionThread(g1, fe, "Extracts/");
        FTPThread fT = new FTPThread(g1, fe, XBOX_IP, XBOX_USER, XBOX_PASS, XBOX_DIR);
        FTPThread fT2 = new FTPThread(g1, fe, XBOX_IP, XBOX_USER, XBOX_PASS);

        if (verbose)
                System.out.println("Downloading and extracting game to test with, please hold...");

        dT.start();
        dT.join();

        eT.start();
        eT.join();

        if (fT != null && fT2 != null) {
            if (verbose)
                System.out.println("Object initialization:\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("Object initialization:\t\tFAILED");
        }

        if (fT.GetProgess() == 0) {
            if (verbose)
                System.out.println("GetProgress():\t\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("GetProgress():\t\t\tFAILED");
        }

        if (fT.getGame() == g1) {
            if (verbose)
                System.out.println("getGame():\t\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("getGame():\t\t\tFAILED");
        }

        fT.start();
        Thread.sleep(5000);

        if (fT.GetProgess() > 0 && client.changeWorkingDirectory(XBOX_DIR + g1.getName())) {
            if (verbose)
                System.out.println("start():\t\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("start():\t\t\tFAILED");
        }
        
        client.changeWorkingDirectory(XBOX_DIR);

        fT.pauseThread();
        Thread.sleep(2000);
        double fTp = fT.GetProgess();
        Thread.sleep(2000);

        if (fT.GetProgess() == fTp && fT.paused()) {
            if (verbose)
                System.out.println("pauseThread:\t\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("pauseThread():\t\t\tFAILED");
        }

        fT.resumeThread();
        Thread.sleep(2000);
        if (fT.GetProgess() > fTp && !fT.paused()) {
            if (verbose)
                System.out.println("resumeThread():\t\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("resumeThread():\t\t\tFAILED");
        }

        fT.interrupt();
        if (fT.isAlive())
            fT.join();
        
        if (!fT.isAlive()) {
            if (verbose)
                System.out.println("interrupt():\t\t\tPASSED");
            passCount++;
        } else {
            if (verbose)
                System.out.println("interrupt():\t\t\tFAILED");
        }

        fT.interrupt();
        g1.deleteAllLocalFiles();

        client.changeWorkingDirectory(XBOX_DIR);
        FTPFile deleteThisFile = null;
        for (FTPFile f : client.listFiles())
            if (f.getName().equals(g1.getName()))
                deleteThisFile = f;
        
        client.changeWorkingDirectory(XBOX_DIR + deleteThisFile.getName());
        deleteFTPFiles(client, deleteThisFile);

        client.logout();
        client.disconnect();

        System.out.println("FTPThread pass count: " + passCount + " out of " + totalPasses);
    }

    private static void deleteFTPFiles(FTPClient client, FTPFile dir) throws IOException {
        if (dir != null && !dir.getName().equals("..") && !dir.getName().equals(".")) {
            
            if (dir.isDirectory()) {
                client.changeWorkingDirectory(client.printWorkingDirectory() + "/" + dir.getName());
                for (FTPFile tempF : client.listFiles())
                    deleteFTPFiles(client, tempF);
                String curDir = client.printWorkingDirectory();
                client.changeWorkingDirectory(curDir.substring(0, curDir.lastIndexOf("/")));
                client.removeDirectory(client.printWorkingDirectory() + "/" + dir.getName());
            } else
                client.deleteFile(dir.getName());
        }
    }
}

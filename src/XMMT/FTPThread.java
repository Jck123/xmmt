package XMMT;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class FTPThread extends XMMTThread {
    public FTPThread() {
        super();
    }

    public FTPThread(Game g, EngineInterface e, String... a) {
        super(g, e, a);
        //Must have 3 or 4 arguments, the destination IP, username, and password
        if (a.length < 3 || a.length > 4)
            throw new IllegalArgumentException();
    }

    public void run() {
        //Assigns arguments as necessary
        String XBOX_IP = args[0];
        String username = args[1];
        String password = args[2];
        String destPath;

        //Defaults to '/C/' if directory is not assigned
        if (args.length == 4)
            destPath = args[3];
        else
            destPath = "/C/";

        FTPClient client = new FTPClient();
        String dir  = game.getDecompressedPath().toString();

        try {
            //Obtains full list of files needing to be transferred to FTP server
            Stream<Path> pathstream = Files.walk(Paths.get(dir));
            List<Path> pathList = pathstream.collect(Collectors.toList());
            pathstream.close();

            Double filesTransferred = 0.0;
            Double fileCount = (double)pathList.size();

            //Connects to FTP server
            client.connect(XBOX_IP);
            client.login(username, password);

            for(Path p : pathList) {
                File currentFile = p.toFile();
                if (currentFile.isDirectory())      //Skips if directory
                    continue;

                FileInputStream in = null;
                try {
                    boolean matchFound = false;

                    if (client.changeWorkingDirectory(destPath + p.normalize().getParent().toString()) /*&& ftpF.getSize() == currentFile.length()*/) {
                        for(FTPFile ftpF : client.listFiles()) {                //Checks if there is already a file on the server
                            if (ftpF.getName().equals(p.getFileName().toString()) ) {
                                matchFound = true;
                                break;
                            }
                        }
                    }

                    if (!matchFound) {          //File is skipped if a match is found
                        String filename = p.normalize().toString();
                        in = new FileInputStream(filename);
                        filename = destPath + filename.substring(filename.indexOf(game.getName())); //Sets up directory of where the file will go

                        client.setFileType(FTP.BINARY_FILE_TYPE);       //Sets file type(VERY IMPORTANT)
                        client.storeFile(filename, in);                 //Sends file over
                    }
                    filesTransferred++;
                    progress = (filesTransferred / fileCount) * 100;
                    while(paused)                                       //Runs if user requests thread to pause
                        Thread.sleep(1000);
                    if (isInterrupted()) break;                         //Runs if user requests thread to stop
                } catch (IOException e) {
                    e.printStackTrace();
                    sendFailFlag();
                } finally {
                    if (in != null)
                        in.close();
                }
            }
            client.logout();
            if (!isInterrupted())                                       //If all runs smoothly, informs engine the transfer was completed
                sendCompleteFlag();
            else
                sendFailFlag();                                         //Tells engine of a failure taking place
        } catch (Exception e) {
            e.printStackTrace();
            sendFailFlag();
        } finally {
            try {
                client.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

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
        if (a.length < 3 || a.length > 4)
            throw new IllegalArgumentException();
    }

    public void run() {
        String XBOX_IP = args[0];
        String username = args[1];
        String password = args[2];
        String destPath;

        if (args.length == 4) {
            destPath = args[3];
            File dirTester = new File(destPath);
            if (!dirTester.exists())
                dirTester.mkdirs();
        } else
            destPath = "/C/";

        FTPClient client = new FTPClient();
        String dir  = game.getDecompressedPath().toString();

        try {
            Stream<Path> pathstream = Files.walk(Paths.get(dir));
            List<Path> pathList = pathstream.collect(Collectors.toList());
            pathstream.close();

            Double filesTransferred = 0.0;
            Double fileCount = (double)pathList.size();

            client.connect(XBOX_IP);
            client.login(username, password);

            for(Path p : pathList) {
                File currentFile = p.toFile();
                if (currentFile.isDirectory())
                    continue;

                FileInputStream in = null;
                try {
                    boolean matchFound = false;

                    if (client.changeWorkingDirectory(destPath + p.normalize().getParent().toString()) /*&& ftpF.getSize() == currentFile.length()*/) {
                        for(FTPFile ftpF : client.listFiles()) {
                            if (ftpF.getName().equals(p.getFileName().toString()) ) {
                                matchFound = true;
                                break;
                            }
                        }
                    }

                    if (!matchFound) {
                        //System.out.println("Skipping file " + p);
                        //continue;
                    

                        //client.changeWorkingDirectory(destPath);
                        String filename = p.normalize().toString();
                        in = new FileInputStream(filename);
                        filename = destPath + filename.substring(filename.indexOf(game.getName()));
                        //System.out.println(filename);
            
                        //System.out.println("Transferring [" + currentFile.length() + "] " + in.ge + " --> " + client.printWorkingDirectory());

                        client.setFileType(FTP.BINARY_FILE_TYPE);
                        client.storeFile(filename, in);
                    }
                    filesTransferred++;
                    progress = (filesTransferred / fileCount) * 100;
                    while(paused)
                        Thread.sleep(1000);
                    if (isInterrupted()) break;
                } catch (IOException e) {
                    e.printStackTrace();
                    sendFailFlag();
                } finally {
                    if (in != null)
                        in.close();
                }
            }
            client.logout();
            if (!isInterrupted())
                sendCompleteFlag();
            else
                sendFailFlag();
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

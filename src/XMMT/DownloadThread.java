package XMMT;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class DownloadThread extends XMMTThread {
    public DownloadThread() {
        super();
    }

    public DownloadThread(Game g, EngineInterface e) {
        super(g, e);
        super.setArgs("./");
    }

    public DownloadThread(Game g, EngineInterface e, String... a) {
        super(g, e, a);
        //Checks to ensure there is only one arg
        if (args.length > 1)
            throw new IllegalArgumentException();
    }

    public void run() {
        //Creates destPath if one was not given(Chooses local root)
        String destPath;
        if (args.length == 0)
            destPath = "./";
        else 
            destPath = args[0];

        //Creates dir if it doesn't already exist
        File dirTester = new File(destPath);
        if (!dirTester.exists())
            dirTester.mkdirs();

        //Obtains game file name from source's URL
        String gameFileName = URLDecoder.decode(game.getURL().toString().substring(game.getURL().toString().lastIndexOf("/") + 1), StandardCharsets.UTF_8);
        try (BufferedInputStream in = new BufferedInputStream(game.getURL().openStream());
            FileOutputStream out = new FileOutputStream(destPath + gameFileName)) {
            //Sets up connection and file output
            game.setCompressedPath(destPath + gameFileName);
            byte dataBuffer[] = new byte[1024];
            HttpURLConnection httpConnection = (HttpURLConnection)game.getURL().openConnection();
            long totalFileSize = httpConnection.getContentLengthLong();
            int bytesRead;
            long downloaded = 0;
            //Where the magic really happens!
            //Loops through all incoming HTTP data and pastes it into local file at specified destination
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                out.write(dataBuffer, 0,  bytesRead);
                progress = ((double) ((((double)downloaded) / ((double)totalFileSize))) * 100);
                downloaded += bytesRead;
                while(paused) {     //If user requested to pause thread
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        game.deleteCompressedFile();
                        sendFailFlag();
                    }
                }
                if(isInterrupted()) break;      //If user wishes to stop the thread
            }
            in.close();
            out.close();
            if (!interrupted()) {               //If complete, informs associated engine
                sendCompleteFlag();
            } else {                            //If incomplete, deletes associated file and informs associated engine
                game.deleteCompressedFile();
                sendFailFlag();
            }
        } catch (IOException e) {
            game.deleteCompressedFile();
            sendFailFlag();
        }
    }
}

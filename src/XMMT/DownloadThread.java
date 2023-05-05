package XMMT;
import java.io.BufferedInputStream;
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
    }

    public DownloadThread(Game g, EngineInterface e, String p) {
        super(g, e, p);
    }

    public void run() {
        String gameFileName = URLDecoder.decode(game.getURL().toString().substring(game.getURL().toString().lastIndexOf("/") + 1), StandardCharsets.UTF_8);
        try (BufferedInputStream in = new BufferedInputStream(game.getURL().openStream());
            FileOutputStream out = new FileOutputStream(destPath + gameFileName)) {
            game.setCompressedPath(destPath + gameFileName);
            byte dataBuffer[] = new byte[1024];
            HttpURLConnection httpConnection = (HttpURLConnection)game.getURL().openConnection();
            long totalFileSize = httpConnection.getContentLengthLong();
            int bytesRead;
            long downloaded = 0;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                out.write(dataBuffer, 0,  bytesRead);
                progress = ((double) ((((double)downloaded) / ((double)totalFileSize))) * 100);
                downloaded += bytesRead;
                while(paused) {
                    try {
                        XMMTThread.sleep(1000);
                    } catch (InterruptedException e) {
                        //TODO: Figure out what happens if interrupted
                    }
                }
            }
            in.close();
            out.close();
            System.out.println(game.getName() + " has been downloaded!");
            sendCompleteFlag();
        } catch (IOException e) {
            e.printStackTrace();;
            //TODO: Process potential File IO errors
        }
    }
}

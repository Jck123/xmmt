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
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        game.deleteCompressedFile();
                        sendFailFlag();
                    }
                }
                if(isInterrupted()) break;
            }
            in.close();
            out.close();
            if (!interrupted()) {
                sendCompleteFlag();
            } else {
                game.deleteCompressedFile();
                sendFailFlag();
            }
        } catch (IOException e) {
            game.deleteCompressedFile();
            sendFailFlag();
        }
    }
}

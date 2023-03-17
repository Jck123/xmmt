import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.file.Paths;

public class XMMTDownloadThread extends Thread {
    private Double progress;
    private boolean paused;
    private XMMTGame game;

    public XMMTDownloadThread(XMMTGame inputGame) {
        game = inputGame;
        progress = 0.0;
    }

    public void run() {
        try (BufferedInputStream in = new BufferedInputStream(game.getURL().openStream());
            FileOutputStream out = new FileOutputStream(game.getName())) {
            game.setCompressedPath(Paths.get(game.getName()));
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
                        //TODO: Figure out what happens if interrupted
                    }
                }
            }
            in.close();
            out.close();
            System.out.println(game.getName() + " has been downloaded!");
        } catch (IOException e) {
            e.printStackTrace();;
            //TODO: Process potential File IO errors
        }
    }

    public double GetProgess() {
        return progress;
    }

    public void pauseThread() {
        paused = true;
    }

    public void resumeThread() {
        paused = false;
    }

    public boolean paused() {
        return paused;
    }

    public XMMTGame getGame() {
        return game;
    }
}

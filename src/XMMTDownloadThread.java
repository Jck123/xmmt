import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class XMMTDownloadThread extends Thread {
    private Thread t;
    private URL sourceURL;
    private String name;
    private Double progress;

    public XMMTDownloadThread(XMMTGame game) {
        sourceURL = game.getURL();
        name = game.getName();
    }

    public void run() {
        try (BufferedInputStream in = new BufferedInputStream(sourceURL.openStream());
            FileOutputStream out = new FileOutputStream(name)) {
            byte dataBuffer[] = new byte[1024];
            HttpURLConnection httpConnection = (HttpURLConnection)sourceURL.openConnection();
            long totalFileSize = httpConnection.getContentLengthLong();
            int bytesRead;
            long downloaded = 0;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                out.write(dataBuffer, 0,  bytesRead);
                progress = ((double) ((((double)downloaded) / ((double)totalFileSize))) * 100);
                downloaded += bytesRead;
            }
            in.close();
            out.close();
            System.out.println(name + " has been downloaded!");
        } catch (IOException e) {
            e.printStackTrace();;
            //TODO: Process potential File IO errors
        }
    }

    public void start() {
        if (t == null) {
            t = new Thread(this, name);
            t.start();
        }
    }

    public double GetProgess() {
        return progress;
    }
}

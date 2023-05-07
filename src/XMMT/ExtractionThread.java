package XMMT;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;

public class ExtractionThread extends XMMTThread {
    public ExtractionThread() {
        super();
    }

    public ExtractionThread(Game g, EngineInterface e) {
        super(g, e);
        super.setArgs("./");
    }

    public ExtractionThread(Game g, EngineInterface e, String... a) {
        super(g, e, a);
        if (args.length > 1)
            throw new IllegalArgumentException();
    }

    public void run() {
        try {
            String destPath;
            if (args.length == 0)
                destPath = "./";
            else 
                destPath = args[0];

            File dirTester = new File(destPath);
            if (!dirTester.exists())
                dirTester.mkdirs();

            SevenZFile sevenZFile = new SevenZFile(game.getCompressedPath());
            SevenZArchiveEntry entry;
            double totalEntries = (double) sevenZFile.getEntries().spliterator().getExactSizeIfKnown();
            double currentEntryCount = 0.0;
            game.setDecompressedPath(destPath + game.getName());
            game.getDecompressedPath().mkdir();
            while ((entry = sevenZFile.getNextEntry()) != null) {
                if (entry.isDirectory())
                    continue;
                File curfile = new File(game.getDecompressedPath() + "/" + entry.getName());
                File parent = curfile.getParentFile();
                if (!parent.exists())
                    parent.mkdirs();
                FileOutputStream out = new FileOutputStream(curfile);
                byte[] content = new byte[(int) entry.getSize()];
                sevenZFile.read(content, 0, content.length);
                out.write(content);
                out.close();
                currentEntryCount++;
                progress = (currentEntryCount / totalEntries) * 100;
                while(paused) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        game.deleteDecompressedFiles();
                        sendFailFlag();
                    }
                }
                if(isInterrupted()) break;
            }
            if (!isInterrupted())
                sendCompleteFlag();
            else {
                game.deleteDecompressedFiles();
                sendFailFlag();
            }
        } catch (IOException e) {
            game.deleteDecompressedFiles();
            sendFailFlag();
        }
    }
}

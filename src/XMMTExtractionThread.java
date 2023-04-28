import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;

public class XMMTExtractionThread extends XMMTThread {
    public XMMTExtractionThread() {
        super();
    }

    public XMMTExtractionThread(XMMTGame g, XMMTEngineInterface e) {
        super(g, e);
    }

    public XMMTExtractionThread(XMMTGame g, XMMTEngineInterface e, String p) {
        super(g, e, p);
    }

    public void run() {
        try {
            SevenZFile sevenZFile = new SevenZFile(game.getCompressedPath().toFile());
            SevenZArchiveEntry entry;
            double totalEntries = (double) sevenZFile.getEntries().spliterator().getExactSizeIfKnown();
            double currentEntryCount = 0.0;
            game.setDecompressedPath(Paths.get(destPath + game.getName()));
            while ((entry = sevenZFile.getNextEntry()) != null) {
                if (entry.isDirectory())
                    continue;
                File curfile = new File(destPath + entry.getName());
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
            }
            sendCompleteFlag();
        } catch (IOException e) {
            //TODO:Figure out what to do here
            e.printStackTrace();
        }
    }
}

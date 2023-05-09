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
        //Checks to ensure there is only one arg
        if (args.length > 1)
            throw new IllegalArgumentException();
    }

    public void run() {
        try {
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

            //Obtains the downloaded compressed game file
            SevenZFile sevenZFile = new SevenZFile(game.getCompressedPath());
            SevenZArchiveEntry entry;
            double totalEntries = (double) sevenZFile.getEntries().spliterator().getExactSizeIfKnown();
            double currentEntryCount = 0.0;
            //Creates destination directory
            game.setDecompressedPath(destPath + game.getName());
            game.getDecompressedPath().mkdir();
            while ((entry = sevenZFile.getNextEntry()) != null) {
                if (entry.isDirectory())        //Skips if current entry is directory
                    continue;
                File curfile = new File(game.getDecompressedPath() + "/" + entry.getName());    //Creates file in specified directory
                File parent = curfile.getParentFile();
                if (!parent.exists())                                                           //Creates directories leading up to file, if needed
                    parent.mkdirs();
                FileOutputStream out = new FileOutputStream(curfile);                           //Copies file content from 7z to uncompressed file
                byte[] content = new byte[(int) entry.getSize()];
                sevenZFile.read(content, 0, content.length);
                out.write(content);
                out.close();
                currentEntryCount++;
                progress = (currentEntryCount / totalEntries) * 100;
                while(paused) {                     //If user requested to pause thread
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        game.deleteDecompressedFiles();
                        sendFailFlag();
                    }
                }
                if(isInterrupted()) break;          //If user requested to stop thread
            }
            if (!isInterrupted())                   //Send flag to associated engine if properly completed
                sendCompleteFlag();
            else {
                game.deleteDecompressedFiles();     //Deletes all extracted files if failed and notifies associated engine
                sendFailFlag();
            }
        } catch (IOException e) {
            game.deleteDecompressedFiles();
            sendFailFlag();
        }
    }
}

package XMMT;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class Game {
    private String name;
    private URL sourceURL;
    private File compressedPath;
    private File decompressedPath;
    private int priorityLevel;

    public Game() {
        name = "";
        priorityLevel = 0;
    }

    public Game(String newName, String strURL) {
        name = newName;
        priorityLevel = 0;

        try {
            sourceURL = new URL(strURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public Game(String newName, String strURL, int priLvl) {
        name = newName;
        priorityLevel = priLvl;

        try {
            sourceURL = new URL(strURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public Game(String strURL) {
        priorityLevel = 0;

        try {
            sourceURL = new URL(strURL);        //Generates name of game based on name of input URL
            strURL = URLDecoder.decode(strURL, StandardCharsets.UTF_8);
            name = strURL.substring(strURL.lastIndexOf("/") + 1, strURL.lastIndexOf("."));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public String getName() {return name;}      //Name getter and setter
    public void setName(String newName) {name = newName;}

    public URL getURL() {return sourceURL;}     //URL getter and setters
    public void setURL(URL newURL) {sourceURL = newURL;}
    public void setURL(String newURL) {
        try {
            sourceURL = new URL(newURL);        //Verifies if URL is valid
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public File getCompressedPath() {return compressedPath;}    //CompressedPath getter and setters
    public void setCompressedPath(File newPath) {
        if (newPath.isDirectory())                              //Because the compressed file is expected to be a single file, input cannot be a directory
            throw new IllegalArgumentException("This path is a directory");
        compressedPath = newPath;
    }
    public void setCompressedPath(String newPath) {
        File newFile = new File(newPath);
        if (newFile.isDirectory())
            throw new IllegalArgumentException("This path is a directory");
        compressedPath = newFile;
    }

    public File getDecompressedPath() {return decompressedPath;}        //DecompressedPath getter and setters
    public void setDecompressedPath(File newPath) {
        decompressedPath = newPath;
    }
    public void setDecompressedPath(String newPath) {
        decompressedPath = new File(newPath);
    }
    
    public int getPriorityLevel() {return priorityLevel;}               //Priority level is a measure of how important the game is(typically set by user)
    public void setPriorityLevel(int newNum) {priorityLevel = newNum;}

    public void deleteCompressedFile() {            //Deletes compressed file if it exists
        if (compressedPath == null)
            return;
        compressedPath.delete();
        compressedPath = null;
    }

    public void deleteDecompressedFiles() {         //Recursively deletes decompressed files if they exist
        if (decompressedPath == null)
            return;
        deleteDirectory(decompressedPath);
        decompressedPath = null;
    }

    private void deleteDirectory(File dir) {
        if (dir != null) {
            if(dir.isDirectory())
                for (File tempF : dir.listFiles())
                    deleteDirectory(tempF);
            dir.delete();
        }
    }

    public void deleteAllLocalFiles() {             //Deletes all files(compressed and decompressed)
        deleteCompressedFile();
        deleteDecompressedFiles();
    }

    @Override                                       //Declares how two game objects will be compared
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Game temp = (Game) o;
        return temp.getURL().equals(this.getURL());
    }
}

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
            sourceURL = new URL(strURL);
            strURL = URLDecoder.decode(strURL, StandardCharsets.UTF_8);
            name = strURL.substring(strURL.lastIndexOf("/") + 1, strURL.lastIndexOf("."));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public String getName() {return name;}
    public void setName(String newName) {name = newName;}

    public URL getURL() {return sourceURL;}
    public void setURL(URL newURL) {sourceURL = newURL;}
    public void setURL(String newURL) {
        try {
            sourceURL = new URL(newURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public File getCompressedPath() {return compressedPath;}
    public void setCompressedPath(File newPath) {
        if (newPath.isDirectory())
            throw new IllegalArgumentException("This path is a directory");
        compressedPath = newPath;
    }
    public void setCompressedPath(String newPath) {
        File newFile = new File(newPath);
        if (newFile.isDirectory())
            throw new IllegalArgumentException("This path is a directory");
        compressedPath = newFile;
    }

    public File getDecompressedPath() {return decompressedPath;}
    public void setDecompressedPath(File newPath) {
        decompressedPath = newPath;
    }
    public void setDecompressedPath(String newPath) {
        decompressedPath = new File(newPath);
    }
    
    public int getPriorityLevel() {return priorityLevel;}
    public void setPriorityLevel(int newNum) {priorityLevel = newNum;}

    public void deleteCompressedFile() {
        if (compressedPath != null)
            compressedPath.delete();
    }

    public void deleteDecompressedFiles() {
        if (decompressedPath != null) {
            for (File tempF : decompressedPath.listFiles())
                if(!tempF.isDirectory())
                    tempF.delete();
            decompressedPath.delete();
        }

    }

    public void deleteAllLocalFiles() {
        deleteCompressedFile();
        deleteDecompressedFiles();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Game temp = (Game) o;
        return temp.getURL().equals(this.getURL());
    }
}

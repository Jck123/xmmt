import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class XMMTGame {
    private String name;
    private URL sourceURL;
    private Path compressedPath;
    private int priorityLevel;
    private int state;

    public XMMTGame() {
        name = "";
        priorityLevel = 0;
        state = 0;
    }

    public XMMTGame(String newName, String strURL) {
        name = newName;
        priorityLevel = 0;
        state = 0;

        try {
            sourceURL = new URL(strURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public XMMTGame(String newName, String strURL, int priLvl) {
        name = newName;
        priorityLevel = priLvl;
        state = 0;

        try {
            sourceURL = new URL(strURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public XMMTGame(String strURL) {
        priorityLevel = 0;
        state = 0;

        try {
            sourceURL = new URL(strURL);
            name = URLDecoder.decode(strURL, StandardCharsets.UTF_8).substring(strURL.lastIndexOf("/") + 1);
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

    public Path getCompressedPath() {return compressedPath;}
    public void setCompressedPath(Path newPath) {
        if (newPath.toFile().isDirectory())
            throw new IllegalArgumentException("This path is a directory");
        compressedPath = newPath;
    }
    
    public int getPriorityLevel() {return priorityLevel;}
    public void setPriorityLevel(int newNum) {priorityLevel = newNum;}

    public int getState() {return state;}
    public void setState(int newState) {state = newState;}

    public void deleteLocalFiles() {
        if (compressedPath != null)
            compressedPath.toFile().delete();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        XMMTGame temp = (XMMTGame) o;
        return temp.getURL() == this.getURL();
    }
}

package Tests;
import XMMT.Game;
import XMMT.DownloadThread;
import XMMT.ExtractionThread;
import XMMT.ThreadEngine;

public class XMMTThreadEngineTest {
    public static void main(String[] args) {
        int passCount = 0;
        
        Game g1 = new Game("https://archive.org/download/xbox_eng_romset/AMF%20Bowling%202004%20%5B%21%5D.7z");
        

        System.out.println("Total pass count: " + passCount + " out of 14");
    }
}

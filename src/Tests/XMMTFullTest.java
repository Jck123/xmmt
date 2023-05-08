package Tests;

import java.io.IOException;

public class XMMTFullTest {
    public static void main(String[] args) throws InterruptedException, IOException {
        XMMTGameTest.main(null);
        XMMTDownloadThreadTest.main(null);
        XMMTExtractionThreadTest.main(null);
        XMMTFTPThreadTest.main(null);
        XMMTThreadEngineTest.main(null);
    }
}

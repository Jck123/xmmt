package Tests;

import java.io.IOException;

public class XMMTFullTest {
    public static void main(String[] args) throws InterruptedException, IOException {
        boolean verbose = true;
        int passCount = 0;
        int totalPasses = XMMTGameTest.totalPasses + XMMTDownloadThreadTest.totalPasses + XMMTExtractionThreadTest.totalPasses + XMMTFTPThreadTest.totalPasses + XMMTThreadEngineTest.totalPasses;
        passCount += XMMTGameTest.runTests(verbose);
        passCount += XMMTDownloadThreadTest.runTests(verbose);
        passCount += XMMTExtractionThreadTest.runTests(verbose);
        passCount += XMMTFTPThreadTest.runTests(verbose);
        passCount += XMMTThreadEngineTest.runTests(verbose);

        System.out.println("Total pass count: " + passCount + " out of " + totalPasses);
    }
}

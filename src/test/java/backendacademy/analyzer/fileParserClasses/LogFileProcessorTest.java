package backendacademy.analyzer.fileParserClasses;

import org.junit.jupiter.api.Test;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import static org.junit.jupiter.api.Assertions.*;

class LogFileProcessorTest {
    @Test
    public void testGetLogRecords_URL() throws Exception {
        String path = "URL_copy.txt";
        assertEquals(51462, LogFileProcessor.getLogRecords(path).size());
    }

    @Test
    public void testGetLogRecords_LocalFile() throws Exception {
        String path = "log_1.txt";
        assertEquals(1, LogFileProcessor.getLogRecords(path).size());
    }

    @Test
    public void testGetLogRecords_InvalidPath() throws Exception {
        String path = "path";
        assertThrows(IOException.class, () -> LogFileProcessor.getLogRecords(path));
    }
}

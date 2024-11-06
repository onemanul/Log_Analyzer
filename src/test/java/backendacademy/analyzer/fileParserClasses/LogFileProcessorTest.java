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
        String path = "https://raw.githubusercontent.com/elastic/examples/master/Common%20Data%20Formats/nginx_logs/nginx_logs";
        URL url = new URL(path);
        URLConnection connection = url.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        assertEquals(LogParser.makeRecordList(reader), LogFileProcessor.getLogRecords(path));
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

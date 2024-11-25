package backendacademy.analyzer.fileParserClasses;

import backendacademy.analyzer.LogAnalyze;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Paths;
import static org.junit.jupiter.api.Assertions.*;

/*
root
├── LOG
│   ├── logs
│   │   └── logers_7.txt
│   ├── loges_3.txt
│   └── logs_5.txt
├── log_1.txt
└── URL_copy.txt
 */

class LogFileProcessorTest {
    LogAnalyze analyze;

    @Test
    public void testGetLogRecords_URL_LocalCopy() throws Exception {
        String path = "URL_copy.txt";
        analyze = LogFileProcessor.getLogRecords(path, null, null);
        assertEquals(51462, analyze.getRequestsCount());
    }

    @Test
    public void testGetLogRecords_InvalidPathURL() throws Exception {
        String path = "http://InvalidPathAbsolytnoTochno";
        assertThrows(IOException.class, () -> LogFileProcessor.getLogRecords(path, null, null));
    }

    @Test
    public void testGetLogRecords_LocalFile() throws Exception {
        String path = "log_1.txt";
        analyze = LogFileProcessor.getLogRecords(path, null, null);
        assertEquals(1, analyze.getRequestsCount());
    }

    @Test
    public void testGetLogRecords_LocalDirectory() throws Exception {
        String path = "LOG/*";                       // в папке два файла
        analyze = LogFileProcessor.getLogRecords(path, null, null);
        assertEquals(3+5, analyze.getRequestsCount());

        path = "LOG/logs/*";                         // в папке один файл
        analyze = LogFileProcessor.getLogRecords(path, null, null);
        assertEquals(7, analyze.getRequestsCount());

        path = "LOG/**";                            // два файла в этой папке, один во внутренней
        analyze = LogFileProcessor.getLogRecords(path, null, null);
        assertEquals(3+5+7, analyze.getRequestsCount());

        String wrongPath = "LOG/logs/loggers";      // папки не существует
        assertThrows(IOException.class, () -> LogFileProcessor.getLogRecords(wrongPath, null, null));
    }

    @Test
    public void testGetLogRecords_MaskOfFile() throws Exception {
        String path = "lo*";            // один файл подходит
        analyze = LogFileProcessor.getLogRecords(path, null, null);
        assertEquals(1, analyze.getRequestsCount());

        path = "*.txt";                 // два файла подходят
        analyze = LogFileProcessor.getLogRecords(path, null, null);
        assertEquals(51463, analyze.getRequestsCount());

        path = "LOG/*3.txt";            // один файл подходит
        analyze = LogFileProcessor.getLogRecords(path, null, null);
        assertEquals(3, analyze.getRequestsCount());

        path = "LOG/*.txt";                 // два файла подходят
        analyze = LogFileProcessor.getLogRecords(path, null, null);
        assertEquals(3+5, analyze.getRequestsCount());

        path = "LOG/log[ert]*";              // один файл подходит
        analyze = LogFileProcessor.getLogRecords(path, null, null);
        assertEquals(3, analyze.getRequestsCount());

        path = "LOG/log?s*";              // один файл подходит
        analyze = LogFileProcessor.getLogRecords(path, null, null);
        assertEquals(3, analyze.getRequestsCount());

        path = "LOG/log*s*";             // два файла подходят
        analyze = LogFileProcessor.getLogRecords(path, null, null);
        assertEquals(3+5, analyze.getRequestsCount());

        String wrongPath = "LOG/*.pdf";                     // файл не существует
        assertThrows(IOException.class, () -> LogFileProcessor.getLogRecords(wrongPath, null, null));
    }

    @Test
    public void testGetLogRecords_MaskOfDirectory() throws Exception {
        String path = "LOG/**/logers_7.txt";
        analyze = LogFileProcessor.getLogRecords(path, null, null);
        assertEquals(7, analyze.getRequestsCount());

        path = "**/logs_5.txt";
        analyze = LogFileProcessor.getLogRecords(path, null, null);
        assertEquals(5, analyze.getRequestsCount());

        String wrongPath = "LOG/**/no.txt";                     // файл не существует
        assertThrows(IOException.class, () -> LogFileProcessor.getLogRecords(wrongPath, null, null));
    }

    @Test
    public void testGetLogRecords_MaskOfFileAndDirectory() throws Exception {
        String path = "**/log[ert]*t";
        analyze = LogFileProcessor.getLogRecords(path, null, null);
        assertEquals(3+7, analyze.getRequestsCount());

        path = "**log*.txt";
        analyze = LogFileProcessor.getLogRecords(path, null, null);
        assertEquals(1+3+5+7, analyze.getRequestsCount());

        path = "LOG/**loge*.txt";
        analyze = LogFileProcessor.getLogRecords(path, null, null);
        assertEquals(3+7, analyze.getRequestsCount());

        String wrongPath = "LOG/**/no.txt";                     // файл не существует
        assertThrows(IOException.class, () -> LogFileProcessor.getLogRecords(wrongPath, null, null));
    }

    @Test
    public void testGetLogRecords_InvalidPathLocalFile() throws Exception {
        String path = "InvalidPathAbsolytnoTochno";
        assertThrows(IOException.class, () -> LogFileProcessor.getLogRecords(path, null, null));
    }
    @Test
    public void testGetRootDirectory_WithoutMask() {
        String path = "C:\\Users\\Anyone\\Desktop\\file.txt";
        String expected = "C:/Users/Anyone/Desktop/file.txt";
        assertEquals(expected, LogFileProcessor.getRootDirectory(path));

        path = "C:\\Users\\Anyone\\Desktop";
        expected = "C:/Users/Anyone/Desktop";
        assertEquals(expected, LogFileProcessor.getRootDirectory(path));

        path = "Desktop/Something/Else";
        assertEquals(path, LogFileProcessor.getRootDirectory(path));
    }

    @Test
    public void testGetRootDirectory_WithMask() {
        String path = "C:\\Users\\Anyone\\Desktop\\file*";
        String expected = "C:/Users/Anyone/Desktop";
        assertEquals(expected, LogFileProcessor.getRootDirectory(path));

        path = "C:\\Users\\Anyone\\Desktop\\file.t?t";
        assertEquals(expected, LogFileProcessor.getRootDirectory(path));

        path = "C:\\Users\\Anyone\\Desktop\\f[ioe]le.txt";
        assertEquals(expected, LogFileProcessor.getRootDirectory(path));

        path = "C:\\Users\\Anyone\\D?sktop\\image.png";
        expected = "C:/Users/Anyone";
        assertEquals(expected, LogFileProcessor.getRootDirectory(path));

        path = "C:\\Users\\Any[on]e\\De?ktop\\fi*.txt";
        expected = "C:/Users";
        assertEquals(expected, LogFileProcessor.getRootDirectory(path));
    }

    @Test
    public void testGetRootDirectory_EmptyPath() {
        String path = "";
        assertEquals(path, LogFileProcessor.getRootDirectory(path));
    }

    @Test
    public void testGetPattern_SameRoot() {
        String path = "C:\\Users\\Anyone\\Desktop\\file.txt";
        String root = "C:/Users/Anyone/Desktop/file.txt";
        assertEquals("**", LogFileProcessor.getPattern(path, root));

        path = "LOG/loges_3.txt";
        root = "LOG/loges_3.txt";
        assertEquals("**", LogFileProcessor.getPattern(path, root));
    }

    @Test
    public void testGetPattern_AbsolutePath() {
        String path = "C:\\Users\\Anyone\\Desktop\\file*";
        String root = "C:/Users/Anyone/Desktop";
        assertEquals("C:/Users/Anyone/Desktop/file*", LogFileProcessor.getPattern(path, root));

        path = "/Users/Anyone/Desktop/file*";
        assertEquals("/Users/Anyone/Desktop/file*", LogFileProcessor.getPattern(path, root));
    }

    @Test
    public void testGetPattern_RelativePath() {
        String path = "LOG/log*";
        String root = "LOG";
        String expected = Paths.get("").toAbsolutePath().toString().replace('\\', '/') + "/" + path;
        assertEquals(expected, LogFileProcessor.getPattern(path, root));
    }

    @Test
    public void testGetPattern_EmptyPath() {
        String path = "";
        String root = "root";
        String expected = Paths.get("").toAbsolutePath().toString().replace('\\', '/') + "/";
        assertEquals(expected, LogFileProcessor.getPattern(path, root));
    }
}

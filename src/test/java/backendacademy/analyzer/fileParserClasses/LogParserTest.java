package backendacademy.analyzer.fileParserClasses;

import backendacademy.analyzer.LogAnalyze;
import backendacademy.analyzer.LogRecord;
import org.junit.jupiter.api.Test;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class LogParserTest {
    LogAnalyze analyze = new LogAnalyze(null, null);

    @Test
    public void testParseLogLine_CorrectLogLine() {
        String logLine = "50.57.209.100 - - [03/Jun/2015:03:06:42 +0000] \"GET /downloads/product_2 HTTP/1.1\" " +
            "404 340 \"-\" \"Debian APT-HTTP/1.3 (0.9.7.9)\"";
        Optional<LogRecord> logRecord = LogParser.parseLogLine(logLine);

        assertTrue(logRecord.isPresent());
        assertEquals("50.57.209.100", logRecord.get().remoteAddr());
        assertEquals(LocalDate.of(2015, 6, 3), logRecord.get().date());
        assertEquals("/downloads/product_2", logRecord.get().requestPath());
        assertEquals(404, logRecord.get().status());
        assertEquals(340, logRecord.get().bodyBytesSent());
    }

    @Test
    public void testParseLogLine_IncorrectLogLine_1() {
        Optional<LogRecord> logRecord = LogParser.parseLogLine("Invalid log line format");
        assertFalse(logRecord.isPresent());
    }

    @Test
    public void testParseLogLine_IncorrectLogLine_2() {
        Optional<LogRecord> logRecord = LogParser.parseLogLine("50.57.209.100 - - [03/Jun/2015:03:06:42 +0000] ");
        assertFalse(logRecord.isPresent());
    }

    @Test
    public void testParseLogLine_IncorrectLogLine_NoBytesSent() {
        Optional<LogRecord> logRecord = LogParser.parseLogLine("50.57.209.100 - - [03/Jun/2015:03:06:42 +0000] " +
            "\"GET /downloads/product_2 HTTP/1.1\" 1234 \"-\" \"Debian APT-HTTP/1.3 (0.9.7.9)\"");
        assertFalse(logRecord.isPresent());
    }

    @Test
    public void testParseLogLine_IncorrectLogLine_NoStatus() {
        Optional<LogRecord> logRecord = LogParser.parseLogLine("Invalid log line format");
        assertFalse(logRecord.isPresent());
    }

    @Test
    public void testMakeRecordList_CorrectInput() throws IOException{
        String logData = "79.136.114.202 - - [03/Jun/2015:04:06:40 +0000] \"GET /downloads/product_1 HTTP/1.1\" 404 332 \"-\" \"Debian APT-HTTP/1.3 (1.0.1ubuntu2)\"\n" +
            "217.168.17.5 - - [03/Jun/2015:04:06:54 +0000] \"GET /downloads/product_2 HTTP/1.1\" 200 490 \"-\" \"Debian APT-HTTP/1.3 (0.8.10.3)\"";
        BufferedReader reader = new BufferedReader(new StringReader(logData));
        LogParser.addRecordsToAnalyze(reader, analyze);
        assertEquals(2, analyze.getRequestsCount());
        assertEquals(1, analyze.getRemoteAddrCount().get("79.136.114.202"));
        assertEquals(1, analyze.getRemoteAddrCount().get("217.168.17.5"));
    }

    @Test
    public void testMakeRecordList_IncorrectInput() throws IOException {
        String logData = "Invalid log line format\n" + "Another invalid log line";
        BufferedReader reader = new BufferedReader(new StringReader(logData));
        LogParser.addRecordsToAnalyze(reader, analyze);
        assertEquals(0, analyze.getRequestsCount());
    }

    @Test
    public void testMakeListOfRecords_MixedInput() throws IOException {
        String logData = "79.136.114.202 - user [01/Jan/2023:10:00:00 +0000] \"GET /index.html HTTP/1.1\" 200 1234 \"http://referer.com\" \"User -Agent\"\n" +
            "Invalid log line format\n" +
            "217.168.17.5 - user [01/Jan/2023:10:05:00 +0000] \"POST /submit HTTP/1.1\" 404 0 \"http://referer.com\" \"User -Agent\"";
        BufferedReader reader = new BufferedReader(new StringReader(logData));
        LogParser.addRecordsToAnalyze(reader, analyze);
        assertEquals(2, analyze.getRequestsCount());
        assertEquals(1, analyze.getRemoteAddrCount().get("79.136.114.202"));
        assertEquals(1, analyze.getRemoteAddrCount().get("217.168.17.5"));
    }

    @Test
    public void testMakeListOfRecords_Exception() throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(new String()));
        reader.close();
        assertThrows(IOException.class, () -> LogParser.addRecordsToAnalyze(reader, analyze));
    }
}

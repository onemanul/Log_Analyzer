package backendacademy.analyzer;

import backendacademy.analyzer.fileParserClasses.LogParser;
import backendacademy.analyzer.reportClasses.ReportFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LogAnalyzerTest {
    LogAnalyze analyze = new LogAnalyze(null, null);
    LogAnalyze analyzeWithDate = new LogAnalyze(LocalDate.of(2015,1,1),
        LocalDate.of(2018,1,1));
    List<LogRecord> records = new ArrayList<>();

    @BeforeEach
    void setRecords() {
        String[] logRecords = {"54.84.255.104 - - [20/May/2010:07:05:24 +0000] \"GET /downloads/product_1 HTTP/1.1\" 100 333 \"-\" \"Debian APT-HTTP/1.3 (1.0.1ubuntu2)\"\n",
            "172.29.141.101 - - [20/May/2012:07:05:54 +0000] \"GET /downloads/product_2 HTTP/1.1\" 404 444 \"-\" \"Debian APT-HTTP/1.3 (0.9.7.9)\"\n",
            "78.108.122.1 - - [20/May/2015:08:05:41 +0000] \"GET /downloads/product_2 HTTP/1.1\" 200 555 \"-\" \"Chef Knife/11.10.4 (ruby-1.9.3-p484; ohai-6.20.0; x86_64-linux; +http://opscode.com)\"\n",
            "184.73.132.8 - - [20/May/2017:08:05:30 +0000] \"GET /downloads/product_1 HTTP/1.1\" 300 666 \"-\" \"Chef Client/11.4.4 (ruby-1.9.3-p286; ohai-6.16.0; x86_64-linux; +http://opscode.com)\"\n",
            "148.251.2.47 - - [20/May/2019:08:05:15 +0000] \"GET /downloads/product_2 HTTP/1.1\" 200 777 \"-\" \"Wget/1.13.4 (linux-gnu)\""};
        for (String log : logRecords) {
            Optional<LogRecord> opt = LogParser.parseLogLine(log);
            opt.ifPresent(records::add);
        }
    }

    void analyzeRecords(LogAnalyze logAnalyze) {
        for (LogRecord log : records) {
            logAnalyze.addRecord(log);
        }
    }

    @Test
    public void testAddRecords_WithoutDate() {
        analyzeRecords(analyze);
        assertEquals(5, analyze.getRequestsCount());
    }

    @Test
    public void testAddRecords_WithDate() {
        analyzeRecords(analyzeWithDate);
        assertEquals(2, analyzeWithDate.getRequestsCount());
    }

    @Test
    public void testCheckDate_WithoutDate() {
        assertTrue(analyze.checkDate(records.get(0)));
        assertTrue(analyze.checkDate(records.get(1)));
        assertTrue(analyze.checkDate(records.get(2)));
        assertTrue(analyze.checkDate(records.get(3)));
        assertTrue(analyze.checkDate(records.get(4)));
    }

    @Test
    public void testCheckDate_WithDate() {
        assertFalse(analyzeWithDate.checkDate(records.get(0)));
        assertFalse(analyzeWithDate.checkDate(records.get(1)));
        assertTrue(analyzeWithDate.checkDate(records.get(2)));
        assertTrue(analyzeWithDate.checkDate(records.get(3)));
        assertFalse(analyzeWithDate.checkDate(records.get(4)));
    }

    @Test
    public void testFinishAnalyze_ValidAnalyze() {
        analyzeRecords(analyze);
        analyze.finishAnalyze();
        assertEquals(555, analyze.getAverageResponseSize());
        assertEquals(555, analyze.getPercentile50());
        assertEquals(777, analyze.getPercentile95());
    }

    @Test
    public void testFinishAnalyze_EmptyToAnalyze() {
        analyze.finishAnalyze();
        assertEquals(0, analyze.getAverageResponseSize());
        assertEquals(0, analyze.getPercentile50());
        assertEquals(0, analyze.getPercentile95());
    }

    @Test
    public void testGetPercentile_ValidPercentile() {
        analyzeRecords(analyze);
        assertEquals(555, analyze.getPercentile(0.5));
        assertEquals(333, analyze.getPercentile(0.2));
        assertEquals(333, analyze.getPercentile(0));
        assertEquals(777, analyze.getPercentile(1));
    }

    @Test
    public void testGetPercentile_OutOfBoundsPercentile() {
        analyzeRecords(analyze);
        assertEquals(0, analyze.getPercentile(-0.1));
        assertEquals(0, analyze.getPercentile(1.1));
    }

    @Test
    public void testGetPercentile_EmptyAnalyze() {
        assertEquals(0, analyze.getPercentile(0.5));
    }
}

package backendacademy.analyzer;

import backendacademy.analyzer.fileParserClasses.LogParser;
import backendacademy.analyzer.reportClasses.ReportFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.BufferedReader;
import java.io.StringReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class LogAnalyzerTest {
    LogAnalyzer analyzer = new LogAnalyzer();
    List<LogRecord> records;

    @BeforeEach
    void setUp() throws Exception {
        String logRecords = "54.84.255.104 - - [20/May/2010:07:05:24 +0000] \"GET /downloads/product_1 HTTP/1.1\" 100 333 \"-\" \"Debian APT-HTTP/1.3 (1.0.1ubuntu2)\"\n" +
            "172.29.141.101 - - [20/May/2012:07:05:54 +0000] \"GET /downloads/product_2 HTTP/1.1\" 404 444 \"-\" \"Debian APT-HTTP/1.3 (0.9.7.9)\"\n" +
            "78.108.122.1 - - [20/May/2015:08:05:41 +0000] \"GET /downloads/product_2 HTTP/1.1\" 200 555 \"-\" \"Chef Knife/11.10.4 (ruby-1.9.3-p484; ohai-6.20.0; x86_64-linux; +http://opscode.com)\"\n" +
            "184.73.132.8 - - [20/May/2017:08:05:30 +0000] \"GET /downloads/product_1 HTTP/1.1\" 300 666 \"-\" \"Chef Client/11.4.4 (ruby-1.9.3-p286; ohai-6.16.0; x86_64-linux; +http://opscode.com)\"\n" +
            "148.251.2.47 - - [20/May/2019:08:05:15 +0000] \"GET /downloads/product_2 HTTP/1.1\" 200 777 \"-\" \"Wget/1.13.4 (linux-gnu)\"";
        records = LogParser.makeRecordList(new BufferedReader(new StringReader(logRecords)));
    }

    @Test
    public void testFilterByDate_validRange() {
        List<LogRecord> filtered = analyzer.filterByDate(records, LocalDate.of(2015, 1, 1),
            LocalDate.of(2020, 1, 2));
        assertEquals(3, filtered.size());
        filtered = analyzer.filterByDate(records, LocalDate.of(2000, 1, 1),
            LocalDate.of(2010, 1, 2));
        assertEquals(0, filtered.size());
    }

    @Test
    public void testFilterByDate_NullFromDate() {
        List<LogRecord> filtered = analyzer.filterByDate(records, null, LocalDate.of(2015, 1, 1));
        assertEquals(2, filtered.size());
        filtered = analyzer.filterByDate(records, null, LocalDate.of(2010, 1, 1));
        assertEquals(0, filtered.size());
    }

    @Test
    public void testFilterByDate_NullToDate() {
        List<LogRecord> filtered = analyzer.filterByDate(records, LocalDate.of(2015, 1, 1), null);
        assertEquals(3, filtered.size());
        filtered = analyzer.filterByDate(records, LocalDate.of(2020, 1, 1), null);
        assertEquals(0, filtered.size());
    }

    @Test
    public void testFilterByDate_NoDate() {
        List<LogRecord> filtered = analyzer.filterByDate(records, null, null);
        assertEquals(records.size(), filtered.size());
    }

    @Test
    public void testFilterByDate_EmptyList() {
        List<LogRecord> logRecords = new ArrayList<>();
        List<LogRecord> filtered = analyzer.filterByDate(logRecords, null, null);
        assertEquals(0, filtered.size());
    }

    @Test
    public void testAnalyze_ValidList() {
        assertTrue(analyzer.analyze(records));
    }

    @Test
    public void testAnalyze_EmptyList() {
        List<LogRecord> logRecords = new ArrayList<>();
        assertFalse(analyzer.analyze(logRecords));
    }

    @Test
    public void testReport_withoutAnalysis() {
        assertEquals("Анализ данных не был проведён.", analyzer.report("format", "path"));
        analyzer.analyze(new ArrayList<>());
        assertEquals("Анализ данных не был проведён.", analyzer.report("format", "path"));
    }

    @Test
    public void testReport_withAnalysis() {
        analyzer.analyze(records);
        LogAnalyzer.StringArrayMaker maker = analyzer. new StringArrayMaker();
        ReportFormatter formatter = new ReportFormatter(maker.generalInfo("path"),
            maker.requestedResources(), maker.responseCodes(), maker.addressesRequestSources());
        assertEquals(formatter.formReport("format"), analyzer.report("format", "path"));
        assertEquals(formatter.formReport("markdown"), analyzer.report("markdown", "path"));
        assertEquals(formatter.formReport("adoc"), analyzer.report("adoc", "path"));
    }

    @Test
    public void testGetPercentile_ValidPercentile() {
        assertEquals(555, analyzer.getPercentile(0.5, records));
        assertEquals(333, analyzer.getPercentile(0.2, records));
        assertEquals(333, analyzer.getPercentile(0, records));
        assertEquals(777, analyzer.getPercentile(1, records));
    }

    @Test
    public void testGetPercentile_OutOfBoundsPercentile() {
        assertEquals(0, analyzer.getPercentile(-0.1, records));
        assertEquals(0, analyzer.getPercentile(1.1, records));
    }

    @Test
    public void testGetPercentile_EmptyList() {
        List<LogRecord> logRecords = new ArrayList<>();
        assertEquals(0, analyzer.getPercentile(0.5, logRecords));
    }
}
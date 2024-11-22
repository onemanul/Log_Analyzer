package backendacademy.analyzer;

import backendacademy.analyzer.fileParserClasses.LogFileProcessor;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class MainTest {/*
    @Test
    public void testCheckDateInput_correctInput() {
        assertTrue(Main.checkDateInput("2023-03-15"));
        assertTrue(Main.checkDateInput("2100-01-01"));
        assertTrue(Main.checkDateInput("1900-01-01"));
    }

    @Test
    public void testCheckDateInput_incorrectInput() {
        assertFalse(Main.checkDateInput("2023-02-30"));
        assertFalse(Main.checkDateInput("2023-13-10"));
        assertFalse(Main.checkDateInput(" 2023-03-15 "));
        assertFalse(Main.checkDateInput("15/03/2023"));
        assertFalse(Main.checkDateInput(""));
        assertFalse(Main.checkDateInput("Hello World"));
    }

    @Test
    public void testAnalyzeLogs_ValidInput() {
       // String path = "https://raw.githubusercontent.com/elastic/examples/master/Common%20Data%20Formats/nginx_logs/nginx_logs";
        LocalDate fromDate = null;
        LocalDate toDate = null;
        String format = "adoc";
        assertTrue(Main.analyzeLogs(path, fromDate, toDate, format));


        String path = "URL_copy.txt";
        assertEquals(51462, LogFileProcessor.getLogRecords(path).size());
    }

    @Test
    public void testAnalyzeLogs_InvalidPath() {
        String path = "invalid/path/to/logfile.log";
        LocalDate fromDate = null;
        LocalDate toDate = null;
        String format = "adoc";
        assertFalse(Main.analyzeLogs(path, fromDate, toDate, format));
    }

    @Test
    public void testAnalyzeLogs_NoRecordsInDateRange() {
        String path = "https://raw.githubusercontent.com/elastic/examples/master/Common%20Data%20Formats/nginx_logs/nginx_logs";
        LocalDate fromDate = LocalDate.of(2023, 1, 1);
        LocalDate toDate = LocalDate.of(2022, 1, 1);
        String format = "adoc";
        assertFalse(Main.analyzeLogs(path, fromDate, toDate, format));
    }

    @Test
    public void testAnalyzeLogs_NullValues() {
        String path = null;
        LocalDate fromDate = null;
        LocalDate toDate = null;
        String format = null;
        assertFalse(Main.analyzeLogs(path, fromDate, toDate, format));
    }*/
}

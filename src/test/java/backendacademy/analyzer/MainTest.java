package backendacademy.analyzer;

import backendacademy.analyzer.fileParserClasses.LogParser;
import org.junit.jupiter.api.Test;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    @Test
    public void testLocalDateConverter_correctInput() {
        Main.LocalDateConverter converter = new Main.LocalDateConverter();
        assertEquals(LocalDate.of(2023,03,15), converter.convert("2023-03-15"));
        assertEquals(LocalDate.of(2100,01,01), converter.convert("2100-01-01"));
        assertEquals(LocalDate.of(1900,01,01), converter.convert("1900-01-01"));
    }

    @Test
    public void testLocalDateConverter_incorrectInput() {
        Main.LocalDateConverter converter = new Main.LocalDateConverter();
        assertNull(converter.convert("2023-02-30"));
        assertNull(converter.convert("2023-13-10"));
        assertNull(converter.convert(" 2023-03-15 "));
        assertNull(converter.convert("15/03/2023"));
        assertNull(converter.convert(""));
        assertNull(converter.convert("Hello World"));
    }

    @Test
    public void testReadLogs_ValidPath() {
        String[] args = {
            "--path", "URL_copy.txt"
        };
        Main.main(args);
        assertTrue(Main.readLogs().isPresent());
        assertEquals(51462, Main.readLogs().get().size());
    }

    @Test
    public void testReadLogs_InvalidPath() {
        String[] args = {
            "--path", "invalid/path/to/logfile.log",
            "--format", "adoc"
        };
        Main.main(args);
        assertTrue(Main.readLogs().isEmpty());
    }

    @Test
    public void testAnalyzeLogs_ValidList() throws IOException {
        String[] args = {
            "--path", "log_1.txt",
            "--from", "2015-05-17",
            "--format", "markdown"
        };
        Main.main(args);
        String logRecord = "185.40.8.59 - - [04/Jun/2015:03:06:02 +0000] \"GET /downloads/product_2 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.21)\"\n";
        List<LogRecord> record = LogParser.makeRecordList(new BufferedReader(new StringReader(logRecord)));
        LogAnalyzer analyzer = new LogAnalyzer();
        analyzer.analyze(record);
        assertEquals(analyzer.report("log_1.txt", LocalDate.of(2015,05,17), null, "markdown"),
                    Main.analyzeLogs(Main.readLogs().get()));
    }

    @Test
    public void testAnalyzeLogs_NoRecordsInDateRange() {
        String[] args = {
            "--path", "URL_copy.txt",
            "--from", "2023-01-01",
            "--to", "2022-01-01",
            "--format", "adoc"
        };
        Main.main(args);
        assertEquals("Нет данных для анализа.", Main.analyzeLogs(Main.readLogs().get()));
    }

    @Test
    public void testAnalyzeLogs_EmptyValue() {
        assertEquals("Нет данных для анализа.", Main.analyzeLogs(new ArrayList<>()));
    }
}

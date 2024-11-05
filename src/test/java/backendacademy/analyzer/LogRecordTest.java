package backendacademy.analyzer;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LogRecordTest {
    @Test
    public void testRecordCreation() {
        LogRecord record = new LogRecord("185.40.8.59", LocalDate.parse("2019-05-25"),
            "/downloads/product_1", 100, 123);
        assertEquals("185.40.8.59", record.remoteAddr());
        assertEquals(LocalDate.parse("2019-05-25"), record.date());
        assertEquals("/downloads/product_1", record.requestPath());
        assertEquals(100, record.status());
        assertEquals(123, record.bodyBytesSent());
    }

    @Test
    public void testEqualsAndHashCode() {
        LogRecord record1 = new LogRecord("185.40.8.59", LocalDate.parse("2019-05-25"),
            "/downloads/product_1", 100, 123);
        LogRecord record2 = new LogRecord("185.40.8.59", LocalDate.parse("2019-05-25"),
            "/downloads/product_1", 100, 123);
        LogRecord record3 = new LogRecord("201.14.105.89", LocalDate.parse("2008-09-01"),
            "/downloads/something", 500, 321);
        assertEquals(record1, record2);
        assertNotEquals(record1, record3);
        assertEquals(record1.hashCode(), record2.hashCode());
    }

    @Test
    public void testToString() {
        LogRecord record = new LogRecord("185.40.8.59", LocalDate.parse("2019-05-25"),
            "/downloads/product_1", 100, 123);
        assertEquals("LogRecord[remoteAddr=185.40.8.59, date=2019-05-25, requestPath=/downloads/product_1, " +
            "status=100, bodyBytesSent=123]", record.toString());
    }

}

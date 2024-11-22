package backendacademy.analyzer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LogFilter {
    private LogFilter() {}

    public static List<LogRecord> filter(List<LogRecord> recordList, LocalDate from, LocalDate to) {
        LogFilter logFilter = new LogFilter();
        return logFilter.filterByDate(recordList, from, to);
    }

    public List<LogRecord> filterByDate(List<LogRecord> recordList, LocalDate from, LocalDate to) {
        List<LogRecord> filteredRecords = new ArrayList<>();
        for (LogRecord logRecord : recordList) {
            boolean isAfterFrom = (from == null || logRecord.date().isAfter(from) || logRecord.date().isEqual(from));
            boolean isBeforeTo = (to == null || logRecord.date().isBefore(to));
            if (isAfterFrom && isBeforeTo) {
                filteredRecords.add(logRecord);
            }
        }
        return filteredRecords;
    }
}

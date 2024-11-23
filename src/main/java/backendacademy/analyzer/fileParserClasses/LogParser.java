package backendacademy.analyzer.fileParserClasses;

import backendacademy.analyzer.LogAnalyze;
import backendacademy.analyzer.LogRecord;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogParser {
    private LogParser() {}

    public static void addRecordsToAnalyze(BufferedReader reader, LogAnalyze analyze) throws IOException {
        LogParser adder = new LogParser();
        adder.addRecords(reader, analyze);
    }

    public static Optional<LogRecord> parseLogLine(String recordLine) {
        LogParser parser = new LogParser();
        return parser.transformLogLine(recordLine);
    }

    private void addRecords(BufferedReader reader, LogAnalyze analyze) throws IOException {
        String recordLine;
        Optional<LogRecord> optLogRecord;
        while ((recordLine = reader.readLine()) != null) {
            optLogRecord = transformLogLine(recordLine);
            optLogRecord.ifPresent(analyze::addRecord);
        }
    }

    private Optional<LogRecord> transformLogLine(String recordLine) {
        String logPattern = "^(?<remoteAddr>[^ ]+) - (?<remoteUser>[^ ]+) "
            + "\\[(?<dateLocal>[^:]+)[^\\]]+\\] \\\"(?<requestType>[A-Z]+) (?<requestPath>[^ ]+) "
            + "(?<requestProtocol>[^\\\"]+)\\\" (?<status>[1-5]\\d{2}) (?<bodyBytesSent>\\d+) "
            + "\\\"(?<httpReferer>[^\\\"]+)\\\" \\\"(?<httpUserAgent>[^\\\"]+)\\\"$";
        Pattern pattern = Pattern.compile(logPattern);
        Matcher matcher = pattern.matcher(recordLine);

        if (matcher.find()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy", Locale.ENGLISH);
            LocalDate dateLocal = LocalDate.parse(matcher.group("dateLocal"), formatter);
            return Optional.of(new LogRecord(matcher.group("remoteAddr"), dateLocal,
                matcher.group("requestPath"), Integer.parseInt(matcher.group("status")),
                Long.parseLong(matcher.group("bodyBytesSent"))));
        }
        return Optional.empty();
    }
}

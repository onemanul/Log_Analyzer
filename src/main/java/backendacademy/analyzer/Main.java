package backendacademy.analyzer;

import backendacademy.analyzer.fileParserClasses.LogFileProcessor;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import backendacademy.analyzer.fileParserClasses.LogFilter;
import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

public class Main {
    @Parameter(names = "--path", required = true, description = "<путь к лог-файлам>")
    private static String path;
    @Parameter(names = "--from", converter = LocalDateConverter.class, description = "<дата> (включена в диапазон)")
    private static LocalDate from;
    @Parameter(names = "--to", converter = LocalDateConverter.class, description = "<дата> (не включена в диапазон)")
    private static LocalDate to;
    @Parameter(names = "--format", description = "<markdown|adoc>")
    private static String format = "markdown";

    private static final PrintStream OUTPUT = System.out;

    public static void main(String[] args) {
        JCommander jCommander = JCommander.newBuilder().addObject(new Main()).build();
        try {
            jCommander.parse(args);
        } catch (ParameterException e) {
            jCommander.usage();
            return;
        }

       /* OUTPUT.println("Path: " + path);
        OUTPUT.println("From: " + from);
        OUTPUT.println("To: " + to);
        OUTPUT.println("Format: " + format);*/

        Optional<List<LogRecord>> optRecordList = readLogs();
        if (optRecordList.isEmpty()) {
            OUTPUT.println("Ошибка в чтении файла.");
        } else {
            OUTPUT.println(analyzeLogs(optRecordList.get()));
        }
    }

    public static Optional<List<LogRecord>> readLogs() {
        try {
            return Optional.of(LogFileProcessor.getLogRecords(path));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static String analyzeLogs(List<LogRecord> recordList) {
        LogAnalyzer analyzer = new LogAnalyzer();
        if (analyzer.analyze(LogFilter.filter(recordList, from, to))) {
            return analyzer.report(path, from, to, format);
        } else {
            return "Нет данных для анализа.";
        }
    }

    public static class LocalDateConverter implements IStringConverter<LocalDate> {
        @Override
        public LocalDate convert(String value) {
            try {
                return LocalDate.parse(value);
            } catch (Exception e) {
                OUTPUT.println("Недопустимый формат даты. Требуемый формат yyyy-MM-dd");
                return null;
            }
        }
    }
}

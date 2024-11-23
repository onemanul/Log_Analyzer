package backendacademy.analyzer;

import backendacademy.analyzer.fileParserClasses.LogFileProcessor;
import backendacademy.analyzer.reportClasses.LogReport;
import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.Optional;

//CHECKSTYLE:OFF
public class Main {
//CHECKSTYLE:ON
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

        Optional<LogAnalyze> optLogAnalyze = readLogs();
        if (optLogAnalyze.isEmpty()) {
            OUTPUT.println("Ошибка в чтении файла.");
        } else {
            optLogAnalyze.get().finishAnalyze();
            OUTPUT.println(LogReport.report(optLogAnalyze.get(), path, format));
        }
    }

    public static Optional<LogAnalyze> readLogs() {
        try {
            return Optional.of(LogFileProcessor.getLogRecords(path, from, to));
        } catch (Exception e) {
            return Optional.empty();
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
